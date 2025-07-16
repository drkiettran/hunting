package com.ops.hunting.common.exception;

import lombok.Getter;

/**
 * Exception for entity not found scenarios
 */
@Getter
public class EntityNotFoundException extends BusinessException {

	private final String entityType;
	private final Object entityId;

	public EntityNotFoundException(String entityType, Object entityId) {
		super(String.format("%s with id '%s' not found", entityType, entityId), "ENTITY_NOT_FOUND");
		this.entityType = entityType;
		this.entityId = entityId;
	}

	public EntityNotFoundException(String message, String entityType, Object entityId) {
		super(message, "ENTITY_NOT_FOUND");
		this.entityType = entityType;
		this.entityId = entityId;
	}
}