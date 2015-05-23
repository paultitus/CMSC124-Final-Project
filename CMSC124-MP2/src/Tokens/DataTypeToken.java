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
public class DataTypeToken extends VariadicToken {
	public DataTypeToken(String content) {
		super("data type", content);
		supertype = "Data Type";
	}
	
}
