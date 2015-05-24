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
public class PunctuationToken extends OperatorToken {
	public PunctuationToken(String content) {
		super("punctuation", content);
	}
	public PunctuationToken(String type, String content) {
		super(type, content);
	}
}