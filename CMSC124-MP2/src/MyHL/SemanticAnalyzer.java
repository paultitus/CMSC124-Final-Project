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
import java.util.List;

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
		
	}
}
