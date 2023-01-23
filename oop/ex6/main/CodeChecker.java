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
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final int LEGAL = 0;
    private static final int ILLEGAL = 1;
    private static final int ERROR = 2;
    private static final String COMMENT_REGEX = "^//.*";
    private static final String BLANK_LINE_REGEX = "\\s*";
    private static final String[] typesOfVariables = {"int", "double", "String", "boolean", "char"};
    private static final String ILLEGAL_NAME_REGEX = "_|[0-9].*";

    //    private static final String INT_CORRECT_FORM_REGEX = "^(?:(?:.*?)\\s*(?:=\\s*\\d+)?,?)+?;$";
//    private static final String INT_REGEX = "^((?:(?:.*?)\\s*(?:=\\s*(\\d+)))?,?)+?;$";
    private static final String INT_REGEX = "(?:\\+|-|)[0-9]+";
    private static final String DOUBLE_REGEX = "(?:\\+|-|)((\\d+.\\d*)|(\\d*.\\d+))" + "|" + INT_REGEX;
    private static final String STRING_REGEX = "\"[^\"]*\"";
    private static final String BOOLEAN_REGEX = "true|false|" + INT_REGEX + "|" + DOUBLE_REGEX;
    private static final String CHAR_REGEX = "'.'";
    private static final int NAME = 0;
    private static final int MAX_PARTS = 2;
    private static final int VAL_TO_PUT = 1;

    private static String error;
    private static ArrayList<String> linesOfFile;
    private static CodeChecker codeChecker;
    private static Pattern oneLinerComment;
    private static Pattern emptyLinePattern;
    private static Pattern illegalVariableName;
    private static Pattern intPattern;
    private static Pattern doublePattern;
    private static Pattern stringPattern;
    private static Pattern charPattern;
    private static Pattern booleanPattern;
    private static Pattern legalNameChars;

    private static int index;
    private static boolean returned;

    //saves the scope, and a hashmap with all the scope's variables name and their type
    private HashMap<Integer, HashMap<String, Var>> variables;
    //TODO- FIX methods !!!!!!!!!!!!!!!!!!
    private HashMap<String, ArrayList<Var>> methods; //TODO: consider the relation between methods
    // and variables


    /**
     * Basic Singleton constructor for the class CodeChecker
     */
    private CodeChecker() {
        linesOfFile = new ArrayList<>();
        oneLinerComment = Pattern.compile(COMMENT_REGEX);
        emptyLinePattern = Pattern.compile(BLANK_LINE_REGEX);
        illegalVariableName = Pattern.compile(ILLEGAL_NAME_REGEX);
        intPattern = Pattern.compile(INT_REGEX);
        doublePattern = Pattern.compile(DOUBLE_REGEX);
        stringPattern = Pattern.compile(STRING_REGEX);
        booleanPattern = Pattern.compile(BOOLEAN_REGEX);
        charPattern = Pattern.compile(CHAR_REGEX);
        legalNameChars = Pattern.compile("[0-9A-Za-z_]*");
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
            if (globalVars() == ILLEGAL) return ILLEGAL;
            for (String line : linesOfFile) {
                if (compileLine(line.trim().replace("\t", "")) == ILLEGAL) return ILLEGAL;
            }
            if (index != 1) {
                error = "Illegal number of curly brackets";
                return ILLEGAL;
            }
        } catch (IOException e) {
            error = e.getMessage();
            return ERROR;
        } catch (FileException e) {
            error = e.getMessage();
            return ILLEGAL;
        }
        return LEGAL;
    }

    private int globalVars() throws IllegalVariableException, IllegalExpressionException, NotFoundException, IllegalNameException {
        int ind = 1;
        for (int i = 0; i < linesOfFile.size(); i++) {
            if (linesOfFile.get(i).contains("{")) ind++;
            if (linesOfFile.get(i).contains("}")) ind--;
            if (ind > 1) continue;
            String line = linesOfFile.get(i);
            String[] words = line.split(" ");
            if (line.charAt(line.length() - 1) == ';') { //Out of bounds
                for (String type : typesOfVariables) {
                    if (words[0].equals("final") && type.equals(words[1])) {
                        if(compileVariableDecleration(line.substring(words[0].length() + 1 + words[1].length() + 1, line.length() - 1), words[1], variables.get(1), true) == ILLEGAL) return ILLEGAL;
                        //
                    } else if (type.equals(words[0])) {
                        if(compileVariableDecleration(line.substring(words[0].length() + 1, line.length() - 1), words[0], variables.get(1), false) == ILLEGAL) return ILLEGAL;
                    }
                }
            }
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
    private int compileLine(String line) throws FileException {
        if (line.contains("/*") || line.contains("/**") || line.contains("//") || line.contains("*/")) {
            error = "Illegal comment";
            return ILLEGAL;
        }


        line = line.replaceAll("[ ]{2,}", " ");
        String[] words = line.split(" ");
//        System.out.println("Compiling! - " + line + " words " + words.length);
        if (line.charAt(line.length() - 1) == ';') {
             if (words.length == 1 && words[0].equals("return;") && index > 1) {
                //Changed to words[0] for readability
                //Checks return case
                returned = true;
                return LEGAL;
            }
            if (returned) {
                error = "Lines after returned from function";
                return ILLEGAL;
            }
            for (String type : typesOfVariables) {
                if (words[0].equals("final") && type.equals(words[1])) {
                    if (index > 1) {
                        return compileVariableDecleration(line.substring(words[0].length() + 1 + words[1].length() + 1, line.length() - 1), words[1], variables.get(1), true);
                    }
                    else if(index == 1) return LEGAL;
                    //
                } else if (type.equals(words[0])) {
                    if (index > 1) {
                        return compileVariableDecleration(line.substring(words[0].length() + 1, line.length() - 1), words[0], variables.get(index), false);
                    }
                    else if(index == 1) return LEGAL;
                }

            }
            if (checkVariableExist(words[0]) != null) return compileSetVariable(line);
            String[] name = words[0].split("[(]");
            if (methods.containsKey(name[0])) {
                if (index < 2) {
                    error = "Illegal call of function";
                    return ILLEGAL;
                }
                return checkFuncCall(line, methods.get(name[0]));
            }
            error = "Illegal line";
            return ILLEGAL;
        } else if (line.charAt(line.length() - 1) == '{') {
            switch (words[0]) {
                case "void":
                    if (index > 1) {
                        error = "Illegal location of definition of function";
                        return ILLEGAL;
                    } else {

                        // TODO ----- need to move this part to first run (tests 427 and 452), order of
                        //  declaration of the function after the call to the function
                        variables.put(++index, new HashMap<>());
                        return compileMethod(line.substring(5));
                    }
                case "if":
                case "while":
                    variables.put(++index, new HashMap<>());
                    return checkIfWhileExpression(line.substring(words[0].length()+1,line.length()-1));
            }
            error = "Illegal line";
            return ILLEGAL;
        } else if (line.charAt(line.length() - 1) == '}') {
            //TODO: check
            if (index == 1) {
                error = "Illegal curly brackets";
                return ILLEGAL;
            }
            variables.remove(index--);
            returned = false;
            return LEGAL;
        }

        error = "Illegal line";
        return ILLEGAL;
    }

    //Check if a function call is valid
    private int checkFuncCall(String line, ArrayList<Var> vars) {
        String[] words = line.split("[()]"); //TODO- check if there's better ways to split
        String[] params;
        if(words[1].length() == 0){
          params = new String[]{};
        }
        else {
            params = words[1].split(",");
        }
        if (params.length != vars.size()) {
            error = "Illegal number of variables";
            return ILLEGAL; //Illegal number of arguments
        }

        for (int i = 0; i < vars.size(); i++) {
            params[i]=params[i].strip();
 //           System.out.println("Checking variable: " + vars.get(i) + " ---- " + params[i]);

            // Check if we got a variable name
            Var curVar = checkVariableExist(params[i]);
            if (curVar == null) {
                // if not a variable name, check if we got the expected type of variable
                error = "Illegal variables";
                switch(vars.get(i).getType()){
                    case "int":
                        Matcher intType = intPattern.matcher(params[i]);
                        if(!intType.matches()){
                            return ILLEGAL;
                        }
                        break;
                    case "double":
                        //Matcher intTDouble =intPattern.matcher(params[i]);
                        Matcher doubleMatcher  = doublePattern.matcher(params[i]);
                        if(!doubleMatcher.matches()){ //intTDouble.matches() ||
                            return ILLEGAL;
                        }
                        break;
                    case "String":
                        Matcher stringMatcher = stringPattern.matcher(params[i]);
                        if(!stringMatcher.matches()){
                            return ILLEGAL;
                        }
                        break;
                    case "boolean":
                        Matcher boolMatcher = booleanPattern.matcher(params[i]);
                        //Matcher intToType = intPattern.matcher(params[i]);
                        //Matcher doubleType = doublePattern.matcher(params[i]);
                        if(!boolMatcher.matches() ){ //|| intToType.matches() || doubleType.matches()
                            return ILLEGAL;
                        }
                        break;
                    case "char":
                        Matcher charMatcher = charPattern.matcher(params[i]);
                        if(!charMatcher.matches()){
                            return ILLEGAL;
                        }
                        break;
                }
            }
            else {
                // Check if variable type is right
                if (!vars.get(i).getType().equals(curVar.getType())) {
                    error = "Illegal variable/s type";
                    return ILLEGAL;
                }

                // Check if variable is initialized
                if (!curVar.Initiated()) {
                    error = "Variable not initialized";
                    return ILLEGAL;
                }
            }
//            if (curVar.isFinal() && !vars.get(i).isFinal()) {
//                error = "Illegal variables";
//                return ILLEGAL;
//            }
//            String newLine = vars
//            return compileSetVariable()
        }
//        int num = index;
//        while (num > 0) {
//            for (String name : params) {
//                if (!(name.split(" ")[0].equals("final") && variables.get(num).get(name).Initiated() && variables.get(num).get(name).isFinal())) {
//                    return ILLEGAL;
//                }
//                if (!(variables.get(num).containsKey(name) && variables.get(num).get(name).Initiated())) {
//                    return ILLEGAL;
//                }
//            }
//        }
        return LEGAL;
    }


    /**
     * A function that ressponsble to check the expression inside the if/while start of bllock.
     * for example if(...) or while(...)
     * @param line - the variables in format (a&&b||  c)
     * @return LEGAL if valid, else ILLEGAL
     * @throws IllegalExpressionException = If the expression is illegal
     */

    private int checkIfWhileExpression(String line) throws IllegalExpressionException {
        //String exp = line.substring(line.indexOf('('), line.indexOf(')'));
        String exp = line.replaceFirst("[ ]*\\(","").replaceFirst("\\)[ ]*","");
        exp = exp.replaceAll(" ","");
        exp = exp.replaceAll("&&"," ");
        exp = exp.replaceAll("\\x7c\\x7c"," ");
        String[] miniExpressions = exp.split(" ");
        for (int i = 0; i < miniExpressions.length; i++) {
            miniExpressions[i] = miniExpressions[i].strip();
        }
        for (String miniExpression : miniExpressions) {
            if (!checkIsLegal(miniExpression)) {
                throw new IllegalExpressionException();
            }
        }
        return LEGAL;
    }


    /**
     * Checks if the boolean Expression is valid
     * @param exp = the expression to check
     * @return true if legal, false else
     */
    private boolean checkIsLegal(String exp) {
        if (exp.equals("")) {
            return false;
        }
        Matcher booleanExp = booleanPattern.matcher(exp);
        if (booleanExp.matches()) {
            return true;
        }
        Var expVar = checkVariableExist(exp);
        if (expVar != null && expVar.Initiated()) {
            return expVar.getType().equals(BOOLEAN) || expVar.getType().equals(INT) ||
                    expVar.getType().equals(DOUBLE);
        }
        return false;
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
        //Save the function name
        String name = parts[0];

        //Start to work on the params
        //Is the params is a variables of the function? It should be on=ly for local variables, the params
        // of the function are initialized by this part...
        String[] params = parts[1].split(",");
        ArrayList<Var> temp = new ArrayList<>();
        //Go through the params and see if they are legal, if so add them to temp
        for (String param : params) {
            //first check if the param is final, then check for validity

            //This part is in compileVariable...

            //Need to be in a helper function because relevant for global variables as well (lines 193-209)
            parts = param.strip().split(" ");
 //           System.out.println("*** param: "+param+ " parts #:" + parts.length);
            if (parts.length == 3) {
                if (!parts[0].equals("final")) return ILLEGAL;
                for (String type : typesOfVariables) {
                    if (type.equals(parts[1])) {
                        Var var = new Var(parts[2], type, true, true);
                        temp.add(var);
                    }
                }
                //if the param isn't final, check for validity
            } else if (parts.length == 2) {
                for (String type : typesOfVariables) {
                    if (type.equals(parts[0])) {
                        Var var = new Var(parts[1], type, true, false);
                        temp.add(var);
                    }
                }
            } else if (param.strip().equals("")) {
                continue;
            } else {
                error = "Illegal method";
                return ILLEGAL;
            }
        }
        methods.put(name, temp);
        HashMap<String, Var> varsTemp = new HashMap<>();
        for (Var v : temp) {
            varsTemp.put(v.getName(), v);
        }
        variables.put(index, varsTemp);
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
            //TODO- delete when submitting
//            System.out.println(linesOfFile.toString());
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



    /**
     * @param variable     the variable we want to check
     * @param type         the type of the variable
     * @param variablesMap the HashMap we want to insert the variable into (based on the scope)
     * @param finalVal     whether the variable is final or not
     * @return 0 if the variable is legal, 1 if not, and 2 in case of errors
     */

    //TODO check if we can change to stirng line or change calls to this function

    /**
     * The function that responsible to compile the variables declerations according to type and put them
     * in the right HashMap.
     * @param variable
     * @param type
     * @param variablesMap
     * @param finalVal
     * @return
     * @throws IllegalNameException
     * @throws IllegalVariableException
     * @throws NotFoundException
     * @throws IllegalExpressionException
     */
    private int compileVariableDecleration(String variable, String type, HashMap<String, Var> variablesMap,
                                           boolean finalVal) throws IllegalNameException,
            IllegalVariableException, NotFoundException, IllegalExpressionException {
        if (variable.equals("")) {
            throw new NotFoundException();
        }
        String[] variablesList = variable.split((","));
        //check definitions of variable
        for (String token : variablesList) {
            if (token.contains("=")) {
                String[] token_parts = token.split("=");
                if (token_parts.length > MAX_PARTS) {
                    throw new IllegalVariableException();
                }
                for (int i = 0; i < token_parts.length; i++) {
                    token_parts[i] = token_parts[i].strip();
                }
                if (checkVariableName(token_parts[NAME]) && !variablesMap.containsKey(token_parts[NAME])) {
                    //If we got here, we know that we add a new variable, so - we must check its value
                    if (checkVal(token_parts[VAL_TO_PUT], type)) {
                        Var varToAdd = new Var(token_parts[NAME], type, true, finalVal);
                        if(variablesMap.containsKey(varToAdd.getName())) {
                            error = "Multiple definition of a variable";
                            return ILLEGAL;
                        }
                        variablesMap.put(varToAdd.getName(), varToAdd);
                    }
                    else {
                        throw new IllegalVariableException();
                    }
                }
                else {
                    throw new IllegalNameException();
                }
            } else {
                if (checkVariableName(token.strip()) && !variablesMap.containsKey(token.strip()) && !finalVal) {
                    //Add to dict
                    Var varToAdd = new Var(token.strip(), type);
                    if(variablesMap.containsKey(varToAdd.getName())) {
                        error = "Multiple definition of a variable";
                        return ILLEGAL;
                    }
                    variablesMap.put(varToAdd.getName(), varToAdd);
                } else {
                    throw new IllegalNameException();
                }
            }
        }
        return LEGAL;
    }

    /**
     * compile a Set value to variables, each variable get the correct type to him. else, throws an exception.
     * @param variableList - The list oof variables
     * @return LEGAL if set is correct, else Illegal
     * @throws IllegalVariableException = The variable is illegal
     * @throws NotFoundException = A variable not found
     * @throws IllegalAssigmentException = Illegal Assigment
     */
    private int compileSetVariable(String variableList) throws IllegalVariableException, NotFoundException,
            IllegalAssigmentException {
        String[] variables = variableList.split(",");
        for (var variable : variables) {
            if (variable.contains("=")) {
                String[] token_parts = variable.split("=");
                if (token_parts.length > MAX_PARTS) {
                    throw new IllegalVariableException();
                }
                Var variableToCheck = checkVariableExist(token_parts[NAME]);
                if (variableToCheck == null) {
                    throw new NotFoundException();
                }
                if (variableToCheck.isFinal()) {
                    throw new IllegalAssigmentException();
                }
                if (variableToCheck.getType().equals(DOUBLE)) {
                    if (!checkVal(token_parts[VAL_TO_PUT], variableToCheck.getType()) ||
                            !checkVal(token_parts[VAL_TO_PUT], INT)) {
                        throw new IllegalVariableException();
                    }
                } else if (variableToCheck.getType().equals(BOOLEAN)) {
                    if (!checkVal(token_parts[VAL_TO_PUT], variableToCheck.getType()) ||
                            !checkVal(token_parts[VAL_TO_PUT], INT) ||
                            !checkVal(token_parts[VAL_TO_PUT], DOUBLE)) {
                        throw new IllegalVariableException();
                    }

                } else {
                    if (!checkVal(token_parts[VAL_TO_PUT], variableToCheck.getType())) {
                        throw new IllegalVariableException();
                    }
                }
            }
        }
        return LEGAL;
    }

    /**
     * A function that checks if value of a variable is valid, according to Ex'6 description.
     * @param value
     * @param type
     * @return
     */
    private boolean checkVal(String value, String type) {
        switch (type) {
            case INT:
                Matcher intType = intPattern.matcher(value);
                if (intType.matches()) {
                    return true;
                }
                break;
            case DOUBLE:
                Matcher doubleType = doublePattern.matcher(value);
                if (doubleType.matches()) {
                    return true;
                }
                break;
            case STRING:
                Matcher stringType = stringPattern.matcher(value);
                if (stringType.matches()) {
                    return true;
                }
                break;
            case BOOLEAN:
                Matcher booleanType = booleanPattern.matcher(value);
                if (booleanType.matches()) {
                    return true;
                }
                break;
            case CHAR:
                Matcher charType = charPattern.matcher(value);
                if (charType.matches()) {
                    return true;
                }
                break;
        }
        //If we got here, we might assign a value of other variable
        Var valToAssign = checkVariableExist(value);
        if (valToAssign != null && valToAssign.getType().equals(type) && valToAssign.Initiated()) {
            return true;
        }
        return false;
    }


    /**
     * Checks if variable name is valid
     */
    private boolean checkVariableName(String name) throws IllegalNameException, IllegalExpressionException {
        Matcher illegalName = illegalVariableName.matcher(name);
        //Pattern legalNameChars = Pattern.compile("[0-9A-Za-z_]*");
        Matcher namePattern = legalNameChars.matcher(name);
        if (namePattern.matches()) {
            if (illegalName.matches()) {
                throw new IllegalNameException();
            }
            return true;
        }
        throw new IllegalExpressionException();
    }

    /**
     * Checks if the variable already exists in one of the hashMaps
     *
     * @param name - the name of the variable
     * @return true if exists such variable in any scope...
     */
    private Var checkVariableExist(String name) {
        //System.out.println("checking variable " + name);
        for (int i = index; i >= 0; i--) {
            if (variables.get(i) != null && variables.get(i).containsKey(name)) {
                return variables.get(i).get(name);
            }
        }
        return null;
    }

    private String checkMethodExist(String name) {
        for (int i = index; i >= 0; i--) {
            if (variables.get(i).containsKey(name)) {
//                return methods.get(i).ge;
            }
        }
        return null;
    }
//
//    /**
//     * Used for definition of new variables in some scope, to be sure that there is no one in the same name
//     */
//    private Var checkVariableExistsInScope(String name){
//        return variables.get(index).get(name);
//    }
}
