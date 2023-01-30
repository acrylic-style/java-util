package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class PerformanceCounter {
    private static final String ERROR_PREFIX = "[PERFORMANCE COUNTER STACK CORRUPTION] ";
    private final List<Map.Entry<Double, Double>> times = Collections.synchronizedList(new ArrayList<>());
    private final ThreadLocal<Double> startTime = new ThreadLocal<>();
    private final Unit unit;

    public PerformanceCounter(@NotNull Unit unit) {
        this.unit = unit;
    }

    @Contract(pure = true)
    public @NotNull Unit getUnit() {
        return unit;
    }

    public void recordStart() {
        if (startTime.get() != null) {
            throw new IllegalStateException(ERROR_PREFIX + "recordStart() called twice without calling recordEnd()");
        }
        startTime.set(getCurrentTime());
    }

    public void recordEnd() {
        if (startTime.get() == null) {
            throw new IllegalStateException(ERROR_PREFIX + "recordStart() was not called before calling recordEnd()");
        }
        times.add(new AbstractMap.SimpleImmutableEntry<>(startTime.get(), getCurrentTime()));
        startTime.set(null);
    }

    public @NotNull String getDetails(boolean multiline) {
        List<Map.Entry<Double, Double>> times = new ArrayList<>(this.times);
        List<Double> durations = new ArrayList<>();
        for (Map.Entry<Double, Double> time : times) {
            if (time.getValue() == null) {
                continue; // ignore incomplete entries
            }
            durations.add(time.getValue() - time.getKey());
        }
        double min = durations.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double avg = Math.round(durations.stream().mapToDouble(Double::doubleValue).average().orElse(0.0) * 1000.0) / 1000.0;
        double max = durations.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double[] confidenceInterval = calculateLowerUpperConfidenceBoundary95Percent(durations.stream().mapToDouble(Double::doubleValue).toArray());
        double lower = Math.round(confidenceInterval[0] * 1000.0) / 1000.0;
        double upper = Math.round(confidenceInterval[1] * 1000.0) / 1000.0;
        double error = Math.round((upper - lower) / 2.0 * 1000.0) / 1000.0;
        if (multiline) {
            return  "  average = " + avg + "±(95%) " + error + " " + unit.getName() + "/op\n" +
                    "  (min, avg, max) = (" + min + ", " + avg + ", " + max + ")\n" +
                    "  CI (95%) = [" + lower + ", " + upper + "]\n" +
                    "  count = " + durations.size();
        } else {
            return String.format("min: %s, avg: %s, max: %s, 95%% confidence interval: [%s, %s], error: %s", unit.format(min), unit.format(avg), unit.format(max), unit.format(lower), unit.format(upper), unit.format(error));
        }
    }

    private double getCurrentTime() {
        if (unit == Unit.MILLISECONDS) {
            return System.nanoTime() / 1000000.0;
        } else if (unit == Unit.NANOSECONDS) {
            return System.nanoTime();
        } else {
            throw new IllegalStateException("Unknown unit: " + unit);
        }
    }

    @Contract("_ -> new")
    private static double @NotNull [] calculateLowerUpperConfidenceBoundary95Percent(double @NotNull [] givenNumbers) {
        // calculate the mean value (= average)
        double sum = 0.0;
        for (double num : givenNumbers) {
            sum += num;
        }
        double mean = sum / givenNumbers.length;

        // calculate standard deviation
        double squaredDifferenceSum = 0.0;
        for (double num : givenNumbers) {
            squaredDifferenceSum += (num - mean) * (num - mean);
        }
        double variance = squaredDifferenceSum / givenNumbers.length;
        double standardDeviation = Math.sqrt(variance);

        // value for 95% confidence interval, source: https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps
        double confidenceLevel = 1.96;
        double temp = confidenceLevel * standardDeviation / Math.sqrt(givenNumbers.length);
        return new double[]{mean - temp, mean + temp};
    }

    public enum Unit {
        MILLISECONDS("ms"),
        NANOSECONDS("ns"),
        ;

        private final String name;

        Unit(@NotNull String name) {
            this.name = name;
        }

        @Contract(pure = true)
        public @NotNull String getName() {
            return name;
        }

        @Contract(pure = true)
        public @NotNull String format(@Nullable Object value) {
            return value + " " + name;
        }
    }
}
