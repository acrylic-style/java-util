package util.experimental;

public class GeneratorFunctionResult<T> {
    public final T value;
    public final boolean done;

    GeneratorFunctionResult(T value, boolean done) {
        this.value = value;
        this.done = done;
    }
}
