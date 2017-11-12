/**
 * Lispreter.java is a part of Lispreter. 
 */
package interpreter;

import interpreter.lexer.Lexer;
import interpreter.parser.Parser;

import java.io.FileInputStream;
import java.io.FileWriter;

/**
 * @author Anand
 *
 */
public class Lispreter {

	public static void main(String[] args) {
		try {
			Lexer lexer;

			if (Flag.INPUT_FILE.containsFlag(args)) {
				lexer = new Lexer(new FileInputStream(
						Flag.INPUT_FILE.getParts(args)[0]));
			} else {
				lexer = new Lexer(System.in);
			}

			Parser parser;
			if (Flag.OUTPUT_FILE.containsFlag(args)) {
				parser = new Parser(lexer.getTokens(), new FileWriter(
						Flag.OUTPUT_FILE.getParts(args)[0]));
			} else {
				parser = new Parser(lexer.getTokens());
			}
			parser.eval();
		}
		catch (Exception e) {
			System.out.println("Error occurred!");
			if (Flag.DEBUG.containsFlag(args)) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} else {
				System.out.println("Specify '-d' to debug the error!");
			}
		}
	}
}
