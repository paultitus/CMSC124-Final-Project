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
public class WordToken extends Token {
	public WordToken(String content) {
		super("word", content);
		supertype = "Word";
	}
}
