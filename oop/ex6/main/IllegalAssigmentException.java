package oop.ex6.main;

public class IllegalAssigmentException extends FileException {
    private static final String BAD_ASSIGMENT = "Can't assign to this variable the given value";

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return BAD_ASSIGMENT;
    }
}
