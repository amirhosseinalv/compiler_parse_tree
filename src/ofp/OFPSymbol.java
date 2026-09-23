package ofp;

public class OFPSymbol {
    private final String name;
    private final OFPType type;

    public OFPSymbol(String name, OFPType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public OFPType getType() {
        return type;
    }

    @Override 
    public String toString() {
        return "Symbol(name=" + name + ", type=" + type + ")";
    }

    
}
