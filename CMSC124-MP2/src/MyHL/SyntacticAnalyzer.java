/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyHL;

import Tokens.Token;
import GenericTree.src.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author obligado
 */
public class SyntacticAnalyzer {
	int index;
	Token nextToken;
	Token currentToken;
	ArrayList<String> errors;
	ArrayList<Token> tokenList;
	GenericTree<SyntacticUnit> AST;
	
	SyntacticAnalyzer(ArrayList<Token> tokenList) {
		index = 0;
		errors = new ArrayList();
		this.tokenList = new ArrayList();
		this.tokenList = tokenList;
		AST = new GenericTree<>();
	}
	
	public void analyze() {		
		getNextToken();
		GenericTreeNode<SyntacticUnit> node = program();
		AST.setRoot(node);
	}
	
	private void getNextToken() {
		if (index < tokenList.size()) {			
			currentToken = nextToken;
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
	
	private GenericTreeNode<SyntacticUnit> identifier() {
		if (accept("identifier")) {
			return new GenericTreeNode<>(new SyntacticUnit("identifier", currentToken.content));
		}	else {
			errors.add("Identifier: Syntax error.");
			getNextToken();
			return null;
		}	
	}
	
	private GenericTreeNode<SyntacticUnit> dataType() {
		if (accept("number")) {
			return new GenericTreeNode<>(new SyntacticUnit("data type", currentToken.content));
		} else if (accept("word")) {
			return new GenericTreeNode<>(new SyntacticUnit("data type", currentToken.content));
		} else {
			errors.add("Data type: Syntax error.");
			getNextToken();
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> identifierList() {
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("identifier list"));
		GenericTreeNode<SyntacticUnit> ident = identifier();
		
		if (ident != null) {
			node.addChild(ident);
		}
		
		while (nextToken.type.toLowerCase().equals("comma")) {
			getNextToken();
			GenericTreeNode<SyntacticUnit> identList = identifierList();
			if (identList != null) {
				List<GenericTreeNode<SyntacticUnit>> children = identList.getChildren();
				for (GenericTreeNode<SyntacticUnit> child : children) {
					node.addChild(child);
				}
			}
		}
		
		if (node.hasChildren()) {
			return node;
		} else {
			errors.add("Indentifier List: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> variableDeclaration() {
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("variable declaration"));
		GenericTreeNode<SyntacticUnit> identList = identifierList();
		if (identList != null) {
			node.addChild(identList);
		}		
		
		if (accept("use as")) {
			GenericTreeNode<SyntacticUnit> dataType = dataType();
			if (dataType != null) {
				node.addChild(dataType);
			}			
			
			expect("semicolon");
		} else {
			errors.add("Variable Declaration: Syntax error.");
			return null;
		}
		
		if (node.hasChildren()) {
			return node;
		} else {
			errors.add("Variable Declaration: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> varsBlock() {
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("variable declaration block"));
		expect("begin");
		expect("vars");
		while (nextToken.type.toLowerCase().equals("identifier")) {
			GenericTreeNode<SyntacticUnit> varDec = variableDeclaration();
			if (varDec != null) {
				node.addChild(varDec);
			}			
			
		}
		expect("end");
		expect("vars");
		
		if (node.hasChildren()) {
			return node;
		} else {
			errors.add("Variable Declaration Block: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> factor() {
		if (accept("identifier")) {
			return new GenericTreeNode<>(new SyntacticUnit("identifier", currentToken.content));
		}	else if (accept("number")) {
			return new GenericTreeNode<>(new SyntacticUnit("number", currentToken.content));
		}	else if (accept("left parenthesis")) {
			GenericTreeNode<SyntacticUnit> node = numberExpression();
			if (!expect("right parenthesis")) {
				return null;
			} else {
				return node;
			}
		} else {
			errors.add("Factor: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> term() {
		GenericTreeNode<SyntacticUnit> node1 = null;
		GenericTreeNode<SyntacticUnit> node2 = null;
		GenericTreeNode<SyntacticUnit> lh = factor();
		while (nextToken.type.toLowerCase().equals("multiplication") || nextToken.type.toLowerCase().equals("division")) {
			node2 = new GenericTreeNode<>(new SyntacticUnit(nextToken.type.toLowerCase(), nextToken.content));
			if (node1 == null) {
				node1 = node2;
			} else {
				node1.addChild(node2);
			}
			
			if (!node2.hasChildren()) {
				node2.addChild(lh);
			}
			getNextToken();
			lh = factor();
		}
		
		if (node2 != null && lh != null) {
			node2.addChild(lh);
		}		
		
		if (node1 == null) {
			return lh;
		}	else {
			return node1;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> numberExpression() {
		GenericTreeNode<SyntacticUnit> node = null;
		GenericTreeNode<SyntacticUnit> lh = null;
		GenericTreeNode<SyntacticUnit> rh = null;
		if (nextToken.type.toLowerCase().equals("addition") || nextToken.type.toLowerCase().equals("subtraction")) {
			lh = new GenericTreeNode<>(new SyntacticUnit(nextToken.type.toLowerCase(), nextToken.content));
			getNextToken();
		}
		GenericTreeNode<SyntacticUnit> lh1 = term();
		
		if (lh1 != null && lh != null) {
			lh.addChild(lh1);
		}		
		
		while (nextToken.type.toLowerCase().equals("addition") || nextToken.type.toLowerCase().equals("subtraction")) {
			node = new GenericTreeNode<>(new SyntacticUnit(nextToken.type.toLowerCase(), nextToken.content));
			rh = null;
			if (lh != null) {
				node.addChild(lh);
			} else {
				node.addChild(lh1);
			}
			
			getNextToken();
			rh = term();
			if (rh != null) {
				node.addChild(rh);
				lh = node;
			}			
		}
		
		if (node != null) {
			return node;
		} else if (lh != null) {
			return lh;
		} else if (lh1 != null) {
			return lh1;
		} else {
			errors.add("Number Expression: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> wordFactor() {
		if (accept("word")) {
			return new GenericTreeNode<>(new SyntacticUnit("word", currentToken.content));
		} else {
			errors.add("Word: Syntax error.");
			return null;
		}
	}
	
//	private void wordExpression() {
//		if (nextToken.type.toLowerCase().equals("addition")) {
//			getNextToken();
//		}
//		wordFactor();
//		while (nextToken.type.toLowerCase().equals("addition")) {
//			getNextToken();
//		wordFactor();
//		}
//	}
	
	private GenericTreeNode<SyntacticUnit> expression() {
		if (nextToken.type.toLowerCase().equals("word")) {
			return wordFactor();
//			if (!nextToken.type.toLowerCase().equals("semicolon")) {		
//				wordExpression();
//			}
		}	else if (nextToken.type.toLowerCase().equals("subtraction") 
						|| nextToken.type.toLowerCase().equals("addition") 
						|| nextToken.type.toLowerCase().equals("identifier") 
						|| nextToken.type.toLowerCase().equals("number") 
						|| nextToken.type.toLowerCase().equals("left parenthesis")) {
//			if (!nextToken.type.toLowerCase().equals("semicolon")) {		
				return numberExpression();
//			}
		}
		errors.add("Expression: Syntax error.");
		return null;
	}
	
	private GenericTreeNode<SyntacticUnit> assignmentStatement() {		
		GenericTreeNode<SyntacticUnit> lh = identifier();
		GenericTreeNode<SyntacticUnit> rh = null;		
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit(nextToken.type.toLowerCase(), nextToken.content));
		if (expect("basic assignment")) {
			rh = expression();
			if (lh != null && rh != null) {
				node.addChild(lh);
				node.addChild(rh);
				return node;
			}
		}
		errors.add("Assignment Statement: Syntax error.");
		return null;
	}
	
	private GenericTreeNode<SyntacticUnit> printStatement() {
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("print statement"));
		GenericTreeNode<SyntacticUnit> ident;
		expect("print");
		ident = identifier();
		
		if (ident != null) {
			node.addChild(ident);
		}		
		
		if (node.hasChildren()) {
			return node;
		} else {
			errors.add("Print Statement: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> readStatement() {
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("read statement"));
		GenericTreeNode<SyntacticUnit> ident;
		expect("read");
		ident = identifier();
		
		if (ident != null) {
			node.addChild(ident);
		}		
		
		if (node.hasChildren()) {
			return node;
		} else {
			errors.add("Read Statement: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> programStatement() {
		GenericTreeNode<SyntacticUnit> node = null;
		if (nextToken.type.toLowerCase().equals("read")) {
			node = readStatement();
		}	else if (nextToken.type.toLowerCase().equals("print")) {
			node = printStatement();
		} else if (nextToken.type.toLowerCase().equals("identifier")) {
			node = assignmentStatement();
		}
		if (expect("semicolon")) {
			if (node != null) {
				return node;
			}
		}
		errors.add("Program Statement: Syntax error.");
		return null;
	}
	
	private GenericTreeNode<SyntacticUnit> statementsBlock() {
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("statements block"));
		expect("begin");
		expect("statements");
		while (nextToken.type.toLowerCase().equals("identifier") || nextToken.type.toLowerCase().equals("read") || nextToken.type.toLowerCase().equals("print")) {
			GenericTreeNode<SyntacticUnit> statement = programStatement();
			if (statement != null) {
				node.addChild(statement);
			}
		}
		expect("end");
		expect("statements");
		
		if (node.hasChildren()) {
			return node;
		} else {
			errors.add("Statements Block: Syntax error.");
			return null;
		}
	}
	
	private GenericTreeNode<SyntacticUnit> program() {
		GenericTreeNode<SyntacticUnit> lh = varsBlock();
		GenericTreeNode<SyntacticUnit> rh = statementsBlock();		
		GenericTreeNode<SyntacticUnit> node = new GenericTreeNode<>(new SyntacticUnit("program"));
		if (expect("EOF")) {
			if (lh != null && rh != null) {
				node.addChild(lh);
				node.addChild(rh);
				return node;
			}
		}
		errors.add("Program: Syntax error.");
		return null;
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
	
	public List<String> getErrors() {
		return this.errors;
	}
	
	public GenericTree<SyntacticUnit> getAST() {
		return this.AST;
	}

	public class SyntacticUnit {	
		public String content;
		public String type;
		
	
		public SyntacticUnit(String type, String content) {
			this.type = type;
			this.content = content;
		}
		
	
		public SyntacticUnit(String type) {
			this.type = type;
			this.content = null;
		}
		
		public String toString() {
			return content + " (" + type + ")";
		}
	}
}
