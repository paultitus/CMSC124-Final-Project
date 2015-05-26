package MyHL;


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
		tokenizer.tokenize();
		System.out.println("Tokenizer:\n");
		tokenizer.printTokenList();
		
		SyntacticAnalyzer syntactic = new SyntacticAnalyzer(tokenizer.getTokenListNoComments());
		syntactic.analyze();
		System.out.println("\n\nSyntactic Analysis:\n");
		syntactic.printErrors();
		
		if (syntactic.getErrors().isEmpty()) {
			SemanticAnalyzer semantic;
			if (syntactic.getAST() != null) {
				semantic = new SemanticAnalyzer(syntactic.getAST());
				semantic.analyze();
				System.out.println("\n\nSemantic Analysis:\n");
				semantic.printErrors();
			}
		}
	}
	
}
