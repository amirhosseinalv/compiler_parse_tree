/**
 * Main.java
 * 10 sep. 2023
 * jlnmsi
 * 
 * Starting point for the ofp compiler
 */
package ofp;

import java.io.IOException;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
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

        Trees.inspect(root, parser);
        
    }
}
