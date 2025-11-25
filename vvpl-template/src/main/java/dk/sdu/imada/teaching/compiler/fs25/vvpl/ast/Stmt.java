package dk.sdu.imada.teaching.compiler.fs25.vvpl.ast;

import java.util.List;

import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.visitors.StmtVisitor;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.scan.Token;

/**
 * @author Sandra K. Johansen and Sofie Løfberg
 * @version CompilerConstruction FT 2025
 */

public abstract class Stmt 
{
    public abstract <T> T accept(StmtVisitor<T> visitor);

    public static class ExprStmt extends Stmt 
    {
        public final Expr expr;

        public ExprStmt(Expr expr) 
        {
            this.expr = expr;
        }

        @Override
        public <T> T accept(StmtVisitor<T> visitor) 
        {
            return visitor.visitExprStmt(this);
        }
    }

    public static class IfStmt extends Stmt 
    {
        public final Expr guard;
        public final Stmt ifStmt;
        public final Stmt elseStmt;
        

        public IfStmt(Expr guard, Stmt ifStmt, Stmt elseStmt) 
        {
            this.guard = guard;
            this.ifStmt = ifStmt;
            this.elseStmt = elseStmt;
        }


        @Override
        public <T> T accept(StmtVisitor<T> visitor) 
        {
            return visitor.visitIfStmt(this);
        }

    }

    public static class WhileStmt extends Stmt 
    {
        public final Expr guard;
        public final Stmt whileStmt;

        public WhileStmt(Expr guard, Stmt whileStmt)
        {
            this.guard = guard;
            this.whileStmt = whileStmt;
        }

        @Override
        public <T> T accept(StmtVisitor<T> visitor) 
        {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class PrintStmt extends Stmt 
    {
        public final Expr expr;

        public PrintStmt(Expr expr) 
        {
            this.expr = expr;
        }

        @Override
        public <T> T accept(StmtVisitor<T> visitor) 
        {
            return visitor.visitPrintStmt(this);
        }

    }

    public static class Block extends Stmt 
    {
        public final List<Stmt> decls;

        public Block(List<Stmt> decls)
        {
            this.decls = decls;
        }

        @Override
        public <T> T accept(StmtVisitor<T> visitor) 
        {
            return visitor.visitBlock(this);
        }
    }

    public static class VarDecl extends Stmt 
    {
        public final String id;
        public final Token type;
        public final Expr expr;
        
        public VarDecl(String id, Token type, Expr expr) 
        {
            this.id = id;
            this.type = type;
            this.expr = expr;
        }

        @Override
        public <T> T accept(StmtVisitor<T> visitor) 
        {
            return visitor.visitVarDecl(this);
        }
    }
}