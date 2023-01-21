package oop.ex6.main;

public class IllegalNameException extends FileException {
    private final String ILLEGAL_NAME = "Illegal variable or method name";
    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return ILLEGAL_NAME;
    }
}
