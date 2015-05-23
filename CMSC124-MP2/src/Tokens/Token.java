package Tokens;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Objects;

/**
 *
 * @author obligado
 */
public class Token implements Comparable<Token> {
	public String content;
	public String type;
	public String supertype;
	
	
	public Token(String supertype, String type, String content) {
		this.supertype = supertype;
		this.type = type;
		this.content = content;
	}
	
	public Token(String type, String content) {
		this.type = type;
		this.content = content;
	}
	
	public Token() {
		
	}
	
	@Override
	public String toString() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		Token tok = (Token) obj;
		return this.type.equals(tok.type) && this.content.equals(tok.content);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.content);
		hash = 59 * hash + Objects.hashCode(this.type);
		return hash;
	}

	@Override
	public int compareTo(Token o) {
		return supertype.compareToIgnoreCase(o.supertype);
	}
}