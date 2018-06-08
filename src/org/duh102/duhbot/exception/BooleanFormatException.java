package org.duh102.duhbot.exception;

public class BooleanFormatException extends Exception {
	public BooleanFormatException(String message) {
		super(message);
	}

	public BooleanFormatException(String message, Throwable throwable) {
		super(message, throwable);
	}
}