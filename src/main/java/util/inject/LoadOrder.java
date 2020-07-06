package util.inject;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LoadOrder {
    LoadPriority value() default LoadPriority.NORMAL;

    enum LoadPriority {
        /**
         * Load priority is very low and should load first
         */
        LOWEST(1),
        LOW(2),
        NORMAL(3),
        HIGH(4),
        /**
         * Load priority is very high and should load last.
         */
        HIGHEST(5),
        ;

        private final int slot;

        LoadPriority(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }
}
