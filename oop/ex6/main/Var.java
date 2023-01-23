package oop.ex6.main;

public class Var {
    private String name;
    private String type;
    private boolean value;
    private boolean finalValue;

    public Var(String name, String type, boolean value, boolean finalValue){
        this.name = name;
        this.type = type;
        this.value = value;
        this.finalValue = finalValue;
    }
    public Var(String name, String type){
        this.name = name;
        this.type = type;
        this.finalValue = false;
        this.value = false;
    }
    public String getName(){return this.name;}

    public String getType(){return this.type;}

    public boolean Initiated(){return this.value;}

    public boolean isFinal(){return this.finalValue;}

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     * @apiNote In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * The string output is not necessarily stable over time or across
     * JVM invocations.
     * @implSpec The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     */
    @Override
    public String toString() {
        return "name: " +  name + " type: " + type + " is final: " + isFinal();
    }
}
