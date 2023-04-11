package xyz.acrylicstyle.util.expression.instruction;

final class NoReturn extends RuntimeException {
    public static final RuntimeException INSTANCE = new NoReturn();
}
