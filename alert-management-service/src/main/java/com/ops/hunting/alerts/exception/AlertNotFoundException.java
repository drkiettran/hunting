package com.ops.hunting.alerts.exception;

public class AlertNotFoundException extends RuntimeException {
	public AlertNotFoundException(String message) {
		super(message);
	}
}