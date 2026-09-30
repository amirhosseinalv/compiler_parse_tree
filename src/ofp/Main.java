/**
 * Entry point of the OFP compiler. It handles reading the input source file, 
 * initializes the ANTLR lexer and parser, generates the parse tree, and 
 * coordinates the tree traversal using a ParseTreeWalker to trigger listeners.
 */

package ofp;
import java.io.IOException;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.gui.Trees;
import generated.ofpLexer;
import generated.ofpParser;

public class Main  {

    public static void main(String[] args)  {
        System.out.println(OFPType.INT);
        String testProgram = args.length > 0 ? args[0] : "test.ofp";
        
        if ( !testProgram.endsWith(".ofp") ) {
            System.out.println("\nPrograms most end with suffix .ofp! Found "+testProgram);
            System.exit(-1);
        }
        System.out.println("Reading test program from: "+testProgram);
        
        System.out.println("\nParsing started");
        ofpParser parser = null;
        ofpParser.ProgramContext root = null;
        try {
            CharStream inputStream = CharStreams.fromFileName(testProgram);
            ofpLexer lexer = new ofpLexer( inputStream );		
            parser = new ofpParser(new BufferedTokenStream(lexer));	
            root = parser.program();
        } catch (IOException e) {				
            e.printStackTrace();
        } 
        System.out.println("\nParsing completed");

        //Print listener
        ParseTreeWalker walker = new ParseTreeWalker();
        PrintListener listener = new PrintListener();
        walker.walk(listener, root);
        System.out.println("\nPrint listener completed");

        //Symbol table listener
        SymbolTableListener symbolTableListener = new SymbolTableListener();
        walker.walk(symbolTableListener, root);
        System.out.println("\nSymbol table listener completed");
        System.out.println("\nSymbol table listener error count: " + symbolTableListener.getErrorCount());
        OFPScope globalScope = symbolTableListener.getScopes().get(root);
        globalScope.printTree(0);

        //Check ref Listener
        CheckRefListener checkRefListener = new CheckRefListener(symbolTableListener.getScopes());
        walker.walk(checkRefListener, root);
        System.out.println("\nCheck reference listener completed");
        System.out.println("Check reference error count: " + checkRefListener.getErrorCount());


        //Final debugging
        int totalErrors = symbolTableListener.getErrorCount() + checkRefListener.getErrorCount();
        if(totalErrors > 0){
            System.out.println(totalErrors + " total errors found");
        } else{
            System.out.println("No errors found");
        }

        //Trees.inspect(root, parser);
        
    }
}
