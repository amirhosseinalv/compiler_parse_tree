package ofp;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import generated.ofpBaseVisitor;
import generated.ofpParser;

public class TypeCheckVisitor extends ofpBaseVisitor<OFPType> {

        private final ParseTreeProperty<OFPScope> scopes;
        private OFPScope currentScope;
        private OFPScope globalScope; 
        private String currentFunction;
        private int errorCount = 0;

    public TypeCheckVisitor(ParseTreeProperty<OFPScope> scopes){  
        this.scopes = scopes;   
    }

    public int getErrorCount(){
        return errorCount;
    }

    @Override
    public OFPType visitFuncDecl(ofpParser.FuncDeclContext ctx){
        currentScope = scopes.get(ctx);
        currentFunction = ctx.getChild(1).getText();
        visitChildren(ctx);
        currentScope = currentScope.getEnclosingScope();
        return null;
    }

    @Override 
    public OFPType visitProgram(ofpParser.ProgramContext ctx){
        globalScope = scopes.get(ctx);
        currentScope = globalScope;
        visitChildren(ctx);
        return null;
    }

    @Override 
    public OFPType visitMain(ofpParser.MainContext ctx){
        currentScope = scopes.get(ctx);
        visitChildren(ctx);
        currentScope = currentScope.getEnclosingScope();
        return null;
    }

    @Override 
    public OFPType visitBlock(ofpParser.BlockContext ctx){
        OFPScope blockScope = scopes.get(ctx);
        if(blockScope != null){
            currentScope = blockScope;
            visitChildren(ctx);
            currentScope = currentScope.getEnclosingScope();
            return null;
        } else {
            visitChildren(ctx);
            return null;
        }
    }

    @Override 
    public OFPType visitIntExpr(ofpParser.IntExprContext ctx){
        return OFPType.INT;
    }

    @Override 
    public OFPType visitBoolExpr(ofpParser.BoolExprContext ctx){
        return OFPType.BOOL;
    }

    @Override 
    public OFPType visitFloatExpr(ofpParser.FloatExprContext ctx){
        return OFPType.FLOAT;
    }

    @Override 
    public OFPType visitCharExpr(ofpParser.CharExprContext ctx){
        return OFPType.CHAR;
    }

    @Override 
    public OFPType visitStringExpr(ofpParser.StringExprContext ctx){
        return OFPType.STRING;
    }

    @Override 
    public OFPType visitIdExpr(ofpParser.IdExprContext ctx){
        String id = ctx.getChild(0).getText();
        OFPSymbol symbol = currentScope.resolve(id);
        if(symbol == null){
            // TOCHECK: check if filippo is handeling this error in a different way, if not, we should handle it here
            // System.err.println("Error: Variable " + id + " is not declared.");
            // errorCount++;
            return OFPType.ERROR;
        } else {
            return symbol.getType();
        }
    }
} 