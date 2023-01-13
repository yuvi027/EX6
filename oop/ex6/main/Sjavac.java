package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sjavac {

    private static final int LEGAL = 0;
    private static final int FILE_NAME_IND = 2;


    public static void main(String[] args){
        CodeChecker  checker = CodeChecker.getInstance();
        //int num = checker.checkCode(args[FILE_NAME_IND]);
        int num = checker.checkCode("oop/ex6/main/test2.txt");
        if (num != LEGAL){
            System.err.println(checker.getErr());
        }
        else{
            System.out.println(num);
        }

    }
}
