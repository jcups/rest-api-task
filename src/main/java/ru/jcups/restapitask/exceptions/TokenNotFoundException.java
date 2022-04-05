package ru.jcups.restapitask.exceptions;

public class TokenNotFoundException extends RuntimeException {
	public TokenNotFoundException(String s) {
		super(s);
	}

	public TokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
