/**
 * Represents a lexical scope (or environment) in the code. It stores a map of 
 * symbols declared within that specific block and maintains a reference to its 
 * enclosing (parent) scope, enabling recursive hierarchical name resolution.
 */

package ofp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OFPScope {
    private OFPScope enclosingScope; // null if global (outermost) scope
    private Map<String, OFPSymbol> symbols = new LinkedHashMap<>();
    private final List<OFPScope> children = new ArrayList<>();

    public OFPScope(OFPScope enclosingScope){ 
        this.enclosingScope = enclosingScope;
        if(enclosingScope != null){
            enclosingScope.addChild(this);
        }
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

    public void addChild(OFPScope childScope){
        children.add(childScope);
    }

    public void printTree(int depth){
        System.out.println("- ".repeat(depth) + symbols.toString());
        for (OFPScope child : children){
            child.printTree(depth + 1);
        }
    }

}
