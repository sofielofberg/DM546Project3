package dk.sdu.imada.teaching.compiler.fs25.vvpl.interpret;

import java.util.List;

import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.Assignment;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.Binary;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.Grouping;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.Variable;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.Literal;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.LogicExpr;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.Unary;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.Block;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.ExprStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.IfStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.PrintStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.VarDecl;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.WhileStmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.visitors.ExprVisitor;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.visitors.StmtVisitor;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.scan.TokenType;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.scan.Token;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {
    private List<Stmt> program;

    private Enviroment currentEnviroment = new Enviroment(); 

    public Interpreter(List<Stmt> program) {
        this.program = program;
    }

    public void interpret() {
        try {
            for (Stmt stmt : program) {
                stmt.accept(this);
            }
        } catch (RuntimeInterpretationError error) {
            error.printStackTrace();
            System.exit(-1);
        }
        
    }

    @Override
    public Void visitExprStmt(ExprStmt Stmt) {
        Stmt.expr.accept(this);
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt Stmt) {
    }

    @Override
    public Void visitWhileStmt(WhileStmt Stmt) {
    }

    @Override
    public Void visitPrintStmt(PrintStmt Stmt) {
        System.out.println(Stmt.expr.accept(this));
        return null;
    }
        

    @Override
    public Void visitBlock(Block Stmt) {
    }

    @Override
    public Void visitVarDecl(VarDecl Stmt) {
        Object value = null;
        if (Stmt.expr != null) {
            value = Stmt.expr.accept(this);
        }
        
        currentEnviroment.define(Stmt.id, value);
        return null;
    }

    @Override
    public Object visitAssignment(Assignment expr) {
        Object value = expr.expr.accept(this);
        currentEnviroment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitLogicExpr(LogicExpr expr) {
    }

    @Override
    public Object visitBinary(Binary expr) {
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        switch (expr.operator.type) {
            case TokenType.SUB:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) - ((Double) right);
            
            case TokenType.DIV:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) / ((Double) right);

            case TokenType.MULTIPLY:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) * ((Double) right);
            
            case TokenType.PLUS:
                if (left instanceof String && right instanceof String) {
                    return ((String) left) + ((String) right);
                } 
                if (left instanceof Double && right instanceof Double){
                    return ((Double) left) + ((Double) right);
                } 
                else {
                    throw new RuntimeInterpretationError(expr.operator, "Operands must be two numbers or two strings");
                }

            case TokenType.GREATER:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) > ((Double) right);

            case TokenType.GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) >= ((Double) right);
            
            case TokenType.LESS:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) < ((Double) right);

            case TokenType.LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return ((Double) left) <= ((Double) right);
            
            case TokenType.NOT_EQUALS:
                return !isEqual(left, right);

            case TokenType.EQUALS:
                return isEqual(left, right);

            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object visitUnary(Unary expr) {
        Object value = expr.expr.accept(this);

        switch (expr.token.type) {
            case TokenType.NOT:
                if (value instanceof Boolean) {
                    return !((Boolean) value);
                } else {
                    throw new RuntimeInterpretationError(expr.token, "Operand must be a Boolean");
                }
            
            case TokenType.SUB:
                if (value instanceof Double) {
                    return -((Double) value);
                } else {
                    throw new RuntimeInterpretationError(expr.token, "Operand must be a number");
                }

            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return expr.cast_type.literal;
    }

    @Override
    public Object visitVariable(Variable expr) {
        return currentEnviroment.get(expr.name);
    }

    @Override
    public Object visitGrouping(Grouping expr) {
        return expr.expr.accept(this);
    }

    ////////////////////
    // Helper methods //
    ////////////////////
    
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeInterpretationError(operator, "Operands must be numbers");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    //////////////////////
    // Custom exception //
    /////////////////////
    
    public class RuntimeInterpretationError extends RuntimeException {
        final Token token;

        RuntimeInterpretationError(Token token, String message) {
            super(message);
            this.token = token;
        }
}
    
}
