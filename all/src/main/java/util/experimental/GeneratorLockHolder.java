package util.experimental;

import util.Collection;

final class GeneratorLockHolder {
    public static final Collection<GeneratorFunction<?>, Thread> holder = new Collection<>();
}
