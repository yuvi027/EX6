package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    private static boolean returned;

    //saves the scope, and a hashmap with all the scope's variables name and their type
    private HashMap<Integer, HashMap<String, Var>> variables;
    //TODO- FIX methods !!!!!!!!!!!!!!!!!!
    private HashMap<String, HashMap<String, ArrayList<Var>>> methods; //TODO: consider the relation between methods
    // and variables


    /**
     * Basic Singleton constructor for the class CodeChecker
     */
    private CodeChecker() {
        linesOfFile = new ArrayList<>();
        oneLinerComment = Pattern.compile(COMMENT_REGEX);
        emptyLinePattern = Pattern.compile(BLANK_LINE_REGEX);
        illegalVariableName = Pattern.compile(ILLEGAL_NAME_REGEX);
        index = 1;
        variables = new HashMap<>();
        methods = new HashMap<>();
        returned = false;
        //intPattern = Pattern.compile();
    }

    /**
     * @return the instance of CodeChecker we need to run the program, we only want one CodeChecker
     * for the entire program
     */
    public static CodeChecker getInstance() {
        if (codeChecker == null) {
            codeChecker = new CodeChecker();
        }
        return codeChecker;
    }

    /**
     * Gets a file, and operates on it to see if the code it contains is legal
     *
     * @param fileName the file we want to check if legal
     * @return 0 if the file is legal, 1 if not, and 2 in case of errors
     */
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
            //TODO: add different types of exceptions...
            error = e.getMessage();
            return ILLEGAL;
        }
        return LEGAL;
    }

    /**
     * This method gets a single line and checks if this line is legal or not
     *
     * @param line the line of code we are checking
     * @return 0 if the line is legal, 1 if not, and 2 in case of errors
     * @throws Exception
     */
    private int compileLine(String line) throws Exception {
        String[] words = line.split(" ");
        if (line.charAt(line.length()) == ';') { //Out of bounds
            if (words.length == 1 && words[0].equals("return") && index > 1) {
                //Changed to words[0] for readability
                //Checks return case
                returned = true;
                return LEGAL;
            }
            if (returned) return ILLEGAL;
            for (String type : typesOfVariables) {
                if (words[0].equals("final") && type.equals(words[0])) {
                    return compileVariable(line, words[0], variables.get(index), true);
                } else if (type.equals(words[0])) {
                    return compileVariable(line, words[0], variables.get(index), false);
                }
            }
            int num = index;
            while (num > 0) {
                if (variables.get(num).containsKey(words[0])) {
                    return checkVarValue(line);
                }
                num--;
            }
            num = index;
            while (num > 0) {
                if (methods.get(num).containsKey(words[0])) {
                    return checkFuncCall(line, methods.get(num).get(words[0]));
                }
                num--;
            }
            return ILLEGAL;
        } else if (line.charAt(line.length()) == '{') {
            switch (words[0]) {
                case "void":
                    if (index > 1) return ILLEGAL;
                    else {
                        variables.put(++index, new HashMap<>());
                        return compileMethod(line);
                    }
                case "if":
                    variables.put(++index, new HashMap<>());
                    return checkIfLegal(line);
                case "while":
                    variables.put(++index, new HashMap<>());
                    return checkWhileLegal(line);
            }
            return ILLEGAL;
        } else if (line.charAt(line.length()) == '}') {
            //TODO: check
            if (index == 1) return ILLEGAL;
            variables.remove(index--);
            returned = false;
            return LEGAL;
        }
        return ILLEGAL;
    }
//Check this function
    private int checkFuncCall(String line, ArrayList<Var> vars) {
        String[] words = line.split("()"); //TODO- check if there's better ways to split
        String[] params = words[1].split(",");
        if(params.length != vars.size()) return ILLEGAL; //Illegal number of arguments
        int num = index;
        while (num > 0) {
            for (String name : params) {
                if(!(name.split(" ")[0].equals("final") && variables.get(num).get(name).Initiated() && variables.get(num).get(name).isFinal())){
                    return ILLEGAL;
                }
                if (!(variables.get(num).containsKey(name) && variables.get(num).get(name).Initiated())){
                    return ILLEGAL;
                }
            }
        }
        return LEGAL;
    }

    /**
     * Checks if the while loop is legal
     *
     * @param line the line of code we are checking
     * @return 0 if the while loop is legal, 1 if not, and 2 in case of errors
     */

    private int checkWhileLegal(String line) {
        //Agam's function
        return LEGAL;
    }

    /**
     * Checks if the if statement is legal
     *
     * @param line the line of code we are checking
     * @return 0 if the if statement is legal, 1 if not, and 2 in case of errors
     */
    private int checkIfLegal(String line) {

        return LEGAL;
    }

    /**
     * Checks if a function is legal
     *
     * @param line the line of code we are checking
     * @return 0 if the function is legal, 1 if not, and 2 in case of errors
     */

    //When we check if a function is legal, we must check some things, one of them is to add only the LOCAL
    // variables to the dictionary, we also know that they have a value that legal.
    private int compileMethod(String line) {
        //TODO- check function...

        //TODO- check regex for proper function definition
        //TODO- also check where we call functions in the code
        // Split the code into void function name, and params
        String[] parts = line.split("[()]");
        // Check it really does start with void, and there are only 2 words in the function declaration
        String[] words = parts[0].split(" ");
        if (words.length > 2 || !words[0].equals("void")) return ILLEGAL; //TODO check if name legal
        //Save the function name
        String name = words[1];

        //Start to work on the params
        //Is the params is a variables of the function? It should be on=ly for local variables, the params
        // of the function are initialized by this part...
        words = parts[1].split(",");
        HashMap<String, Var> temp = new HashMap<>();
        //Go through the params and see if they are legal, if so add them to temp
        for (String word : words) {
            //first check if the param is final, then check for validity

            //This part is in compileVariable...

            //Need to be in a helper function because relevant for global variables as well (lines 193-209)
            parts = word.split(" ");
            if (parts.length == 3) {
                if (!parts[0].equals("final")) return ILLEGAL;
                for (String type : typesOfVariables) {
                    if (type.equals(parts[1])) {
                        Var var = new Var(parts[2], type, false, true);
                        temp.put(var.getName(), var);
                    }
                }
                //if the param isn't final, check for validity
            } else if (parts.length == 2) {
                for (String type : typesOfVariables) {
                    if (type.equals(parts[0])) {
                        Var var = new Var(parts[1], type, false, false);
                        temp.put(var.getName(), var);
                    }
                }
            } else return ILLEGAL;
        }
//        methods.put(name, temp);
        return LEGAL;
    }

    /**
     * Checks if the variable initialization is legal (both type and final)
     *
     * @param line the line of initialization of the variable
     * @return 0 if the initialization is legal, 1 if not, and 2 in case of errors
     */
    //TODO- maybe we should move some of the code from the compile here, because we'll need it a lot...
    //This will be a subroutine of compileVariable...
    private int checkVarValue(String line) {
        String[] words = line.split(" "); //TODO- make into regex, so we can also check for (example) num=5
        String type = variables.get(index).get(words[0]).getType();
        //TODO- check based on the initialization and the type of the var if the initialization is legal
        return LEGAL;
    }

    /**
     * Try to open the file and read the lines
     *
     * @param fileName the file we want to open
     * @return 0 if the file opened properly, 1 if not, and 2 in case of errors
     * @throws IOException
     */
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
        } catch (IOException e) {//TODO: add more types of exceptions
            throw e;
        }
        return LEGAL;
    }

    /**
     * @return the error message we want to print
     */
    public String getErr() {
        return error;
    }


    //int a = 1, b = 7, double = 40;
    //char a = 5+3;

    /**
     * @param variable     the variable we want to check
     * @param type         the type of the variable
     * @param variablesMap the HashMap we want to insert the variable into (based on the scope)
     * @param finalVal     whether the variable is final or not
     * @return 0 if the variable is legal, 1 if not, and 2 in case of errors
     */
    private int compileVariable(String variable, String type, HashMap<String, Var> variablesMap, boolean finalVal) {
        String[] tokenOfVariable = variable.split(",");
        if (tokenOfVariable[1].matches(ILLEGAL_NAME_REGEX)) {
            return ILLEGAL;
        } else {
            //TODO: finish
            for (String token : tokenOfVariable) {
                if (variablesMap.containsKey(token)) {
                    if (!variablesMap.get(token).getType().equals(type))
                        return ILLEGAL;
                    else {
                        //Check if valid
                    }
                } else {
                    //if valid then
                    Pattern ifInitialization = Pattern.compile("=");
                    Matcher initializationMatcher = ifInitialization.matcher(token);
                    if (initializationMatcher.find()) {
                        //TODO- maybe we should take this as an outer function because checkVarLegal also use this code
                        //that's a function used to check if a re-initialization of aa variable is legal
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

//                    variablesMap.put(token, type);
                    //else return INVALID
                }

            }
        }
        return ILLEGAL;
    }
}
