package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {

    private static final int LEGAL = 0;
    private static final int FILE_NAME_IND = 2;
    private static final int NUM_OF_ARGS = 3;


    public static void main(String[] args){
        CodeChecker  checker = CodeChecker.getInstance();
        //int num = checker.checkCode(args[FILE_NAME_IND]);
        Pattern fileEndingPattern = Pattern.compile("\\.sjava$");
        Matcher fileHasCorrectEnding = fileEndingPattern.matcher(args[FILE_NAME_IND]);
        int num = checker.checkCode("oop/ex6/main/test2.txt");
        if(args.length != NUM_OF_ARGS){
            System.err.println("Wrong parameters number!");
            return;
        }
        else if(!fileHasCorrectEnding.find()){
            System.err.println("Wrong file name!");
            return;
        }

        if (num != LEGAL){
            System.err.println(checker.getErr());
        }
        else{
            System.out.println(num);
        }

    }
}
