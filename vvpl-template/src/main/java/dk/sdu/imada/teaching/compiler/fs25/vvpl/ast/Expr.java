package dk.sdu.imada.teaching.compiler.fs25.vvpl.ast;

import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.visitors.ExprVisitor;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.scan.*;;

/**
 * @author Sandra K. Johansen and Sofie Løfberg
 * @version CompilerConstruction FT 2025
 */
public abstract class Expr 
{
    public abstract <T> T accept(ExprVisitor<T> visitor);

    public static class Assignment extends Expr 
    {
        public final Expr expr;
        public final String name;
        
        public Assignment(String name, Expr expr) 
        {
            this.name = name;
            this.expr = expr;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitAssignment(this);
        }
        
    }

    public static class LogicExpr extends Expr 
    {
        //logicOr and logicAnd

        public final Expr left;
        public final Token operator;
        public final Expr right;

        public LogicExpr(Expr left, Token operator, Expr right) 
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitLogicExpr(this);
        }
        
    }

    public static class Binary extends Expr 
    {
        //Equality, compr and term

        public final Expr left;
        public final Token operator;
        public final Expr right;

        public Binary(Expr left, Token operator, Expr right) 
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitBinary(this);
        }
    }

    public static class Unary extends Expr 
    {
        public final Expr expr;
        public final Token token;

        public Unary(Token token, Expr expr) 
        {
            this.expr = expr;
            this.token = token;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitUnary(this);
        }

    }

    public static class Literal extends Expr 
    {
        public final Token cast_type;
        public final Object object;

        public Literal(Token type, Object object) 
        {
            this.cast_type = type;
            this.object = object;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitLiteral(this);
        }

    }

    public static class Variable extends Expr 
    {
        public final Token type;
        public final String name;

        public Variable(Token type, String name) 
        {
            this.type = type;
            this.name = name;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitVariable(this);
        }
    }

    public static class Grouping extends Expr 
    {
        public final Token cast_type;
        public final Expr expr;

        public Grouping (Token type, Expr expr) 
        {
            this.cast_type = type;
            this.expr = expr;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) 
        {
            return visitor.visitGrouping(this);
        }
        
    }
      
}
