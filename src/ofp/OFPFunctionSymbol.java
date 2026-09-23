package ofp;

import java.util.ArrayList;
import java.util.List;

public class OFPFunctionSymbol extends OFPSymbol {

    private final List<OFPParamSymbol> params = new ArrayList<>();


    public OFPFunctionSymbol(String name, OFPType type) {
        super(name, type);
    }


    public void addParam(OFPParamSymbol param){
        params.add(param);
    }

    public List<OFPParamSymbol> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "FunctionSymbol(name=" + getName() + ", type=" + getType() + ", params=" + params + ")";
    }
}
