package dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.visitors;

import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Expr.*;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt;
import dk.sdu.imada.teaching.compiler.fs25.vvpl.ast.Stmt.*;

/**
 * @author Sandra K. Johansen and Sofie Løfberg
 * @version CompilerConstruction FT 2025
 */

public class ASTPrinter implements ExprVisitor<String>, StmtVisitor<String> 
{
  private final String NL = System.lineSeparator();

  private int indent = 0;

  private String space()
  {
    return "  ".repeat(indent);
  }

  public String print(Stmt stmt) 
  {
    return stmt.accept(this);
  }

  @Override
  public String visitExprStmt(ExprStmt Stmt) 
  {
    String string = "ExprStmt" + NL;
    indent++;
    string += space() + Stmt.expr.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitIfStmt(IfStmt Stmt) 
  {
    String string = "IfStmt" + NL;
    indent++;
    string += space() + Stmt.guard.accept(this); //throw to correct handler
    string += space() + Stmt.ifStmt.accept(this); //throw to correct handler
    string += space() + Stmt.elseStmt.accept(this);
    indent--;
    return string;
  }

  @Override
  public String visitWhileStmt(WhileStmt Stmt) 
  {
    String string = "WhileStmt" + NL;
    indent++;
    string += space() + Stmt.guard.accept(this); //throw to correct handler
    string += space() + Stmt.whileStmt.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitPrintStmt(PrintStmt Stmt) 
  {
    String string = "PrintStmt" + NL;
    indent++;
    string += space() + Stmt.expr.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitBlock(Block Stmt) 
  {
    String string = "BlockStmt" + NL;
    indent++;
    for (Stmt statement : Stmt.decls) // we wanna throw each decl to accept one at a time
    {
      string += space() + statement.accept(this); //throw to correct handler
    }
    indent--;
    return string;
  }

  @Override
  public String visitVarDecl(Stmt.VarDecl Stmt) 
  {
    String string = "VarDecl" + NL;
    indent++;
    string += space() + Stmt.id + NL;
    string += space() + Stmt.type.lexeme + NL;
    if(Stmt.expr != null)
    {
      string += space() + Stmt.expr.accept(this); //throw to correct handler
    }
    indent--;
    return string;
  }

  @Override
  public String visitAssignment(Assignment expr) 
  {
    String string = "AssignExpr" + NL;
    indent++;
    string += space() + expr.name + NL; //throw to correct handler
    string += space() + expr.expr.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitLogicExpr(LogicExpr expr) 
  {
    String string = "LogicalExpr" + NL;
    indent++;
    string += space() + expr.left.accept(this); //throw to correct handler
    string += space() + expr.operator.lexeme + NL;
    string += space() + expr.right.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitBinary(Binary expr) 
  {
    String string = "BinaryExpr" + NL;
    indent++;
    string += space() + expr.left.accept(this); //throw to correct handler
    string += space() + expr.operator.lexeme + NL;
    string += space() + expr.right.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitUnary(Unary expr) 
  {
    String string = "UnaryExpr" + NL;
    indent++;
    string += space() + expr.token.lexeme + NL;
    string += space() + expr.expr.accept(this); //throw to correct handler
    indent--;
    return string;
  }

  @Override
  public String visitIdentifier(Identifier expr) 
  {
    String string = "VariableExpr" + NL;
    indent++;
    if(expr.type != null)
    {
      string += space() + "Cast_To " + expr.type.lexeme + NL;
    }
    string += space() + expr.string + NL;
    indent--;
    return string;
  }

  @Override
  public String visitLiteral(Literal expr) 
  {
    String string = "LiteralExpr" + NL;
    indent++;
    if(expr.type != null)
    {
      string += space() + "Cast_To " + expr.type.lexeme + NL;
    }
    if (expr.object instanceof String)
    {
      string += space() + "\"" + expr.object + "\"" + NL;
    }
    else
    {
      string += space() + expr.object + NL;
    }
    indent--;
    return string;
  }

  @Override
  public String visitGrouping(Grouping expr) 
  {
    String string = "LiteralExpr" + NL;
    indent++;
    if(expr.type != null)
    {
      string += space() + "Cast_To " + expr.type.lexeme + NL;
    }
    string += space() + expr.accept(this);
    indent--;
    return string;
  }
}
