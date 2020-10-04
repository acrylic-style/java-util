// Credit: https://github.com/reinoutkorbee
// https://github.com/reinoutkorbee/FileBasedCollection

package util.file;

import org.jetbrains.annotations.NotNull;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;
import util.AbstractCollectionList;
import util.ICollectionList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This list is almost same as {@link util.CollectionList CollectionList},
 * but it may be slow as it saves the data to the disk. (unless you're using ssd)
 * This list saves your memory and prevents {@link OutOfMemoryError} when storing
 * the many entries into the list. The list data will be saved at the tmp directory
 * and will be removed when VM exits or the {@link #close()} was called.
 */
@SuppressWarnings({ "ConstantConditions", "unused", "RedundantSuppression" })
public class FileBasedCollectionList<E extends Serializable> extends AbstractCollectionList<E> implements ICollectionList<E>, Cloneable, AutoCloseable {
    /**
     * Keeps track of the number of elements in this collection.
     */
    private long size = 0;

    /**
     * The chunkSize determines the number of elements that a chunk can contain before being cached.
     */
    private int chunkSize;

    /**
     * A chunk contains a block of elements.
     */
    private ArrayList<E> currentChunk;

    /**
     * Keeps track of the real size of the current chunk
     */
    private int currentChunkSize;

    private Store<E> store;

    private static final int CHUNK_SIZE = 100;

    @SafeVarargs
    public FileBasedCollectionList(E... e) {
        this(CHUNK_SIZE);
        this.addAll(Arrays.asList(e));
    }

    public FileBasedCollectionList(Collection<? extends E> e) {
        this(CHUNK_SIZE);
        this.addAll(e);
    }

    /**
     * Default constructor
     */
    public FileBasedCollectionList() {
        this(CHUNK_SIZE);
    }

    /**
     * Constructor to set the chunk size to a custom value.
     *
     * @param chunkSize The size of each chunk.
     */
    public FileBasedCollectionList(int chunkSize) {
        this.store = new Store<>();
        this.chunkSize = chunkSize;
        this.currentChunk = new ArrayList<>();

        /*
         *  add shutdown hook to clear the cache: threads are initialized only when they are called, ie. on JVM shutdown so this
         *  doesn't impact the run-time performance
         *
         *  the close() method should be called by the application, but if that fails, the
         *  shutdown hook is there as last rescue
         */
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @SafeVarargs
    @Override
    @NotNull
    public final FileBasedCollectionList<E> concat(ICollectionList<E>... lists) {
        return (FileBasedCollectionList<E>) super.concat(lists);
    }

    /**
     * Call this method to shutdown the file streams orderly after the collection isn't needed anymore.
     * The FileBasedCollection on which this method is called is unusable afterwards.
     */
    @Override
    public synchronized void close() {
        if (store != null) {
            store.close();
            store = null;
            currentChunk = null;
        }
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    @SuppressWarnings("unchecked")
    public <V extends Serializable & Comparable<V>> void sort(int bucketSize) throws IOException {
        FileBasedCollectionList<V> sorted = new FileBasedCollectionSorter().sort((FileBasedCollectionList<V>) this, bucketSize, chunkSize);
        swap((FileBasedCollectionList<E>) sorted);
    }

    public void sort() throws IOException {
        sort(10 * chunkSize);
    }

    /**
     * The iterator always starts at the first element of the collection.
     */
    @Override
    public @NotNull FileBasedIterator<E> iterator() {
        // if there is a non-empty chunk that is not cached yet, then put it in the cache before iteration starts
        if(!currentChunk.isEmpty()) {
            flush();
        }
        return new FileBasedIterator<>(store.getReader());
    }

    /**
     * Default size() implementation which down casts the real size (long) to an int for interface compatibility.
     * This number is unreliable for very large collections.
     */
    @Override
    public int size() { return (int) size; }

    /**
     * This method is useful if the collection is very large because then the default size() implementation
     * down casts to an int and looses information.
     *
     * @return The real size of this collection as long
     */
    public long getRealSize() { return size; }

    /**
     * The removeAll is implemented as a copy operation. All items in this collection not
     * in the provided collection c, are copied to a new collection. The cache of the current
     * collection is replaced with the cache of the new collection.
     */
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if (c == null || c.isEmpty()) {
            return false;
        }
        if (c == this) {
            clear();
            return true;
        }
        Predicate<E> condition = n -> !c.contains(n);
        return retainAll(condition);
    }

    @Override
    public boolean contains(Object o) {
        try (FileBasedIterator<E> it = iterator()) {
            if (o == null) {
                while (it.hasNext())
                    if (it.next() == null) return true;
            } else {
                while (it.hasNext())
                    if (o.equals(it.next()))
                        return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * The retainAll is implemented as a copy operation. All items in this collection also
     * in the provided collection c, are copied to a new collection. The cache of the current
     * collection is replaced with the cache of the new collection.
     */
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if(c == null || c == this) {
            return false;
        }
        if(c.isEmpty()) {
            clear();
            return true;
        }
        Predicate<E> condition = c::contains;
        return retainAll(condition);
    }

    private boolean retainAll(Predicate<E> condition) {
        FileBasedCollectionList<E> modifiedCollection = new FileBasedCollectionList<>(chunkSize);

        try(FileBasedIterator<E> iter = iterator()) {
            while(iter.hasNext()) {
                E next = iter.next();
                if(condition.test(next)) {
                    modifiedCollection.add(next);
                }
            }
        } catch (IOException e) {
            close();
            modifiedCollection.close();
            throw new RuntimeException(e);
        }
        boolean isModified = modifiedCollection.getRealSize() < size;
        swap(modifiedCollection);
        return isModified;
    }

    @Override
    public boolean add(E e) {
        return add(e, 1);
    }

    /**
     * This method calls {@link #add(E)} with an addition parameter size to
     * specify the size of the element that is added.
     */
    public boolean add(E e, int size) {
        currentChunk.add(e);
        currentChunkSize += size;
        if(currentChunkSize >= chunkSize) {
            flush();
        }
        this.size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        if (c instanceof FileBasedCollectionList) {
            FileBasedCollectionList<? extends E> other = (FileBasedCollectionList<? extends E>) c;
            boolean modified = false;
            try (FileBasedIterator<? extends E> iter = other.iterator()) {
                while (iter.hasNext()) {
                    E next = iter.next();
                    if (add(next)) {
                        modified = true;
                    }
                }
            } catch (IOException e) {
                other.close();
                close();
                throw new RuntimeException(e);
            }
            return modified;
        } else {
            c.forEach(this::add);
            return true;
        }
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) { throw new UnsupportedOperationException(); }

    @Override
    public Object[] toArray() { throw new UnsupportedOperationException("toArray"); }

    @Override
    public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException("toArray"); }

    @Override
    public @NotNull FileBasedCollectionList<E> newList() { return new FileBasedCollectionList<>(); }

    @Override
    public @NotNull FileBasedCollectionList<E> newList(Collection<? extends E> list) {
        return new FileBasedCollectionList<>(list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull FileBasedCollectionList<?> createList() {
        return new FileBasedCollectionList<>();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().filter(this::contains).count() == c.size();
    }

    @Override
    public void clear() {
        swap(new FileBasedCollectionList<>(chunkSize));
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Flush writes the current chunk to disk and creates a new, empty chunk.
     * The FileBasedCollection is not thread-safe so calling this could interfere with add(E).
     */
    public void flush() {
        if(!currentChunk.isEmpty()) {
            store.write(currentChunk);
            currentChunk = new ArrayList<>(chunkSize);
            currentChunkSize = 0;
        }
    }

    private void swap(FileBasedCollectionList<E> other) {
        close();
        this.store = other.store;
        this.currentChunk = other.currentChunk;
        this.size = other.size;
        this.chunkSize = other.chunkSize;
    }

    @SuppressWarnings("RedundantOperationOnEmptyContainer")
    public static class FileBasedIterator<E extends Serializable> implements Iterator<E>, Closeable {
        private final ChunkReader<E> reader;

        private ArrayList<E> currentChunk = new ArrayList<>();
        private Iterator<E> chunkIterator = currentChunk.iterator();
        private boolean finished = false;

        FileBasedIterator(ChunkReader<E> chunkReader) {
            reader = chunkReader;
        }

        @Override
        public boolean hasNext() {
            if (isCurrentChunkExhausted() && !isFinished()) {
                readNextChunk();
            }

            return chunkIterator.hasNext();
        }

        @Override
        public E next() {
            if (isCurrentChunkExhausted() && !isFinished()) {
                readNextChunk();
            }
            return chunkIterator.next();
        }

        private boolean isCurrentChunkExhausted() {
            return !chunkIterator.hasNext();
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean isFinished() {
            return finished;
        }

        private void readNextChunk() {
            Optional<ArrayList<E>> newChunk = reader.readChunk();
            if (newChunk.isPresent()) {
                currentChunk = newChunk.get();
            } else {
                currentChunk = new ArrayList<>();
                finished = true;
            }
            chunkIterator = currentChunk.iterator();
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static class Store<E extends Serializable> implements Closeable {
        private final File file;
        private final ObjectOutputStream outputStream;

        Store() {
            try {
                file = createTmpFileForFileBasedCollection();
                // BufferedOutputStream slightly improves performance when applied in this order: ObjectOutputStream -> BufferedOutputStream -> SnappyOutputStream -> FileOutputStream
                outputStream = new ObjectOutputStream(new BufferedOutputStream(new SnappyOutputStream(new FileOutputStream(file))));
                outputStream.flush(); // write ObjectOutputStream headers into file
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private File createTmpFileForFileBasedCollection() {
            String tmpDir = System.getProperty("java.io.tmpdir");
            String uniqueFileName = "FileBasedCollectionList-";
            String stamp = UUID.randomUUID().toString();
            String extension = ".bin";
            File file = new File(tmpDir, uniqueFileName + stamp + extension);
            file.deleteOnExit();
            return file;
        }

        @Override
        public void close() {
            try {
                outputStream.close();
                file.delete();
            } catch(IOException ioe) {
                // ignore
            }
        }

        void write(ArrayList<E> chunk) {
            try {
                outputStream.writeObject(chunk);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ChunkReader<E> getReader() {
            try {
                // BufferedInputStream slightly improves performance when applied in this order: FileInputStream -> SnappyInputStream -> BufferedInputStream -> ObjectInputStream
                return new ChunkReader<>(new ObjectInputStream(new BufferedInputStream(new SnappyInputStream(new FileInputStream(file)))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ChunkReader<E> implements Closeable {
        private final ObjectInputStream inputStream;

        ChunkReader(ObjectInputStream inputStream) {
            this.inputStream = inputStream;
        }

        Optional<ArrayList<E>> readChunk() {
            try {
                @SuppressWarnings("unchecked")
                ArrayList<E> chunk = (ArrayList<E>) inputStream.readObject();
                return Optional.of(chunk);
            } catch (EOFException e) {
                return Optional.empty();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }

    private static class FileBasedCollectionSorter {

        public <E extends Serializable & Comparable<E>> FileBasedCollectionList<E> sort(
                FileBasedCollectionList<E> source,
                final int bucketSize,
                final int chunkSize) throws IOException {
            List<FileBasedCollectionList<E>> buckets = new ArrayList<>();
            SortedSet<E> sorted = new TreeSet<>();
            try (FileBasedCollectionList.FileBasedIterator<E> iterator = source.iterator()) {
                while (iterator.hasNext()){
                    if (sorted.size() < bucketSize) {
                        sorted.add(iterator.next());
                    } else {
                        buckets.add(createBucket(sorted, chunkSize));
                        sorted.clear();
                    }
                }
                if (!sorted.isEmpty()) buckets.add(createBucket(sorted, chunkSize));

            } finally {
                source.close();
            }

            return merge(buckets, source.getChunkSize());
        }

        private <E extends Serializable & Comparable<E>> FileBasedCollectionList<E> createBucket(SortedSet<E> sorted, final int chunkSize){
            FileBasedCollectionList<E> bucket = new FileBasedCollectionList<>(chunkSize);
            bucket.addAll(sorted);
            return bucket;
        }

        private <E extends Serializable & Comparable<E>> FileBasedCollectionList<E> merge(List<FileBasedCollectionList<E>> buckets, final int chunkSize) {
            FileBasedCollectionList<E> merged = new FileBasedCollectionList<>(chunkSize);

            List<FileBasedCollectionList.FileBasedIterator<E>> iterators = buckets.stream()
                    .map(FileBasedCollectionList::iterator)
                    .collect(Collectors.toList());

            List<E> heads = new ArrayList<>(buckets.size());
            for (FileBasedCollectionList.FileBasedIterator<E> iterator : iterators) {
                if(iterator.hasNext()){
                    heads.add(iterator.next());
                }else{
                    heads.add(null);
                }
            }

            while(hasMoreElements(heads)){
                E minValue = findMinValue(heads);
                merged.add(minValue);

                for(int i=0;i<heads.size();i++){
                    if(heads.get(i) != null && heads.get(i).equals(minValue)){
                        heads.set(i, getNextOrNull(iterators.get(i)));
                    }
                }
            }

            iterators.forEach(i -> { try { i.close(); } catch(IOException ioe) {  throw new RuntimeException(ioe); } });
            buckets.forEach(FileBasedCollectionList::close);

            return merged;
        }

        private <E extends Serializable & Comparable<E>> E getNextOrNull(FileBasedCollectionList.FileBasedIterator<E> iterator){
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return null;
        }

        private <E extends Serializable & Comparable<E>> boolean hasMoreElements(List<E> heads) {
            return heads.stream().anyMatch(Objects::nonNull);
        }

        private <E extends Serializable & Comparable<E>> E findMinValue(List<E> heads){
            E minValue = null;
            for(E head : heads){
                if(minValue == null || (head != null && head.compareTo(minValue) < 0)){
                    minValue = head;
                }
            }
            return minValue;
        }
    }

    @SafeVarargs
    @NotNull
    public static <E extends Serializable & Comparable<E>> FileBasedCollectionList<E> of(E... e) {
        return new FileBasedCollectionList<>(e);
    }
}
