package oop.ex6.main;

public class IncorrectTypeException extends IllegalArgumentException{
    public IncorrectTypeException(){
        super("This is an illegal variable type");
    }
}
