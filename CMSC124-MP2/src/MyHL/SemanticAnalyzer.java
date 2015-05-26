/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyHL;

import GenericTree.src.GenericTree;
import GenericTree.src.GenericTreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author obligado
 */
public class SemanticAnalyzer {
	ArrayList<String> errors;
	GenericTree<SyntacticAnalyzer.SyntacticUnit> AST;
	GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> currentNode;
	HashMap<String, ArrayList<String>> symbolTable;
	
	SemanticAnalyzer(GenericTree<SyntacticAnalyzer.SyntacticUnit> AST) {
		this.AST = AST;
		symbolTable = new HashMap();
		errors = new ArrayList();
		currentNode = AST.getRoot();
	}
	
	public void analyze() {
		if (currentNode.getData().type.toLowerCase().equals("program")) {
			processProgram(currentNode);
		}
	}
	
	private void processProgram(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> node) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = node.getChildren();
		if (children.get(0).getData().type.toLowerCase().equals("variable declaration block")) {
			processVarDecBlk(children.get(0));
		}
		
		if (children.get(1).getData().type.toLowerCase().equals("statements block")) {
			processStatementsBlk(children.get(1));
		}		
	}

	private void processVarDecBlk(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> varDecBlk) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = varDecBlk.getChildren();
		for (GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> child : children) {
			if (child.getData().type.toLowerCase().equals("variable declaration")) {
				processVarDec(child);
			}
		}
		
	}

	private void processVarDec(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> varDec) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = varDec.getChildren();
		if (children.get(0).getData().type.toLowerCase().equals("identifier list") && 
				children.get(1).getData().type.toLowerCase().equals("data type")) {
			processIdentList(children.get(0), children.get(1));
		}		
	}

	private void processIdentList(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> list, GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> dataType) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = list.getChildren();
		for (GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> child : children) {
			if (child.getData().type.toLowerCase().equals("identifier")) {
				if (!symbolTable.containsKey(child.getData().content)) {
					ArrayList<String> details = new ArrayList();
					details.add(dataType.getData().content);
					details.add(null);
					symbolTable.put(child.getData().content, details);
				} else {
					errors.add("Semantic Analyzer: Variable " + child.getData().content + " is already declared.");
				}
			}			
		}
	}

	private void processStatementsBlk(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> statementsBlk) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = statementsBlk.getChildren();
		for (GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> child : children) {
			switch (child.getData().type.toLowerCase()) {
				case "read statement":
					processReadStatement(child);
					break;
				case "print statement":
					processPrintStatement(child);
					break;
				case "basic assignment":
					processAssignStatement(child);
					break;
			}
		}
	}

	private void processReadStatement(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> read) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = read.getChildren();
		if (children.isEmpty() || !(children.get(0).getData().type.toLowerCase().equals("identifier"))) {
			errors.add("Semantic Analyzer: Read statement requires an identifier");
		} else if (children.size() > 1) {
			errors.add("Semantic Analyzer: More than 1 parameter in read statement.");
		}
	}

	private void processPrintStatement(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> print) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = print.getChildren();
		if (children.isEmpty() || !(children.get(0).getData().type.toLowerCase().equals("identifier"))) {
			errors.add("Semantic Analyzer: Print statement requires an identifier");
		} else if (children.size() > 1) {
			errors.add("Semantic Analyzer: More than 1 parameter in print statement.");
		}
	}

	private void processAssignStatement(GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> assign) {
		List<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> children = assign.getChildren();
		if (!symbolTable.containsKey(children.get(0).getData().content)) {
			errors.add("Semantic Analyzer: Variable " + children.get(0).getData().content + " has not been declared.");
		}
		
		if (children.get(1).getData().type.toLowerCase().equals("word")) {
			if (!symbolTable.get(children.get(0).getData().content).get(0).toLowerCase().equals("word")) {
				errors.add("Semantic Analyzer: Data type mismatch - trying to assign word value to a " + symbolTable.get(children.get(0).getData().content).get(0) + " variable.");
			}
		} else if (children.get(1).getData().type.toLowerCase().equals("identifier")) {
			if (!symbolTable.containsKey(children.get(1).getData().content)) {
				errors.add("Semantic Analyzer: Variable " + children.get(1).getData().content + " has not been declared.");
			}	else if (!symbolTable.get(children.get(0).getData().content).get(0).toLowerCase().equals(children.get(1).getData().type.toLowerCase())) {
				errors.add("Semantic Analyzer: Data type mismatch - trying to assign " + symbolTable.get(children.get(1).getData().content).get(0) + " value to a " + symbolTable.get(children.get(0).getData().content).get(0) + " variable.");
			}
		} else {
			Queue<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> nodesToVisit = new LinkedList();
			ArrayList<GenericTreeNode<SyntacticAnalyzer.SyntacticUnit>> allChildren = new ArrayList();
			nodesToVisit.add(children.get(1));
			
			while (nodesToVisit.size() > 0) {
				GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> current = nodesToVisit.poll();
				for (GenericTreeNode<SyntacticAnalyzer.SyntacticUnit> child : current.getChildren()) {
					nodesToVisit.add(child);
				}
				if (current.getData().type.toLowerCase().equals("identifier")) {
					if (!symbolTable.get(children.get(0).getData().content).get(0).toLowerCase().equals(symbolTable.get(current.getData().content).get(0).toLowerCase())) {
						errors.add("Semantic Analyzer: Data type mismatch - trying to assign " + symbolTable.get(current.getData().content).get(0) + " value to a " + symbolTable.get(children.get(0).getData().content).get(0) + " variable.");
					}					
				}				
			}
		}
	}	
	
	public void printErrors() {
		if (errors.isEmpty()) {
			System.out.println("Semantic Analysis complete. No errors found.");
		}	else {			
			for (String error : errors) {
				System.out.println(error);
			}
		}	
	}
	
	public List<String> getErrors() {
		return this.errors;
	}
	
	public HashMap<String, ArrayList<String>> getSymbolTable() {
		return this.symbolTable;
	}
}
