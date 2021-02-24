package jmilan;

import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

public class Scanner {
    public static final char EOF = (char) -1;
    
    public Scanner(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.input = new LineNumberReader(new FileReader(fileName));
        this.errorState = false;

        this.keywords = new Hashtable<String, Token>();
        this.keywords.put("begin", Token.BEGIN);
        this.keywords.put("end", Token.END);
        this.keywords.put("if", Token.IF);
        this.keywords.put("then", Token.THEN);
        this.keywords.put("else", Token.ELSE);
        this.keywords.put("fi", Token.FI);
        this.keywords.put("while", Token.WHILE);
        this.keywords.put("do", Token.DO);
        this.keywords.put("od", Token.OD);
        this.keywords.put("write", Token.WRITE);
        this.keywords.put("read", Token.READ);

        nextChar();       
    }

    public void close() throws IOException {
        input.close();
    }

    public int getLineNumber() {
        return input.getLineNumber() + 1;
    }

    public String getFileName() {
        return fileName;
    }

    public Token token() {
        return token;
    }

    public int intValue() {
        return intval;
    }

    public String stringValue() {
        return strval;
    }

    public Cmp cmpValue() {
        return cmpval;
    }

    public Arithmetic arithmeticValue() {
        return arithval;
    }

    public void nextToken() {
        skipSpaces();

        // Пропускаем комментарии
        if(ch == '/') {
            nextChar();
            if(ch == '*') {
                nextChar();
                boolean inside = true;
                while(inside) {
                    while(ch != '*') {
                        nextChar();
                    }

                    nextChar();
                    if(ch == '/') {
                        inside = false;
                        nextChar();
                    }
                }
            }
            else {
                token = Token.MULOP;
                this.arithval = Arithmetic.DIVIDE;
                return;
            }
        }

        skipSpaces();

        if(isDigit(ch)) {
            int value = 0;
            while(isDigit(ch)) {
                value = value * 10 + (ch - '0');
                nextChar();
            }

            token = Token.NUMBER;
            intval = value;
        }
        else if(isIdentifierStart(ch)) {
            StringBuffer buffer = new StringBuffer();
            while(isIdentifierBody(ch)) {
                buffer.append(ch);
                nextChar();
            }

            String identifier = buffer.toString().toLowerCase();
            if(keywords.containsKey(identifier)) {
                token = keywords.get(identifier);
            }
            else {
                token = Token.IDENTIFIER;
                strval = identifier;
            }
        }
        else {
            switch (ch) {
                case '(':
                    nextChar();
                    token = Token.LPAREN;
                    break;

                case ')':
                    nextChar();
                    token = Token.RPAREN;
                    break;

                case ';':
                    nextChar();
                    token = Token.SEMICOLON;
                    break;

                case ':':
                    nextChar();
                    if (ch == '=') {
                        nextChar();
                        token = Token.ASSIGN;
                    } else {
                        reportError("':=' expected, but ':' found");
                        nextChar();
                        token = Token.ILLEGAL;
                    }
                    break;

                case '<':
                    token = Token.CMP;
                    nextChar();
                    if (ch == '=') {
                        nextChar();
                        cmpval = Cmp.LE;
                    } else {
                        cmpval = Cmp.LT;
                    }
                    break;

                case '>':
                    token = Token.CMP;
                    nextChar();
                    if (ch == '=') {
                        nextChar();
                        cmpval = Cmp.GE;
                    } else {
                        cmpval = Cmp.GT;
                    }
                    break;

                case '!':
                    nextChar();
                    if (ch == '=') {
                        nextChar();
                        token = Token.CMP;
                        cmpval = Cmp.NE;
                    } else {
                        reportError("'!=' expected, but '!' found");
                        nextChar();
                        token = Token.ILLEGAL;
                    }
                    break;

                case '=':
                    nextChar();
                    token = Token.CMP;
                    cmpval = Cmp.EQ;
                    break;

                case '+':
                    nextChar();
                    token = Token.ADDOP;
                    arithval = Arithmetic.PLUS;
                    break;

                case '-':
                    nextChar();
                    token = Token.ADDOP;
                    arithval = Arithmetic.MINUS;
                    break;

                case '*':
                    nextChar();
                    token = Token.MULOP;
                    arithval = Arithmetic.MULTIPLY;
                    break;

                case '/':
                    nextChar();
                    token = Token.MULOP;
                    arithval = Arithmetic.DIVIDE;
                    break;

                case EOF:
                    token = Token.EOF;
                    break;
                    
                default:
                    reportError("'%c': illegal character", ch);
                    nextChar();
                    token = Token.ILLEGAL;
            }
        }
    }

    private void skipSpaces() {
        while(isSpace(ch)) {
            nextChar();
        }   
    }

    private boolean isSpace(char c) {
        return (c == ' ' || c == '\t' || c == '\n');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isIdentifierStart(char c) {
        return ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z'));
    }

    private boolean isIdentifierBody(char c) {
        return isIdentifierStart(c) || isDigit(c);
    }

    private void nextChar() {
        try {
            ch = (char) input.read();
        }
        catch(IOException e) {
            reportError("Unable to read character");
        }
    }

    private void reportError(String message, Object... args) {
        errorState = true;
        System.err.printf("[%s:%d] : ", getFileName(), getLineNumber());
        System.err.printf(message, args);
        System.err.println();
    }

    private LineNumberReader input;
    private String fileName;

    private boolean errorState;

    private Hashtable<String, Token> keywords;

    private Token token;
    private int intval;
    private String strval;
    private Cmp cmpval;
    private Arithmetic arithval;

    private char ch;
}
