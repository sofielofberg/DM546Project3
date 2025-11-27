package dk.sdu.imada.teaching.compiler.fs25.vvpl.parse;

import java.util.ArrayList;
import java.util.List;

import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.Block;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.ExprStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.IfStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.PrintStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.WhileStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.VarDecl;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.scan.Token;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.scan.TokenType;

/**
 * @author Sandra K. Johansen and Sofie Løfberg
 * @version CompilerConstruction FT 2025
 */

public class Parser 
{

    private List<Token> tokens;
    private int current = 0;
   
    public Parser(List<Token> tokens) 
    {
        this.tokens = tokens;
    }

    public List<Stmt> parse() 
    {
        List<Stmt> statements = new ArrayList<>();
        
        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    private Stmt statement()
    {
        if (match(TokenType.IF)) 
        {
            return ifStmt();
        }
        if (match(TokenType.PRINT)) 
        {
            return printStmt();
        }
        if (match(TokenType.WHILE)) 
        {
            return whileStmt();
        }
        if (match(TokenType.LEFT_BRACE))
        {
            return block();
        }
        if (match(TokenType.VAR)) 
        {
            return varDecl();
        }
        return exprStmt();
    }

    private IfStmt ifStmt() 
    {
        consume(TokenType.LEFT_PAREN, "Missing parenthesis");
        Expr expr = expression();
        consume(TokenType.RIGHT_PAREN, "missing parenthesis");
        Stmt stmt = statement();
        if(match(TokenType.ELSE)) 
        {
            Stmt elseStmt = statement();
            return new IfStmt(expr, stmt, elseStmt);
        }
        return new IfStmt(expr, stmt, null);

    }

    private PrintStmt printStmt() 
    {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new PrintStmt(expr);   

    }

    private WhileStmt whileStmt() 
    {
        consume(TokenType.LEFT_PAREN, "Missing parenthesis");
        Expr expr = expression();
        consume(TokenType.RIGHT_PAREN, "Missing parenthesis");
        Stmt stmt = statement();
        return new WhileStmt(expr, stmt);
    }

    private Block block() 
    {
        List<Stmt> stmts = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) 
        {
            Stmt stmt = statement();
            stmts.add(stmt);
        }
        consume(TokenType.RIGHT_BRACE, "missing brace");
        return new Block(stmts);
    }

    private VarDecl varDecl() 
    {
        String name = consume(TokenType.IDENTIFIER, "Variable has no name").lexeme;
        consume(TokenType.TYPE_DEF, "Variable has no type");
        Token type = null;
        if (match(TokenType.STRING_TYPE, TokenType.NUMBER_TYPE, TokenType.BOOL_TYPE)) {
            type = previous();
        }
        if (match(TokenType.ASSIGN)) {
            Expr expr = expression();
            consume(TokenType.SEMICOLON, "Missing semicolon");
            return new VarDecl(name, type, expr);
        }
        consume(TokenType.SEMICOLON, "Missing semicolon");
        return new VarDecl(name, type, null);
    }

    private ExprStmt exprStmt() 
    {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new ExprStmt(expr);
    }

    // EXPRESSION
    private Expr expression()
    {
        return assignment();
    }

    private Expr assignment()
    {
        if (peekNext().type == TokenType.ASSIGN) 
        {
            String var = consume(TokenType.IDENTIFIER, "Missing id").lexeme;
            consume(TokenType.ASSIGN, "");
            Expr expr = assignment();
            return new Expr.Assignment(var, expr);
        } 
        
        return logicOr();
    }

    private Expr logicOr() 
    {
        Expr expr = logicAnd();

        while (match(TokenType.OR)) 
        {
            Token operator = previous();
            Expr right = logicAnd();
            expr = new Expr.LogicExpr(expr, operator, right);
        }

        return expr;
    }
    
    private Expr logicAnd() 
    {
        Expr expr = equality();

        while (match(TokenType.AND)) 
        {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.LogicExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() 
    {
        Expr expr = compr();

        while (match(TokenType.NOT_EQUALS, TokenType.EQUALS)) 
        {
            Token operator = previous();
            Expr right = compr();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // comparison
    private Expr compr() 
    {
        Expr expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // TERM
    private Expr term() 
    {
        if(match(TokenType.PLUS, 
                    TokenType.SUB, 
                    TokenType.DIV,
                    TokenType.MULTIPLY))
        {
            Token operator = previous();
            consume(TokenType.LEFT_PAREN, "missing parenthesis");
            Expr left = term();
            Expr right = term();
            Expr expr = new Expr.Binary(left, operator, right);
            consume(TokenType.RIGHT_PAREN, "missing parenthesis");
            return expr;
        }
        return unary();
    }

    private Expr unary()
    {
        if(match(TokenType.NOT, TokenType.MINUS))
        {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }
    

    private Expr primary()
    {
        Token type = null;
        if(match(TokenType.CAST)) 
        {
            if (match(TokenType.NUMBER_TYPE, TokenType.BOOL_TYPE, TokenType.STRING_TYPE)) 
            {
                type = previous();
            } 
            else 
            {
                throw new Error("Not type");
            }
        }

        if(match(TokenType.FALSE))
        {
            return new Expr.Literal(type, false);
        }
        if(match(TokenType.TRUE))
        {
            return new Expr.Literal(type, true);
        }

        if(match(TokenType.NUMBER, TokenType.STRING))
        {
            return new Expr.Literal(type, previous().literal);
        }

        if(match(TokenType.LEFT_PAREN))
        {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression");
            return new Expr.Grouping(type, expr);
        }

        if(match(TokenType.IDENTIFIER))
        {
            return new Expr.Variable(type, previous().lexeme);
        }

        throw new Error("not a primary: " + peek().lexeme + " line: " + peek().line);
    }
    

      /******************** /
     /  helper functions   /
    /*********************/
    // check if tokentypes are same
    private boolean match(TokenType... types)
    {
        for(TokenType type : types)
        {
            if(check(type))
            {
                advance();
                return true;
            }
        }
        return false;
    }

    // consume until we find thing
    private Token consume(TokenType type, String message)
    {
        if(check(type))
        {
            return advance();
        }
        throw new Error(message);
    }

    // check if current token is of given type
    private boolean check(TokenType type)
    {
        if(isAtEnd())
        {
            return false;
        }
        return peek().type == type;
    }

    // consumes current token and returns it
    private Token advance()
    {
        if(!isAtEnd())
        {
            current++;
        }
        return previous();
    }

    // checks if we're done
    private boolean isAtEnd()
    {
        return peek().type == TokenType.EOF;
    }

    // return current unconsumed token
    private Token peek()
    {
        return tokens.get(current);
    }

    private Token peekNext() 
    {
        return tokens.get(current + 1);
    }

    // returns latest recently consumed token
    private Token previous()
    {
        return tokens.get(current - 1);
    }

}