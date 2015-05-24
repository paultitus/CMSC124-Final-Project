/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyHL;

import Tokens.Token;
import java.util.ArrayList;

/**
 *
 * @author obligado
 */
public class SyntacticAnalyzer {
	int index;
	Token nextToken;
	ArrayList<String> errors;
	ArrayList<Token> tokenList;
	
	SyntacticAnalyzer(ArrayList<Token> tokenList) {
		index = 0;
		errors = new ArrayList();
		this.tokenList = new ArrayList();
		this.tokenList = tokenList;
	}
	
	public void analyze() {		
		getNextToken();
		program();
	}
	
	private void getNextToken() {
		if (index < tokenList.size()) {			
			nextToken = tokenList.get(index);
			index++;
		}		
	}
	
	private boolean accept(String type) {
		if (type.toLowerCase().equals(nextToken.type.toLowerCase())) {
			getNextToken();
			return true;
		}
		return false;
	}
	
	private boolean expect(String type) {
		if (accept(type)) {
			return true;
		}
		errors.add("Expect: Unexpected symbol.");
		return false;
	}
	
	private void identifier() {
		if (accept("identifier")) {
			;
		}	else {
			errors.add("Identifier: Syntax error.");
			getNextToken();
		}	
	}
	
	private void dataType() {
		if (accept("number")) {
			;
		} else if (accept("word")) {
			;
		} else {
			errors.add("Data type: Syntax error.");
			getNextToken();
		}
	}
	
	private void identifierList() {
		identifier();
		while (nextToken.type.toLowerCase().equals("comma")) {
			getNextToken();
			identifierList();
		}
	}
	
	private void variableDeclaration() {
		identifierList();
		if (accept("use as")) {
			dataType();
			expect("semicolon");
		} else {
			errors.add("Variable Declaration: Syntax error.");
		}
	}
	
	private void varsBlock() {
		expect("begin");
		expect("vars");
		while (nextToken.type.toLowerCase().equals("identifier")) {
			variableDeclaration();
		}
		expect("end");
		expect("vars");
	}
	
	private void factor() {
		if (accept("identifier")) {
			;
		}	else if (accept("number")) {
			;
		}	else if (accept("left parenthesis")) {
			numberExpression();
			expect("right parenthesis");
		} else {
			errors.add("Factor: Syntax error.");
		}
	}
	
	private void term() {
		factor();
		while (nextToken.type.toLowerCase().equals("multiplication") || nextToken.type.toLowerCase().equals("division")) {
			getNextToken();
			factor();
		}
	}
	
	private void numberExpression() {
		if (nextToken.type.toLowerCase().equals("addition") || nextToken.type.toLowerCase().equals("subtraction")) {
			getNextToken();
		}
		term();
		while (nextToken.type.toLowerCase().equals("addition") || nextToken.type.toLowerCase().equals("subtraction")) {
			getNextToken();
			term();
		}
	}
	
	private void wordFactor() {
		if (accept("identifier")) {
			;
		}	else if (accept("word")) {
			;
		} else {
			errors.add("Word Concatenation: Syntax error.");
		}
	}
	
	private void wordExpression() {
		if (nextToken.type.toLowerCase().equals("addition")) {
			getNextToken();
		}
		wordFactor();
		while (nextToken.type.toLowerCase().equals("addition")) {
			getNextToken();
		wordFactor();
		}
	}
	
	private void expression() {
		if (accept("word")) {
			if (!nextToken.type.toLowerCase().equals("semicolon")) {		
				wordExpression();
			}
		}	else if (accept("identifier") || accept("number") || accept("left parenthesis")) {
			if (!nextToken.type.toLowerCase().equals("semicolon")) {		
				numberExpression();
			}
		}
	}
	
	private void assignmentStatement() {
		identifier();
		expect("basic assignment");
		expression();
	}
	
	private void printStatement() {
		expect("print");
		identifier();
	}
	
	private void readStatement() {
		expect("read");
		identifier();
	}
	
	private void programStatement() {
		if (nextToken.type.toLowerCase().equals("read")) {
			readStatement();
		}	else if (nextToken.type.toLowerCase().equals("print")) {
			printStatement();
		} else if (nextToken.type.toLowerCase().equals("identifier")) {
			assignmentStatement();
		}
		expect("semicolon");
	}
	
	private void statementsBlock() {
		expect("begin");
		expect("statements");
		while (nextToken.type.toLowerCase().equals("identifier") || nextToken.type.toLowerCase().equals("read") || nextToken.type.toLowerCase().equals("print")) {
			programStatement();
		}
		expect("end");
		expect("statements");
	}
	
	private void program() {
		varsBlock();
		statementsBlock();
		expect("EOF");
	}
	
	public void printErrors() {
		if (errors.isEmpty()) {
			System.out.println("Syntax Analysis complete. No errors found.");
		}	else {			
			for (String error : errors) {
				System.out.println(error);
			}
		}	
	}
}
