package ofp;

import java.util.ArrayList;
import java.util.List;

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
        currentFunction = "main";
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


    @Override 
    public OFPType visitAddSubExpr(ofpParser.AddSubExprContext ctx){
        OFPType lhs = visit(ctx.getChild(0));
        OFPType rhs = visit(ctx.getChild(2));

        if(rhs == OFPType.ERROR || lhs == OFPType.ERROR){
            return OFPType.ERROR;

        } else if(rhs != OFPType.INT && rhs != OFPType.FLOAT && rhs != OFPType.CHAR && lhs != OFPType.INT && lhs != OFPType.FLOAT && lhs != OFPType.CHAR){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Type mismatch! Found lhs type: " + lhs + "rhs: " + rhs);
            return OFPType.ERROR;}
        else if(rhs != lhs) {
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Type mismatch! Found lhs type: " + lhs + "rhs: " + rhs);
            return lhs;
        } else {
            return rhs;
        }
    }

    @Override 
    public OFPType visitMulDivExpr(ofpParser.MulDivExprContext ctx) {
        OFPType lhs = visit(ctx.getChild(0));
        OFPType rhs = visit(ctx.getChild(2));

        if(rhs == OFPType.ERROR || lhs == OFPType.ERROR){
            return OFPType.ERROR;
        } else if(rhs != lhs) {
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Type mismatch! Found lhs type: " + lhs + "rhs: " + rhs);
            return lhs;
        } else {
            return rhs;
        }
    }

    @Override 
    public OFPType visitCompareExpr(ofpParser.CompareExprContext ctx) {
        OFPType lhs = visit(ctx.getChild(0));
        OFPType rhs = visit(ctx.getChild(2));

        if(rhs == OFPType.ERROR || lhs == OFPType.ERROR){
            return OFPType.ERROR;
        } else if(rhs != lhs) {
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Type mismatch! Found lhs type: " + lhs + "rhs: " + rhs);
            return OFPType.BOOL;
        } else {
            return OFPType.BOOL;
        }
    }

    @Override 
    public OFPType visitNegExpr(ofpParser.NegExprContext ctx) {
        OFPType type = visit(ctx.getChild(1));
        if(type == OFPType.ERROR){
            return OFPType.ERROR;
        } else if (type != OFPType.INT && type != OFPType.FLOAT) {
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Type mismatch! Found type: " + type);
            return OFPType.ERROR;
        } else {
            return type;
        }
    }

    @Override 
    public OFPType visitParenExpr(ofpParser.ParenExprContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override 
    public OFPType visitWhileStmt(ofpParser.WhileStmtContext ctx) {
        OFPType condition = visit(ctx.getChild(2));

        if (condition == OFPType.ERROR){
            visit(ctx.getChild(4));
            return OFPType.ERROR;
        } else if (condition != OFPType.BOOL) {
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Condition for while statement is not correct!");
            visit(ctx.getChild(4));
            return OFPType.ERROR;
        } else {
            visit(ctx.getChild(4));
            return null;
        }
    }

    @Override 
    public OFPType visitIfStmt(ofpParser.IfStmtContext ctx){
        OFPType condition = visit(ctx.getChild(2));
        int numberOfChildren = ctx.getChildCount();

        if (condition == OFPType.ERROR){
            visit(ctx.getChild(4));
            if (numberOfChildren == 7){
                visit(ctx.getChild(6));
            }
            return OFPType.ERROR;
        } else if (condition != OFPType.BOOL) {
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): If statement is not correct!");
            if (numberOfChildren == 7){
                visit(ctx.getChild(6));
            }
            visit(ctx.getChild(4));
            return OFPType.ERROR;
        } else {
            visit(ctx.getChild(4));
            if (numberOfChildren == 7){
                visit(ctx.getChild(6));
            }
            return null;
        }
    }

    @Override 
    public OFPType visitPrintStmt(ofpParser.PrintStmtContext ctx){
        OFPType printExpr = visit(ctx.getChild(2));
        if (printExpr == OFPType.ERROR){
            return OFPType.ERROR;
        } else if (printExpr != OFPType.INT && printExpr != OFPType.FLOAT && printExpr != OFPType.BOOL && printExpr != OFPType.CHAR && printExpr != OFPType.STRING){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected INT, FLOAT, BOOL, CHAR or STRING but got: " + printExpr);
            return OFPType.ERROR;
        } else {
            return null;
        }
    }

    @Override 
    public OFPType visitLengthExpr(ofpParser.LengthExprContext ctx){
        OFPType arrayLengthExpr = visit(ctx.getChild(0));
        if (arrayLengthExpr == OFPType.ERROR){
            return OFPType.ERROR;
        } else if (arrayLengthExpr != OFPType.STRING && arrayLengthExpr != OFPType.CHAR_ARRAY && arrayLengthExpr != OFPType.INT_ARRAY && arrayLengthExpr != OFPType.FLOAT_ARRAY){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected STRING, CHAR_ARRAY, INT_ARRAY or FLOAT_ARRAY but got: " + arrayLengthExpr);
            return OFPType.ERROR;
        } else {
            return OFPType.INT;
        }
    }


    @Override 
    public OFPType visitIndexExpr(ofpParser.IndexExprContext ctx){
        OFPType indexExpr = visit(ctx.getChild(0));
        OFPType index = visit(ctx.getChild(2));
        if (indexExpr == OFPType.ERROR || index == OFPType.ERROR){
            return OFPType.ERROR;
        } else if (indexExpr != OFPType.STRING && indexExpr != OFPType.CHAR_ARRAY && indexExpr != OFPType.INT_ARRAY && indexExpr != OFPType.FLOAT_ARRAY){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected STRING but got: " + indexExpr);
            return OFPType.ERROR;
        } else if (index != OFPType.INT){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected INT but got: " + index);
            return OFPType.ERROR;
        } else {
            if (indexExpr == OFPType.STRING || indexExpr == OFPType.CHAR_ARRAY){
                return OFPType.CHAR;
            } else if (indexExpr == OFPType.INT_ARRAY){
                return OFPType.INT;
            } else if (indexExpr == OFPType.FLOAT_ARRAY){
                return OFPType.FLOAT;
            }
            return null;
        }
    }

    @Override 
    public OFPType visitNewArrayExpr(ofpParser.NewArrayExprContext ctx){
       String arrayTypeName = ctx.getChild(1).getText() + "[]";
       OFPType arrayType = OFPType.get(arrayTypeName);
       OFPType arraySize = visit(ctx.getChild(3));
         if (arraySize == OFPType.ERROR ){
            return OFPType.ERROR;
        } else if (arraySize != OFPType.INT){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected INT but got: " + arraySize);
            return OFPType.ERROR;
        }
         else {
            return arrayType;   

    }}

    @Override 
    public OFPType visitArrayLiteralExpr(ofpParser.ArrayLiteralExprContext ctx){
        int numOfElements = ctx.getChildCount();
        if (numOfElements == 2){
            return null;
        } else {
            OFPType firstElementType = visit(ctx.getChild(1));
            for (int i = 1; i < numOfElements - 1; i+=2){
                OFPType elementType = visit(ctx.getChild(i));
                if (elementType == OFPType.ERROR){
                    return OFPType.ERROR;
                } else if (elementType != OFPType.INT && elementType != OFPType.FLOAT && elementType != OFPType.CHAR){
                    errorCount++;
                    System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected INT, FLOAT or CHAR but got: " + elementType);
                    return OFPType.ERROR;
                } else if (elementType != firstElementType){
                    errorCount++;
                    System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected all elements to be of the same type but got: " + elementType + " and " + visit(ctx.getChild(1)));
                    return OFPType.ERROR;
                }
            }
            return arrayOf(firstElementType);
        }
    }

    @Override 
    public OFPType visitVarDecl(ofpParser.VarDeclContext ctx){
        String varTypeName = ctx.getChild(0).getText();
        OFPType varType = OFPType.get(varTypeName);
        int numOfChildren = ctx.getChildCount();
        
        if (numOfChildren < 4){
            return varType;
        } else {
            OFPType varValueType = visit(ctx.getChild(3));
            if (varValueType == OFPType.ERROR){
                return null;
            } else if (varValueType != varType){
                errorCount++;
                System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected type: " + varType + " but got: " + varValueType);
                return OFPType.ERROR;
            } else {
                return varType;
            }
        }
    }

    @Override 
    public OFPType visitArrayDecl(ofpParser.ArrayDeclContext ctx){
        String arrayTypeName = ctx.getChild(0).getText() + "[]";
        OFPType arrayType = OFPType.get(arrayTypeName);
        int numOfChildren = ctx.getChildCount();
        if (numOfChildren < 6){
            return arrayType;
        } else {
            OFPType arraySizeType = visit(ctx.getChild(5));
            if (arraySizeType == OFPType.ERROR){
                return null;
            } else if (arraySizeType != arrayType){
                errorCount++;
                System.out.println("Error (line " + ctx.getStart().getLine() + "): Expected type: " + arrayType + " but got: " + arraySizeType);
                return OFPType.ERROR;
            } else {
                return arrayType;
            }
        }
    }

    /**
     * The array type that has the given type as its elements, e.g. INT -> INT_ARRAY.
     * OFP has no bool[] or string[], so every other type gives ERROR.
     */
    @Override
    public OFPType visitAssignStmt(ofpParser.AssignStmtContext ctx){
        // ID '=' expr ';'
        String varName = ctx.getChild(0).getText();
        OFPSymbol varSym = currentScope.resolve(varName);
        OFPType valueType = visit(ctx.getChild(2));

        // Undeclared names are reported by the CheckRefListener, not here
        if (varSym == null || valueType == OFPType.ERROR){
            return null;
        }
        if (varSym.getType() != valueType){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Cannot assign " + valueType
                    + " to '" + varName + "' of type " + varSym.getType() + " in function " + currentFunction);
        }
        return null;
    }

    @Override
    public OFPType visitArrayAssignStmt(ofpParser.ArrayAssignStmtContext ctx){
        // ID '[' expr ']' '=' expr ';'
        String arrayName = ctx.getChild(0).getText();
        OFPSymbol arraySym = currentScope.resolve(arrayName);
        OFPType indexType = visit(ctx.getChild(2));
        OFPType valueType = visit(ctx.getChild(5));

        if (arraySym == null){
            return null;
        }
        // The target must be something we can index, and the index itself must be an int
        OFPType elementType = elementType(arraySym.getType());
        if (elementType == null){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): '" + arrayName
                    + "' has type " + arraySym.getType() + " and cannot be indexed");
            return null;
        }
        if (indexType != OFPType.ERROR && indexType != OFPType.INT){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Array index must be int but got: " + indexType);
        }
        if (valueType != OFPType.ERROR && valueType != elementType){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Cannot assign " + valueType
                    + " to an element of " + arraySym.getType());
        }
        return null;
    }

    @Override
    public OFPType visitReturnStmt(ofpParser.ReturnStmtContext ctx){
        // 'return' expr? ';'
        OFPType declaredType = currentFunctionReturnType();

        if (ctx.getChildCount() == 2){          // return;
            if (declaredType != null && declaredType != OFPType.VOID){
                errorCount++;
                System.out.println("Error (line " + ctx.getStart().getLine() + "): Function " + currentFunction
                        + " must return " + declaredType);
            }
            return null;
        }

        OFPType returnedType = visit(ctx.getChild(1));   // return expr;
        if (returnedType == OFPType.ERROR || declaredType == null){
            return null;
        }
        if (declaredType == OFPType.VOID){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Void function " + currentFunction
                    + " cannot return a value");
        } else if (returnedType != declaredType){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Function " + currentFunction
                    + " must return " + declaredType + " but got: " + returnedType);
        }
        return null;
    }

    @Override
    public OFPType visitFuncCall(ofpParser.FuncCallContext ctx){
        // ID '(' (expr (',' expr)*)? ')'
        String funcName = ctx.getChild(0).getText();

        // The arguments are always visited, so errors inside them are reported too
        List<OFPType> argTypes = new ArrayList<>();
        for (int i = 2; i < ctx.getChildCount() - 1; i += 2){
            argTypes.add(visit(ctx.getChild(i)));
        }

        // Functions live in the global scope, so a local variable cannot hide them
        OFPSymbol sym = globalScope.resolve(funcName);
        if (!(sym instanceof OFPFunctionSymbol)){
            return OFPType.ERROR;      // undeclared function: reported by the CheckRefListener
        }
        OFPFunctionSymbol funcSym = (OFPFunctionSymbol) sym;
        List<OFPType> paramTypes = funcSym.getParamTypes();

        if (argTypes.size() != paramTypes.size()){
            errorCount++;
            System.out.println("Error (line " + ctx.getStart().getLine() + "): Function " + funcName
                    + " takes " + paramTypes.size() + " argument(s) but got " + argTypes.size());
            return funcSym.getType();
        }
        for (int i = 0; i < paramTypes.size(); i++){
            OFPType argType = argTypes.get(i);
            if (argType != OFPType.ERROR && argType != paramTypes.get(i)){
                errorCount++;
                System.out.println("Error (line " + ctx.getStart().getLine() + "): Argument " + (i + 1)
                        + " of " + funcName + " must be " + paramTypes.get(i) + " but got: " + argType);
            }
        }
        return funcSym.getType();       // a call has the function's return type
    }

    @Override
    public OFPType visitCallExpr(ofpParser.CallExprContext ctx){
        return visit(ctx.getChild(0));  // the funcCall child carries the type
    }

    /** The declared return type of the function being visited, or null if it cannot be found */
    private OFPType currentFunctionReturnType(){
        OFPSymbol sym = globalScope.resolve(currentFunction);
        if (sym instanceof OFPFunctionSymbol){
            return sym.getType();
        }
        return null;
    }

    /**
     * The type of the elements of an indexable type, e.g. INT_ARRAY -> INT.
     * Returns null for types that cannot be indexed at all.
     */
    private OFPType elementType(OFPType type){
        if (type == OFPType.INT_ARRAY){
            return OFPType.INT;
        } else if (type == OFPType.FLOAT_ARRAY){
            return OFPType.FLOAT;
        } else if (type == OFPType.CHAR_ARRAY || type == OFPType.STRING){
            return OFPType.CHAR;
        } else {
            return null;
        }
    }

    private OFPType arrayOf(OFPType elementType){
        if (elementType == OFPType.INT){
            return OFPType.INT_ARRAY;
        } else if (elementType == OFPType.FLOAT){
            return OFPType.FLOAT_ARRAY;
        } else if (elementType == OFPType.CHAR){
            return OFPType.CHAR_ARRAY;
        } else {
            return OFPType.ERROR;
        }
    }
} 