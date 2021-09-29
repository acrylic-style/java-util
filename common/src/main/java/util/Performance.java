package util;

import org.jetbrains.annotations.NotNull;

/**
 * @deprecated will be removed in 0.17, you should find a better way to measure performance instead of using this
 */
@Deprecated
public class Performance {
    private long start = -1;
    private long end = -1;
    private final PerformanceUnit unit;

    public Performance(@NotNull PerformanceUnit unit) {
        Validate.notNull(unit, "PerformanceUnit cannot be null");
        this.unit = unit;
    }

    public void start() {
        if (unit == PerformanceUnit.SECONDS || unit == PerformanceUnit.MILLISECONDS) {
            this.start = System.currentTimeMillis();
        } else if (unit == PerformanceUnit.NANOSECONDS) {
            this.start = System.nanoTime();
        }
    }

    public void end() {
        if (unit == PerformanceUnit.SECONDS || unit == PerformanceUnit.MILLISECONDS) {
            this.end = System.currentTimeMillis();
        } else if (unit == PerformanceUnit.NANOSECONDS) {
            this.end = System.nanoTime();
        }
    }

    public void endAndPrint(String name) {
        end();
        System.out.println(name + " took " + toString() + " to complete");
    }

    public void endAndPrintIf(long endTime, String name) {
        end();
        if ((end-start) >= endTime) {
            System.out.println(name + " took " + toString() + " to complete");
        }
    }

    public long getTime() {
        return this.end-this.start;
    }

    @Override
    public String toString() {
        long time = getTime();
        if (unit == PerformanceUnit.SECONDS) {
            return ((float) time / 1000F) + " seconds";
        } else if (unit == PerformanceUnit.MILLISECONDS) {
            return time + "ms";
        } else if (unit == PerformanceUnit.NANOSECONDS) {
            return time + "ns";
        }
        return null;
    }

    public enum PerformanceUnit {
        SECONDS,
        MILLISECONDS,
        NANOSECONDS,
    }
}

