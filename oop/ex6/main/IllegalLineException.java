package oop.ex6.main;

public class IllegalLineException extends FileException{
    private static final String BAD_ASSIGMENT = "Illegal line";

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
