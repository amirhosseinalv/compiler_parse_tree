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
}
