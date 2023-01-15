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
    private static final String INT_CORRECT_FORM_REGEX = "^(?:(?:.*?)\\s*(?:=\\s*\\d+)?,?)+?;$";
    private static final String INT_REGEX = "^((?:(?:.*?)\\s*(?:=\\s*(\\d+)))?,?)+?;$";

    private static String error;
    private static ArrayList<String> linesOfFile;
    private static CodeChecker codeChecker;
    private static Pattern oneLinerComment;
    private static Pattern emptyLinePattern;
    private static Pattern illegalVariableName;
    private static int index;

    //saves the scope, and a hashmap with all the scope's variables name and their type
    private HashMap<Integer, HashMap<String, Var>> variables;
    private HashMap<String, HashMap<String, Var>> methods;


    private CodeChecker() {
        linesOfFile = new ArrayList<>();
        oneLinerComment = Pattern.compile(COMMENT_REGEX);
        emptyLinePattern = Pattern.compile(BLANK_LINE_REGEX);
        illegalVariableName = Pattern.compile(ILLEGAL_NAME_REGEX);
        index = 1;
        variables = new HashMap<>();
        //intPattern = Pattern.compile();
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
            variables.put(index, new HashMap<>());
            for (String line : linesOfFile) {
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
    }

    private int compileLine(String line) throws Exception {
        if (line.charAt(line.length()) == ';') {
            String[] words = line.split(" ");
            for (String type : typesOfVariables) {
                if (type.equals(words[0])) return compileVariable(line, words[0], variables.get(index));
            }
            if (variables.get(index).containsKey(words[0])) {
                return checkVarValue();
            }
            return ILLEGAL;
        } else if (line.charAt(line.length()) == '{') {
            variables.put(++index, new HashMap<>());
            return compileMethod();
        } else if (line.charAt(line.length()) == '}') {
            variables.remove(index--);
            return LEGAL;
        }
        return ILLEGAL;
    }

    private int compileMethod() {
        return 0;
    }

    private int checkVarValue() {
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


    //int a = 1, b = 7, double = 40;
    //char a = 5+3;
    private int compileVariable(String variable, String type, HashMap<String, Var> variablesMap) {
        String[] tokenOfVariable = variable.split(",");
        if (tokenOfVariable[1].matches(ILLEGAL_NAME_REGEX)) {
            return ILLEGAL;
        } else {
            //TODO: finish
            for (String token : tokenOfVariable) {
                if (variablesMap.containsKey(token)) {
                    if (!variablesMap.get(token).equals(type))
                        return ILLEGAL;
                    else {
                        //Check if valid
                    }
                } else {
                    //if valid then
                    Pattern ifInitialization = Pattern.compile("=");
                    Matcher initializationMatcher = ifInitialization.matcher(token);
                    if (initializationMatcher.find()) {
                        switch (type) {
                            case "int":
                                //Check if int is legal
                                if (!token.matches(INT_REGEX)) {
                                    return ILLEGAL;
                                }
                                break;
                            case "double":
                                //check if double is legal
                                break;
                            case "String":
                                //check if String is legal
                                break;
                            case "boolean":
                                //check if boolean is legal
                                break;
                            case "char":
                                //check if boolean is legal
                                break;
                        }
                    }

                    variablesMap.put(token, type);
                    //else return INVALID
                }

            }
        }
        return ILLEGAL;
    }
}
