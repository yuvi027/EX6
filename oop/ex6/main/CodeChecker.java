package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CodeChecker {
    private static final int LEGAL = 0;
    private static final int ILLEGAL = 1;
    private static final int ERROR = 2;
    private static String error;
    private static ArrayList<String> linesOfFile;
    private static CodeChecker codeChecker;
    private CodeChecker() {
        linesOfFile = new ArrayList<>();
    }

    public static CodeChecker getInstance(){
        if(codeChecker == null){
            codeChecker =  new CodeChecker();
        }
        return codeChecker;
    }

    public int checkCode(String fileName){
        return fileOpener(fileName);
        //return 0;
    }

    private static int fileOpener(String fileName){
        try(FileReader reader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(reader)){
            String s;
            while((s = bufferedReader.readLine()) != null){
                linesOfFile.add(s);
            }
            System.out.println(linesOfFile.toString());
        }
        catch (IOException e){
            error = e.getMessage();
            return ERROR;
        }
        return LEGAL;
    }

    public String getErr() {
        return error;
    }
}
