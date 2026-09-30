/**
 * The core semantic analyzer extending ofpBaseListener. It traverses the parse 
 * tree to populate the symbol table, managing lexical scopes, functions, and 
 * parameters by dynamically creating and linking OFPScope and OFPSymbol objects.
 * It also checks for duplicates
 */

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
    public void enterMain(ofpParser.MainContext ctx) {
        String functionName = "main";

        //If main was already used in the global scope we won't add it to the map and report the error
        if(currentScope.resolveLocally(functionName) != null){
            errorCount++;
            System.out.println("Error: Duplicate main function declaration");
        } else{
            String returnTypeName = "void";
            OFPType returnType = OFPType.get(returnTypeName);
            currentFunctionSymbol = new OFPFunctionSymbol(functionName, returnType);
            currentScope.define(currentFunctionSymbol);
        }
        
        //Either way we enter the main scope
        currentScope = new OFPScope(currentScope);
        scopes.put(ctx, currentScope);
    }

    @Override 
    public void exitMain(ofpParser.MainContext ctx) {
        currentScope = currentScope.getEnclosingScope();
        currentFunctionSymbol = null; 
    }


    @Override 
    public void enterFuncDecl(ofpParser.FuncDeclContext ctx) {
        String functionName = ctx.getChild(1).getText();

        //If the function name was already used in the global scope we won't add it to the map and report the error
        if(currentScope.resolveLocally(functionName) != null){
            errorCount++;
            System.out.println("Error: Duplicate '" + functionName + "' function declaration");
        } else{
            String returnTypeName = ctx.getChild(0).getText();
            OFPType returnType = OFPType.get(returnTypeName);
            currentFunctionSymbol = new OFPFunctionSymbol(functionName, returnType);
            currentScope.define(currentFunctionSymbol);
        }
        
        //Either way we enter the function scope
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
        String paramName = ctx.getChild(1).getText();

        //If a parameter is duplicate we don't add it and report the error
        if(currentScope.resolveLocally(paramName) != null){
            errorCount++;
            System.out.println("Error: Duplicate '" + paramName + "' parameter declaration");
            return;
        } 

        //If it's not duplicate we proceed
        String paramType = ctx.getChild(0).getText();
        OFPType paramOFPType = OFPType.get(paramType);
        OFPParamSymbol paramSymbol = new OFPParamSymbol(paramName, paramOFPType);
        currentScope.define(paramSymbol);

        //If the function was duplicate currentFunctionSymbol is still null
        if(currentFunctionSymbol != null){
            currentFunctionSymbol.addParam(paramSymbol);
        }
    }

    //Same as enterValueParam method
    @Override 
    public void enterArrayParam(ofpParser.ArrayParamContext ctx) {
        String paramName = ctx.getChild(3).getText();

        if(currentScope.resolveLocally(paramName) != null){
            errorCount++;
            System.out.println("Error: Duplicate '" + paramName + "' array parameter declaration");

        }

        String paramType = ctx.getChild(0).getText() + "[]";
        OFPType paramOFPType = OFPType.get(paramType);
        OFPParamSymbol paramSymbol = new OFPParamSymbol(paramName, paramOFPType);
        currentScope.define(paramSymbol);
        
        if(currentFunctionSymbol != null){
            currentFunctionSymbol.addParam(paramSymbol);
        }
    }


    @Override 
    public void enterVarDecl(ofpParser.VarDeclContext ctx){
        String varName = ctx.getChild(1).getText();

        //If a variable is duplicate we don't add it and report the error
        if(currentScope.resolveLocally(varName) != null){
            errorCount++;
            System.out.println("Error: Duplicate '" + varName + "' variable declaration");
            return;
        }

        //If it's not duplicate we proceed
        String varTypeString = ctx.getChild(0).getText();
        OFPType varType = OFPType.get(varTypeString);
        OFPSymbol varSym = new OFPSymbol(varName, varType);
        currentScope.define(varSym);
    }

    //Same as enterVarDecl method
    @Override 
    public void enterArrayDecl(ofpParser.ArrayDeclContext ctx){
        String arrayName = ctx.getChild(3).getText();

        if(currentScope.resolveLocally(arrayName) != null){
            errorCount++;
            System.out.println("Error: Duplicate '" + arrayName + "' array variable declaration");
            return;
        }

        String arrayTypeString = ctx.getChild(0).getText() + "[]";
        OFPType arrayType = OFPType.get(arrayTypeString);
        OFPSymbol arraySym = new OFPSymbol(arrayName, arrayType);
        currentScope.define(arraySym);
    }


    @Override 
    public void enterBlock(ofpParser.BlockContext ctx) {
        if ((ctx.getParent() instanceof ofpParser.FuncDeclContext) || (ctx.getParent() instanceof ofpParser.MainContext)){
            return;
    }   else {
            currentScope = new OFPScope(currentScope);
            scopes.put(ctx, currentScope);
        }
    }

    @Override 
    public void exitBlock(ofpParser.BlockContext ctx){
        if (ctx.getParent() instanceof ofpParser.MainContext || ctx.getParent() instanceof ofpParser.FuncDeclContext) {
            return;
        } else {
            currentScope = currentScope.getEnclosingScope();
        }
    }


    public ParseTreeProperty<OFPScope> getScopes(){
        return scopes;
    }

    public int getErrorCount(){
        return errorCount;
    }

}