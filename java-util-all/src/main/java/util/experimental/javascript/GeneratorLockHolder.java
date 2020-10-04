package util.experimental.javascript;

import util.Collection;

final class GeneratorLockHolder {
    public static final Collection<GeneratorFunction<?>, Thread> holder = new Collection<>();
}
