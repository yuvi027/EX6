package oop.ex6.main;

public class NotFoundException extends FileException {
    private final String NOT_FOUND = "Name does not found";
    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return NOT_FOUND;
    }
}
