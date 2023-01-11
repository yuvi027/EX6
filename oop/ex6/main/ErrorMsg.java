package oop.ex6.main;

public enum ErrorMsg {
    INVALID_FILE("The file given does not exist");

    ErrorMsg(String s) {
        msg = s;
    }
    private String msg;

}
