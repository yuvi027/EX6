package oop.ex6.main;

public class IllegalVariableException extends FileException {
    private final String ILLEGAL_VARIABLE = "Variable is not legal";

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return ILLEGAL_VARIABLE;
    }
}
