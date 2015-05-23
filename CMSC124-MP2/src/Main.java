
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author obligado
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//Reads a sample.c file at project root directory
		File inputFile = new File("sample.myhl");
		Tokenizer tokenizer = new Tokenizer(inputFile);
		tokenizer.nextToken();
		while (!tokenizer.currentToken.type.equals("eof")) {
//			System.out.println(tokenizer.currentToken);
			tokenizer.nextToken();
		}
		tokenizer.printTokenTypes();
		System.out.println("\n");
		tokenizer.printTokenList();
	}
	
}
