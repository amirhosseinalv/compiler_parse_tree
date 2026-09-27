/**
 * Represents a lexical scope (or environment) in the code. It stores a map of 
 * symbols declared within that specific block and maintains a reference to its 
 * enclosing (parent) scope, enabling recursive hierarchical name resolution.
 */

package ofp;
import java.util.LinkedHashMap;
import java.util.Map;

public class OFPScope {
    private OFPScope enclosingScope; // null if global (outermost) scope
    private Map<String, OFPSymbol> symbols = new LinkedHashMap<>();
    public OFPScope(OFPScope enclosingScope){ 
        this.enclosingScope = enclosingScope;
    }   
    public OFPSymbol resolveLocally(String name){
        return symbols.get(name);
    }

    public OFPSymbol resolve(String name) {
        OFPSymbol sym = symbols.get(name);
        if (sym != null) {
            return sym;
        } else if (enclosingScope != null) {
            return enclosingScope.resolve(name);
        } else {
            return null;
        }
    }
    
    public void define(OFPSymbol sym) { symbols.put(sym.getName(), sym); }

    public OFPScope getEnclosingScope() { return enclosingScope; }

    @Override 
    public String toString(){
        return symbols.toString();
    }
    // If a name cannot be resolved in this scope,
    // try enclosing/parent scope recursively.
    // If null is returned, name/identifier not found in the symbol table!
    // Print the contents of this scope
}
