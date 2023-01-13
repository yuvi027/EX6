package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeChecker {
    private static final int LEGAL = 0;
    private static final int ILLEGAL = 1;
    private static final int ERROR = 2;
    private static final String COMMENT_REGEX = "^//.*";
    private static final String BLANK_LINE_REGEX = "\\s*";
    private static final String[] typesOfVariables = {"int", "double", "String", "boolean", "char"};
    private static final String ILLEGAL_NAME_REGEX = "_|[0-9].*";

    private static String error;
    private static ArrayList<String> linesOfFile;
//    private static HashMap<>
    private static CodeChecker codeChecker;
    private static Pattern oneLinerComment;
    private static Pattern emptyLinePattern;
    private static Pattern illegalVariableName;


    private CodeChecker() {
        linesOfFile = new ArrayList<>();
        oneLinerComment = Pattern.compile(COMMENT_REGEX);
        emptyLinePattern = Pattern.compile(BLANK_LINE_REGEX);
        illegalVariableName = Pattern.compile(ILLEGAL_NAME_REGEX);
    }

    public static CodeChecker getInstance() {
        if (codeChecker == null) {
            codeChecker = new CodeChecker();
        }
        return codeChecker;
    }

    public int checkCode(String fileName) {
        try {
            fileOpener(fileName);
            for (String line :linesOfFile) {
                compileLine(line);
            }
        } catch (IOException e) {
            error = e.getMessage();
            return ERROR;
        } catch (Exception e) {
            error = e.getMessage();
            return ILLEGAL;
        }
        return LEGAL;
        //Matcher m = oneLinerComment.matcher("//This is a comment");
        //System.out.println(m.matches());
        //return 0;
    }

    private int compileLine(String line) throws Exception{

        return LEGAL;
    }

    private static int fileOpener(String fileName) throws IOException {
        try (FileReader reader = new FileReader(fileName);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String s;
            Matcher blankLineMatcher;
            Matcher commentMatcher;

            while ((s = bufferedReader.readLine()) != null) {
                blankLineMatcher = oneLinerComment.matcher(s);
                commentMatcher = emptyLinePattern.matcher(s);
                if (blankLineMatcher.matches() || commentMatcher.matches()) {
                    continue;
                }
                linesOfFile.add(s);
            }
            System.out.println(linesOfFile.toString());
        } catch (IOException e) {
            throw e;
        }
        return LEGAL;
    }

    public String getErr() {
        return error;
    }

    private int compileInt(String variable) {
        String[] tokenOfVariable = variable.split(" ");
        if (tokenOfVariable[1].matches(ILLEGAL_NAME_REGEX)) {
            return ILLEGAL;
        } else {
            //TODO: finish
        }
        return ILLEGAL;
    }
}
