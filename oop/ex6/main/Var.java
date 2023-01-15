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
    public Var(String name, String type, boolean finalValue){
        this.name = name;
        this.type = type;
        this.finalValue = finalValue;
        this.value = false;
    }
    public String getName(){return this.name;}

    public String getType(){return this.type;}

    public boolean Initiated(){return this.value;}
}
