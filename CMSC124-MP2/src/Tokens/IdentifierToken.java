package Tokens;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author obligado
 */
public class IdentifierToken extends VariadicToken {
	public IdentifierToken(String content) {
		super("identifier", content);
		supertype = "Identifier";
	}
	public IdentifierToken(String supertype, String type, String content) {
		super(type, content);
		this.supertype = supertype;
	}
}
