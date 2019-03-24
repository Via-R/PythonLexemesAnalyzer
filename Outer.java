/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Infinity Monk
 */

import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner; 
import java.util.regex.*;


class Pair <L,R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
      this.left = left;
      this.right = right;
    }

    public L l() {
        return left;
    }
    public R r() {
        return right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.l()) && this.right.equals(pairo.r());
    }

}

class Selection {
    public int from, to;
    public String type;
    
    public Selection(int from, int to, String type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }
}

class LexemesHolder {
    private static Map <String, Integer> pr = new HashMap <String, Integer>();
    private static final String[] lexemesTypes = {
            "WHITESPACE", "COMMENT", "PROPERTY", "IMAGINARY", "FLOAT", "STRING", "FUNCTION", "OPERATOR", "KEYWORD",
            "INT", "BOOL", "NONE", "VARIABLE", "UNDEFINED"
        };
    
    private static List<Pair> dictionary = new ArrayList<Pair>();
    
    private static void insertPair(String reg, int type) {
        boolean added = false;
        for (int i = 0; i < dictionary.size(); ++i) {
            if (type < (int) dictionary.get(i).r()) {
                dictionary.add(i, new Pair<String, Integer>(reg, type));
                added = true;
                break;
            }
        }
        if (!added)
            dictionary.add(new Pair<String, Integer>(reg, type));
    }
    
    public LexemesHolder() {
        for (int i = 0; i < lexemesTypes.length; ++i)
            pr.put(lexemesTypes[i], i);
        
        String[] keywords = { 
            "and", "as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except",
            "finally", "for", "from", "global", "if", "import", "in", "is", "lambda", "nonlocal", "not", "or", "pass", "raise", 
            "return", "try", "while", "with", "yield" };
        for (int i = 0; i < keywords.length; ++i){
            insertPair(singleWordRegExp(keywords[i]), pr.get("KEYWORD"));
        }
        
        String[] ordinaryOperators = {
            ">>=", "<<=", "%-", "!=", "<<", ">>", "<=", ">=", "-=", "&=", "%="};
        
        insertPair("\\/\\/", pr.get("OPERATOR"));
        insertPair("\\/\\/=", pr.get("OPERATOR"));
        insertPair("\\*\\*", pr.get("OPERATOR"));
        insertPair("\\*\\*=", pr.get("OPERATOR"));
        
        for (int i = 0; i < ordinaryOperators.length; ++i){
            insertPair(ordinaryOperators[i], pr.get("OPERATOR"));
        }
        
        insertPair("(?<=\\W|^)@[a-zA-Z_]+(?=\\W|$)", pr.get("PROPERTY"));
        
        String[] specialOperators = {
            "+=", "*=", "/=", "|=", "^=", 
            "+", "/", "|", "^", "*",
            "(", ")", "{", "}", "[", "]", ".", ":"};
        
        for (int i = 0; i < specialOperators.length; ++i){
            insertPair(singleOperatorRegExp(specialOperators[i]), pr.get("OPERATOR"));
        }
        
        String[] ordinaryShortOperators = {
            "<", ">", "=", "-", "&",  "~", ",", ";", "@"
        };
        
        for (int i = 0; i < ordinaryShortOperators.length; ++i){
            insertPair(ordinaryShortOperators[i], pr.get("OPERATOR"));
        }
        
        insertPair("#.*", pr.get("COMMENT"));
        insertPair("(?<=[^\\\\]|^)\\\".*?[^\\\\]\\\"", pr.get("STRING"));
        insertPair("(?<=[^\\\\]|^)\\\'.*?[^\\\\]\\\'", pr.get("STRING"));
        
        insertPair("(?<=[^\\d\\w\\.]|^)\\d+\\.\\d+j(?=[^\\d\\w\\.]|$)", pr.get("IMAGINARY"));
        insertPair("(?<=[^\\d\\w\\.]|^)\\d*\\.+\\d+(?=[^\\d\\w\\.]|$)", pr.get("FLOAT"));
        insertPair("(?<=[^\\d\\w\\.]|^)\\d*(?=[^\\d\\w\\.]|$)", pr.get("INT"));
        insertPair("(?<=[\\W]|^)(True|False)((?=[\\W])|$)", pr.get("BOOL"));
        insertPair("(?<=[\\W]|^)(None)((?=[\\W])|$)", pr.get("NONE"));
        
        insertPair("(?<=\\s|^)[a-zA-Z_]+[\\s]*(?=\\()", pr.get("FUNCTION"));
        
        insertPair("(?<=[\\W]|^)[a-zA-Z_]\\w*(?=\\W|$)", pr.get("VARIABLE"));
        insertPair("\\s", pr.get("WHITESPACE"));
        
        insertPair("(?<=\\s|^)[^\\s]+(?=\\s|$)", pr.get("UNDEFINED"));
//        printDict();
    }
    
    private static String singleWordRegExp(String s) {
        return "(?<=[\\W]|^)" + s + "((?=[\\W])|$)";
    }
    
    private static String singleOperatorRegExp(String s) {
        return "\\" + s;
    }
    
    private static void printDict() {
        for (Pair i : dictionary) {
            System.out.println(i.l() + ": " + i.r());
        }
    }
    
    public static void print(String s) {
        System.out.println(s);
    }
    
    public static void print(Integer s) {
        System.out.println(s);
    }
    
    private static String filler(int n) {
        String chronos = "";
        for(int i = 0; i < n; ++i)
            chronos += " ";
        return chronos;
    }
    
    private static String escape(char s) {
        String r = Character.toString(s);
        String[] special = {
            "+", "*", "/", "|", "^",
            "(", ")", "{", "}", "[", "]", ".", ":"
        };
        if (Arrays.asList(special).contains(r))
            return "\\" + r;
        if (Character.isWhitespace(s))
            return "\\s";
        return r;
    }
    
    private static void insertSelection(List<Selection> l, Selection sel){
        for (int i = 0; i < l.size(); ++i){
            if (sel.from < l.get(i).from){
                l.add(i, sel);
                return;
            }
        }
        l.add(sel);
    }
    
    public static List<Selection> parse(String line) {
        List result = new ArrayList<Selection>();
        for (Pair i : dictionary){
            Pattern pattern = Pattern.compile((String) i.l());
            Matcher matcher = pattern.matcher(line);

            int type = (int) i.r();
            while(matcher.find()) {
                int from = matcher.start();
                int to = matcher.end();
                
                if (to - from == 0)
                    continue;
                
                insertSelection(result, new Selection(from, to, lexemesTypes[type]));
                
                if (lexemesTypes[type] == "WHITESPACE")
                    continue;
               
                line = line.substring(0, from) + filler(to - from) + line.substring(to, line.length());
            }
        }
        return result;
    }
}

class Parser {
    private String finalString = "";
    public Parser(String filename) throws Exception {
        File file = new File("D:\\University\\Java\\Lab3\\" + filename); 
        Scanner sc = new Scanner(file); 
        LexemesHolder regexpDB = new LexemesHolder();
        
        while (sc.hasNextLine()){
            String thisLine = sc.nextLine();
            String finalLine = "";
            List<Selection> lineLexems = regexpDB.parse(thisLine);
            for (Selection sel : lineLexems){
                String lexem = thisLine.substring(sel.from, sel.to);
                finalLine += wrap(lexem, sel.type);
            }
            finalString += wrapLine(finalLine);
        }
    }
    
    private static String wrap(String lexem, String name) {
        if (name == "WHITESPACE"){
            if ((int)lexem.charAt(0) == 9)
                name = "TAB";
            else
                name = "SPACE";
        }
        return "<div class='" + name + "'>" + lexem + "</div>";
    }
    
    private static String wrapLine(String line) {
        return "<div class=\"line\">" + line + "</div>";
    }
    
    public void createOutput(String filename) throws Exception{
        FileWriter fileWriter = new FileWriter("D:\\University\\Java\\Lab3\\" + filename);
        String styles = "<link rel=\"stylesheet\" href=\"stylesheet.css\">";
        fileWriter.write(styles + "<main>" + finalString + "</main>");
        fileWriter.close();
    }
}

public class Outer {
    public static void main(String args[]) throws Exception {
        System.out.println("I'm alive!");
        Parser worker = new Parser("test1.py");
        worker.createOutput("output.html");
        System.out.println("Done!");
    }
}