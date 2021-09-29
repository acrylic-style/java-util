package util.function.generator;

import java.util.HashMap;

final class GeneratorLockHolder {
    static final HashMap<GeneratorFunction<?>, Thread> holder = new HashMap<>();
}
