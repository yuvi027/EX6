package oop.ex6.main;

public class IllegalExpressionException extends FileException {
    private static final String ILLEGAL_EXPRESSION = "An illegal IF/WHILE exception";

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return ILLEGAL_EXPRESSION;
    }
}
