
import Tokens.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author obligado
 */
public class Tokenizer {
	String num = "0123456789";
	String az = "abcdefghijklmnopqrstuvwxyz";
	String az_ = "abcdefghijklmnopqrstuvwxyz_";
	String numAz_ = num + az + "_";
	
	String[] assignments = {"="};
	String[] operators = {"+", "-", "*", "/", "%"};
  String[] reserved = {"begin", "end", "vars", "statements", "print", "read", "number", "word"}; //"use as" keyword is set manually
  String[] types = {"number", "word"};
	
	Reader inputStream;
	Token currentToken;
	Character lastChar;
	ArrayList<Token> tokenTypes;
	ArrayList<Token> tokenList;

	Tokenizer(File inputFile) {
		try {
			inputStream = new InputStreamReader(new FileInputStream(inputFile));
		} catch (FileNotFoundException e) {
			System.out.println("File " + inputFile.getName() + " not found.");
		}
		tokenTypes = new ArrayList();
		tokenList = new ArrayList();
		lastChar = ' ';
	}
	
	public void getChar() {
		int r;		
		try {
			r = inputStream.read();
			if (r != -1) {
				lastChar = (char) r;
			} else {
				lastChar =  '\u001a';
			}			
		} catch (Exception e) {
			lastChar =  '\u001a';
		}
	}
	
	public void nextToken() {
		while (Character.isWhitespace(lastChar)) {
			getChar();
		}
		if (lastChar == '\u001a') {
			currentToken = new EOFToken();
		}	else {
			//operators
			if (Character.toLowerCase(lastChar) == '+') {
				//addition
				currentToken = new ArithmeticOperatorToken("addition", "+");
				getChar();
			} else if (Character.toLowerCase(lastChar) == '-') {
				//subtraction
				currentToken = new ArithmeticOperatorToken("subtraction", "-");
				getChar();
			} else if (Character.toLowerCase(lastChar) == '*') {
				//multiplication
				currentToken = new ArithmeticOperatorToken("multiplication", "*");
				getChar();				
			} else if (Character.toLowerCase(lastChar) == '/') {
				//division
				getChar();				
				if (Character.toLowerCase(lastChar) == '/') {
					//inline comment
					currentToken = getInlineComment();
				} else {
					currentToken = new ArithmeticOperatorToken("division", "/");
				}
			} else if (Character.toLowerCase(lastChar) == '%') {
				//modulo
				currentToken = new ArithmeticOperatorToken("modulo", "%");
				getChar();
			} else if (Character.toLowerCase(lastChar) == '=') {
				//equal
				currentToken = new AssignmentToken("basic assignment", "=");
				getChar();
			} else if (num.indexOf(Character.toLowerCase(lastChar)) != -1) {
				//integer
				currentToken = getNumber();
			} else if (az_.indexOf(Character.toLowerCase(lastChar)) != -1) {
				//identifier or keyword
				currentToken = getIdentifierOrKeyword();
			} else if (Character.toLowerCase(lastChar) == '"') {
				//string
				currentToken = getString();
			} else if (Character.toLowerCase(lastChar) == '(') {
				//add (
				currentToken = new PunctuationToken("(");
				getChar();
			} else if (Character.toLowerCase(lastChar) == ')') {
				getParameterList();
				currentToken = new PunctuationToken(")");
				getChar();
			} else if (Character.toLowerCase(lastChar) == ',') {
				currentToken = new PunctuationToken(",");
				getChar();
			} else if (Character.toLowerCase(lastChar) == ';') {
				currentToken = new PunctuationToken(";");
				getChar();
			} else {
				currentToken = new UnknownToken(lastChar + "");
				getChar();
			}
		} //first if-else
		//add to token list
		addTokenType(currentToken);
		tokenList.add(currentToken);
	}
	
	private IntegerToken getNumber() {
		String number = "" + Character.toLowerCase(lastChar);
		getChar();
		while (num.indexOf(Character.toLowerCase(lastChar)) != -1) {
			number += Character.toLowerCase(lastChar);
			getChar();
		}
		return new IntegerToken(number);
	}
	
	private VariadicToken getIdentifierOrKeyword() {
		String identifier = "" + lastChar;
		getChar();
		while (numAz_.indexOf(lastChar) != -1) {
			identifier += lastChar;
			getChar();
		}
		
		for (String word : reserved) {
			if (identifier.equals(word)) {
				//check if data type
				for (String type : types) {
					if (identifier.equals(type)) {
						return new DataTypeToken(identifier);
					}
				}

				return new KeywordToken(identifier);
			}
		}
		//check if use as
		if (identifier.equals("as")) {
			if (tokenList.get(tokenList.size() - 1).content.equals("use")) {
				tokenList.remove(tokenList.size() - 1);
				return new KeywordToken("use as");
			}
		}
		
		return new IdentifierToken(identifier);
	}
	
	private Token getString() {
		String str = "" + lastChar;
		Character slash = null;
		getChar();
		while (Character.toLowerCase(lastChar) != '"' && lastChar != '\u001a') {
			if (lastChar == '\\') {
				slash = '\\';
			}			
			if (slash != null && slash == '\\' && lastChar != '\\' ) {
				Token escapeToken = new EscapeCharacterToken();
				tokenList.add(escapeToken);
				addTokenType(escapeToken);
				slash = null;
			}
			str += lastChar;
			getChar();
		}
		if (lastChar == '\u001a') {
			return new UnknownToken(str);
		} else {
			//closing "
			str += lastChar;
			getChar();			
		}
		return new StringToken(str);
	}
	
	private Token getInlineComment() {
		String comment = "";
		getChar();
		while (Character.toLowerCase(lastChar) != '\n' && lastChar != '\u001a') {
			comment += lastChar;
			getChar();
		}
		return new CommentToken("inline comment", comment);
	}
	
	private Token getBlockComment() {
		String comment = "";
		getChar();
		while (lastChar != '\u001a') {
			if (Character.toLowerCase(lastChar) == '*') {
				//check if closing
				getChar();
				if (Character.toLowerCase(lastChar) == '/') {
					break;
				} else {
					comment += lastChar;
				}
			}			
			comment += lastChar;
			getChar();
		}
		return new CommentToken("block comment", comment);
	}

	private void addTokenType(Token newToken) {
		for (Token token : tokenTypes) {
			if (token.equals(newToken) || (token.content.equals(newToken.content) && token.supertype.equals("Function"))) {
				return;
			}
		}
		tokenTypes.add(newToken);
	}
	
	private void removeTokenType(Token token) {
		tokenTypes.remove(token);
	}

	private void getParameterList() {
		String parameter = "";
		for (int i = tokenList.size() - 1; i >= 1; i--) {
			if (tokenList.get(i).content.equals("(") && tokenList.get(i).type.toLowerCase().equals("punctuation")) {
				if (tokenList.get(i - 1).type.toLowerCase().equals("identifier")) {
					break;
				} else {
					return;
				}
			}
			parameter = tokenList.get(i).content + parameter;
		}
		addTokenType(new Token("Parameter List", "parameter list", parameter));
	}
	
	public void printTokenTypes() {
		Collections.sort(tokenTypes);
		for (Token token : tokenTypes) {
			System.out.println(token.supertype + " (" + token.type + "): " + token.content);
		}
	}
	
	public void printTokenList() {
		for (Token token : tokenList) {
			System.out.println(token.supertype + " (" + token.type + "): " + token.content);
		}
	}
}
