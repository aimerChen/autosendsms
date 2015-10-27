package com.chen.autosendsms.utils;

public class InvalidParamsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidParamsException() {
		
	}
	
	public InvalidParamsException(String detailMessage) {
		 super(detailMessage);
	}
}
