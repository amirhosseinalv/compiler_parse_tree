package ofp;
import generated.ofpBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;


public class PrintListener extends ofpBaseListener {
    private int depth = 0;
    
    @Override 
    public void enterEveryRule(ParserRuleContext ctx){
        System.out.println("- ".repeat(depth) + ctx.getClass().getSimpleName());
        depth++;
    }

    @Override 
    public void exitEveryRule(ParserRuleContext ctx){
        depth--;
    }


}
