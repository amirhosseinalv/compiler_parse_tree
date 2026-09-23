package ofp;

public class OFPParamSymbol extends OFPSymbol {

    public OFPParamSymbol(String name, OFPType type) {
        super(name, type);
    }
    
    @Override 
    public String toString() {
        return "ParamSymbol(name=" + getName() + ", type=" + getType() + ")";
    }

}
