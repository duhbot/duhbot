package org.duh102.duhbot.exception;

public class AbsentRequiredAttributeException extends Exception {
	public AbsentRequiredAttributeException(String message) {
		super(message);
	}

	public AbsentRequiredAttributeException(String message, Throwable throwable) {
		super(message, throwable);
	}
}