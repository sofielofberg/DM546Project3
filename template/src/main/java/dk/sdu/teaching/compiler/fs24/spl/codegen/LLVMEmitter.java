package dk.sdu.teaching.compiler.fs24.spl.codegen;

//////////////////////////////////////////////
/// LLVEmitter.java
/// November 2025
/// @author Sandra Johansen and Sofie Løfberg
///////////////////////////////////////////////

////////////////// IMPORTS ///////////////////////////////////////
import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;              //
import dk.sdu.teaching.compiler.fs24.spl.ast.ExprVisitor;       //
import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;              //
import dk.sdu.teaching.compiler.fs24.spl.ast.StmtVisitor;       //
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Assign;       //
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Binary;       //
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Literal;      //
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Logical;      //
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Unary;        //
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Variable;     //
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Block;        //
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Expression;   //
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.If;           //
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Print;        //
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Var;          //
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.While;        //
                                                                //
import java.io.IOException;                                     //
import java.nio.charset.StandardCharsets;                       //
import java.nio.file.Files;                                     //
import java.nio.file.Paths;                                     //
import java.util.List;                                          //
                                                                //
//////////////////////////////////////////////////////////////////
import dk.sdu.teaching.compiler.fs24.spl.scan.TokenType;

public class LLVMEmitter implements ExprVisitor<String>, StmtVisitor<Void>
{
    
    public void generateCode(List<Stmt> statements) 
    {
        for (Stmt s : statements) 
        {  
            compile(s);
        }
    }

    public void saveCode(String path) throws IOException
    {
        Files.write(Paths.get(path), IR.getBytes(StandardCharsets.UTF_8));
    }


    // constants
    String IR = "";
    String dent = "    ";
    
    private final String NL = System.lineSeparator();

    Integer varCount = 0;
    Integer lblCount = 0;

    ////////////////////////
    /// Helper functions  //
    ////////////////////////

    private Void compile(Stmt stmt) 
    {
        return stmt.accept(this);
    }

    private String compile(Expr expr) 
    {
        return expr.accept(this);
    }

    private String getNewLabel() 
    {
        // labels just need names, indent and the like handeled elsewhere.
        lblCount++;
        return "L" + lblCount;
    }

    private String getNewVar() 
    {
        // add % in front of name
        varCount++;
        return "%" + varCount;
    }

    //////////////////
    /// statements ///
    //////////////////
    
    @Override
    public Void visitExpressionStmt(Expression stmt) 
    {
       compile(stmt.expression); 
       return null;
    }

    @Override
    public Void visitIfStmt(If stmt) 
    {
        String cond = compile(stmt.condition); //adds the condition to the IR, returns name of var.
        String ifTrue = getNewLabel(); 
        String ifFalse = getNewLabel();

        IR += dent + "br i1 " + cond + ", label %" + ifTrue + ", label %" + ifFalse + NL;

        IR += ifTrue + ":" + NL;
        compile(stmt.thenBranch);
        if (stmt.elseBranch != null)
        {
            IR += ifFalse + ":" + NL;
            compile(stmt.elseBranch);
            return null;
        }
        else 
        {
            return null;
        }
    }


    @Override
    public Void visitVarStmt(Var stmt) 
    {
        String varName = stmt.name.lexeme;
        String varExpr = compile(stmt.initializer);
        IR += dent + "%" + varName + " = " + varExpr + NL;

        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) 
    {
        String testLabel = getNewLabel();
        IR += testLabel + ":" + NL;
        String cond = compile(stmt.condition);
        String ifTrue = getNewLabel();
        String ifFalse = getNewLabel();

        IR += dent + "br i1 " + cond + ", label %" + ifTrue + " label %" + ifFalse + NL;

        IR += ifTrue + ":" + NL;
        compile(stmt.body);
        IR += dent + "br label %" + testLabel + NL;
        IR += ifFalse + ":" + NL;
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) 
    {
        for (Stmt s : stmt.statements) 
        {  
            compile(s);
        }
        return null;
    }


    //////////////////
    /// Expressions //
    //////////////////

    @Override
    public String visitAssignExpr(Assign expr) 
    {   // assign var name to value
        String contains = compile(expr.value);  
        String var = "%" + expr.name.lexeme + " = " + contains + NL;
        IR+= dent + var;
        return var;
    }

    @Override
    public String visitBinaryExpr(Binary expr) 
    {
        String left = compile(expr.left);
        String right = compile(expr.right);
        String var = getNewVar();
        switch (expr.operator.type) 
        {
            case TokenType.PLUS:
                IR += dent + var + " = add i32 " + left + ", " + right + NL;
                break;
        
            case TokenType.MINUS:
                IR += dent + var + " = sub i32 " + left + ", " + right + NL;
                break;

            case TokenType.DIV:
                IR += dent+ var + " = udiv i32 " + left + ", " + right + NL;
                break;

            case TokenType.MULT:
                IR += dent+ var + " = mul i32 " + left + ", " + right + NL;
                break; 
            case TokenType.EQUAL_EQUAL:
                IR += dent+ var + " = icmp eq i32 " + left + ", " + right + NL;
                break;
        
            case TokenType.NOT_EQUAL:
                IR += dent+ var + " = icmp ne i32 " + left + ", " + right + NL;
                break;
            
            case TokenType.GREATER:
                IR += dent+ var + " = icmp ugt i32 " + left + ", " + right + NL;
                break;
            
            case TokenType.GREATER_EQUAL:
                IR += dent+ var + " = icmp uge i32 " + left + ", " + right + NL;
                break;
            
            case TokenType.LESS:
                IR += dent+ var + " = icmp ult i32 " + left + ", " + right + NL;
                break;

            case TokenType.LESS_EQUAL:
                IR += dent+ var + " = icmp ule i32 " + left + ", " + right + NL;
                break;
            default:
                IR += "BINARY DEFAULT CASE! unexpected thing happened.." + NL;  
        }
        return var;
    }

    @Override
    public String visitLiteralExpr(Literal expr) 
    {
        String var = getNewVar();
        if (expr.value instanceof Double) 
        {
            IR += dent + var + " = " + expr.value + NL;
        }
        else if (expr.value instanceof String )
        {
            IR += dent + var + " = " + "\"" + expr.value + "\"" + NL;
        }
        else if (expr.value instanceof Boolean)
        {
            IR += dent + var + " = " + expr.value + NL;
        }
        else 
        {
            IR += "LITERAL DEFAULT CASE! Unexpected thing...." + expr.value + NL;
        }

        return var;
    }

    @Override
    public String visitLogicalExpr(Logical expr) 
    {
        String left = compile(expr.left);
        String right = compile(expr.right);
        TokenType token = expr.operator.type;
        String var = getNewVar();
        if( token == TokenType.AND)
        {
            IR += dent + var + " = icmp AND" + " i32 " + left + ", " + right + NL;
        }
        
        if( token == TokenType.OR)
        {
            IR += dent + var + " = icmp OR" + " i32 " + left + ", " + right + NL;
        }

        return var;
    }

    @Override
    public String visitUnaryExpr(Unary expr) 
    {
        String rightSide = compile(expr.right);
        String var = getNewVar();
        switch (expr.operator.type) 
        {
        case TokenType.NOT:
            IR += dent + var + " = xor i1 true, " + rightSide + NL;
            break;

        case TokenType.MINUS:
            IR += dent + var + " = sub i32 0, " + rightSide + NL;
            break;

        default:
            // Should never be reached!
            // java wants support for all tokens..
        }
        return var;
    }


    @Override
    public String visitVariableExpr(Variable expr) 
    {
        String var = "%" + expr.name.lexeme;
        return var;
    }


    ////////////////////////////
    /// dont need to do these //
    ////////////////////////////

    @Override
    public Void visitPrintStmt(Print stmt) 
    {
        // Auto-generated method stub, don't have to do this
        throw new UnsupportedOperationException("Unimplemented method 'visitPrintStmt'");
    }
}
