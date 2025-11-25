package dk.sdu.imada.teaching.compiler.fs25.vvpl.scan;

/**
 * @author Sofie Løfberg & Sandra Johansen
 * @version CompilerConstruction FT 2025
 */

public enum TokenType 
{
    // Single-character tokens
    HASHTAG, //Comments
    RIGHT_PAREN,
    LEFT_PAREN,
    RIGHT_BRACE,
    LEFT_BRACE,
    SEMICOLON,
    MINUS,

    //literals
    STRING, 
    NUMBER,  
    IDENTIFIER,

    //Types
    STRING_TYPE,
    NUMBER_TYPE,
    BOOL_TYPE,

    //Operators
    SUB, 
    PLUS,
    DIV,
    MULTIPLY,
    
    // Keywords
    OR, 
    AND,
    NOT, 
    NOT_EQUALS,
    EQUALS,
    GREATER,
    GREATER_EQUAL,
    LESS,
    LESS_EQUAL,
    TRUE,
    FALSE,
    TYPE_DEF,
    VAR,
    IF,
    ELSE,
    PRINT,
    WHILE,
    CAST,
    ASSIGN,
    
    // End-of-file
    EOF
}
