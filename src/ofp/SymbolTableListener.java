package ofp;
import generated.ofpParser;
import generated.ofpBaseListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class SymbolTableListener extends ofpBaseListener {
    private OFPScope currentScope = null;
    private OFPFunctionSymbol currentFunctionSymbol = null; 
    private ParseTreeProperty<OFPScope> scopes = new ParseTreeProperty<OFPScope>();
    private int errorCount = 0;

    @Override
    public void enterProgram(ofpParser.ProgramContext ctx) {
        // enclosing scope == null for the global/program scope
        currentScope = new OFPScope(null);
        scopes.put(ctx, currentScope);
    }

    @Override 
    public void enterFuncDecl(ofpParser.FuncDeclContext ctx) {
        String returnTypeName = ctx.getChild(0).getText();
        OFPType returnType = OFPType.get(returnTypeName);

        String functionName = ctx.getChild(1).getText(); 

        currentFunctionSymbol = new OFPFunctionSymbol(functionName, returnType);
        currentScope.define(currentFunctionSymbol);

        currentScope = new OFPScope(currentScope);
        scopes.put(ctx, currentScope);
    }

    @Override
    public void exitFuncDecl(ofpParser.FuncDeclContext ctx) {
        currentScope = currentScope.getEnclosingScope();
        currentFunctionSymbol = null; // Reset the current function symbol when exiting the function declaration
    }

    @Override 
    public void enterValueParam(ofpParser.ValueParamContext ctx) {
        String paramType = ctx.getChild(0).getText();
        OFPType paramOFPType = OFPType.get(paramType);
        String paramName = ctx.getChild(1).getText();
        OFPParamSymbol paramSymbol = new OFPParamSymbol(paramName, paramOFPType);
        currentScope.define(paramSymbol);
        currentFunctionSymbol.addParam(paramSymbol);
    }

    @Override 
    public void enterArrayParam(ofpParser.ArrayParamContext ctx) {
        String paramType = ctx.getChild(0).getText() + "[]";
        OFPType paramOFPType = OFPType.get(paramType);

        String paramName = ctx.getChild(3).getText();
        OFPParamSymbol paramSymbol = new OFPParamSymbol(paramName, paramOFPType);
        currentScope.define(paramSymbol);
        currentFunctionSymbol.addParam(paramSymbol);
    }

    @Override 
    public void enterMain(ofpParser.MainContext ctx) {
        String returnTypeName = "void";
        OFPType returnType = OFPType.get(returnTypeName);

        String functionName = "main"; 

        currentFunctionSymbol = new OFPFunctionSymbol(functionName, returnType);
        currentScope.define(currentFunctionSymbol);

        currentScope = new OFPScope(currentScope);
        scopes.put(ctx, currentScope);
    }

    @Override 
    public void exitMain(ofpParser.MainContext ctx) {
        currentScope = currentScope.getEnclosingScope();
        currentFunctionSymbol = null; 
    }
}
    
