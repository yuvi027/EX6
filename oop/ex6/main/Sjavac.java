package oop.ex6.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {

    private static final int LEGAL = 0;
    private static final int FILE_NAME_IND = 0;//2;
    private static final int NUM_OF_ARGS = 1;


    public static void main(String[] args) {

//       Matcher fileHasCorrectEnding = fileEndingPattern.matcher("oop/ex6/main/test2.txt");


        if (args.length != NUM_OF_ARGS) {
            System.out.println(args.length);
            System.err.println("Wrong parameters number!");
            return;
        }
        CodeChecker checker  = new CodeChecker();
        //CodeChecker checker = CodeChecker.getInstance();
        int num = checker.checkCode(args[FILE_NAME_IND]);
//    int num = checker.checkCode("test.txt");
//        int num = checker.checkCode("oop/ex6/main/test2.txt");
        Pattern fileEndingPattern = Pattern.compile("\\.sjava$");
        Matcher fileHasCorrectEnding = fileEndingPattern.matcher(args[FILE_NAME_IND]);
        if (!fileHasCorrectEnding.find()) {
            System.out.println(2);
            System.err.println("Wrong file name!");
            return;
        }
        System.out.println(num);

        if (num != LEGAL) {
            System.err.println(checker.getErr());
        }


//        HashMap<String, Var> h = new HashMap<>();
//        HashMap<Integer, HashMap<String, Var>> j = new HashMap<>();
//        j.put(0, h);
//        //h.put("c", new Var("c", "boolean"));
//        try{
//            String a = "hello";
//            String b = "World";
//            checker.compileVariableDecleration("c=5-3", "int", h, false);
//        } catch (FileException e) {
//            System.out.println("Failed!!");
//        }
    }
}
