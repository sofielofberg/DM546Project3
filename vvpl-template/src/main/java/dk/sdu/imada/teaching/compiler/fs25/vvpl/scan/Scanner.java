package dk.sdu.imada.teaching.compiler.fs25.vvpl.scan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sofie Løfberg & Sandra Johansen
 * @version CompilerConstruction FT 2025
 */

public class Scanner 
{
    List<Token> scannedTokens =  new LinkedList<>();
    private final String inputString;

    private static final Map<String, TokenType> keywords;

    static 
    {
        keywords = new HashMap<>();
        keywords.put("subtract", TokenType.SUB);
        keywords.put("add", TokenType.PLUS);
        keywords.put("divide", TokenType.DIV);
        keywords.put("multiply", TokenType.MULTIPLY);

        keywords.put("OR", TokenType.OR);
        keywords.put("AND", TokenType.AND);
        keywords.put("NOT", TokenType.NOT);
        keywords.put("NOT_EQUALS", TokenType.NOT_EQUALS);
        keywords.put("EQUALS", TokenType.EQUALS);
        keywords.put("GREATER", TokenType.GREATER);
        keywords.put("GREATER_EQUAL", TokenType.GREATER_EQUAL);
        keywords.put("LESS", TokenType.LESS);
        keywords.put("LESS_EQUAL", TokenType.LESS_EQUAL);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("has_type", TokenType.TYPE_DEF);
        keywords.put("variable", TokenType.VAR);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("write_to_console", TokenType.PRINT);
        keywords.put("loop_while", TokenType.WHILE);
        keywords.put("cast_to", TokenType.CAST);
        keywords.put("is", TokenType.ASSIGN);
        keywords.put("Bool", TokenType.BOOL_TYPE);
        keywords.put("String", TokenType.STRING_TYPE);
        keywords.put("Number", TokenType.NUMBER_TYPE);
    }


    // Scanning state
    private int start = 0;
    private int current = 0;
    private int line = 1;

    
    public Scanner(String inputString) 
    {
        this.inputString = inputString;
    }


    public List<Token> scanTokens() 
    {
        while (!isAtEnd()) 
        {
            start = current;
            char c = advance();
            switch(c) 
            {
                case '(':
                    addToken(TokenType.LEFT_PAREN);
                    break;
                case ')':
                    addToken(TokenType.RIGHT_PAREN);
                    break;
                case '{':
                    addToken(TokenType.LEFT_BRACE);
                    break;
                case '}':
                    addToken(TokenType.RIGHT_BRACE);
                    break;
                case ';':
                    addToken(TokenType.SEMICOLON);
                    break;
                case '-':
                    addToken(TokenType.MINUS);
                    break;
                case '#':
                    comment();
                    break;

                case ' ':
                case '\r':
                case '\t':
                    break;
                case '\n':
                    line++;
                    break;
                
                case '"':
                    string();
                    break;
                
                default: 
                    if (isDigit(c)) 
                    {
                        number();
                    } 
                    else if (isAlpha(c)) 
                    {
                        identifier();
                    } 
                    else 
                    {
                        error();
                    }
                    break;
            }
        }
        
        scannedTokens.add(new Token(TokenType.EOF, "", null, line));
        return scannedTokens;
    }

    private void number()
    {
        while(isDigit(peek()))
        {
            advance();
        }

        // look for fractional
        if(peek() == '.' && isDigit(peekNext()))
        {
            advance();

            while(isDigit(peek()))
            {
                advance();
            }
        }

        addToken(TokenType.NUMBER, Double.parseDouble(inputString.substring(start,current)));
    }

    
    void identifier() 
    {
        while (isAlphaNumeric(peek())) 
        {
            advance();
        }

        String text = inputString.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) 
        {
            type = TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    void string() 
    {
        int startLine = line;
        while (peek() != '"' && !isAtEnd()) 
        {
            if (peek() == '\n') 
            {
                line++;
            }
            advance(); 
        }

        if (isAtEnd() || line - startLine >= 2) 
        {
            //Non-terminated string
            error();
            return;
        }

        advance();

        String value = inputString.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    void comment() 
    {
        while (peek() != '\n') 
        {
            advance();
        }
    }

    void error() 
    {
        System.out.println("Woops");
    }

    //////////////////////////////////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////////////////////////////////
    private void addToken(TokenType type) 
    {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) 
    {
        String text = inputString.substring(start, current);
        scannedTokens.add(new Token(type, text, literal, line));
    }
    
    private char advance() 
    {
        return inputString.charAt(current++);
    }

    private char peek() 
    {
        if (isAtEnd())
        {
            return '\0';
        }
        return inputString.charAt(current);
    }

    private boolean isAtEnd() 
    {
        return current >= inputString.length();
    }

    boolean isAlpha(char c) 
    {
        return (c >= 'a' && c <= 'z') |
            (c >= 'A' && c <= 'Z') |
            c == '_';
    }

    boolean isAlphaNumeric(char c) 
    {
        return isAlpha(c) || isDigit(c);
    }
    
    // works but ugly
    private boolean isDigit(char c)
    {
        boolean bool = false;
        if( c >= '0' && c <= '9' )
        {
            bool = true;
        }
        else if( c == '-' && peek() >= '0' && peek() <= '9' )
        {
            bool = true;
        }

        return bool;
    }

    private char peekNext()
    {
        if( current + 1 >= inputString.length())
        {
            return '\0';
        }
        return inputString.charAt(current + 1);
    }
}
