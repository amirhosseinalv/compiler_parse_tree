package ofp;

import generated.ofpBaseListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CheckRefListener extends ofpBaseListener{
    private ParseTreeProperty<OFPScope> scopes;
    private OFPScope currentScope;
    private OFPScope globalScope;
    private String currentFunction;
    int errorCount;

    public CheckRefListener(ParseTreeProperty<OFPScope> scopes){
        this.scopes = scopes;
        this.errorCount = 0;
    }


    public int getErrorCount(){
        return errorCount;
    }

}
