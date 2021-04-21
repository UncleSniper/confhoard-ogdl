package org.unclesniper.confhoard.ogdl;

import org.unclesniper.confhoard.core.Slot;
import org.unclesniper.confhoard.core.ConfHoardException;

public class BadOGDLRootObjectException extends ConfHoardException {

	private final Slot slot;

	private final Class<?> expectedType;

	private final Class<?> foundType;

	public BadOGDLRootObjectException(Slot slot, Class<?> expectedType, Class<?> foundType) {
		super("OGDL in slot '" + (slot == null ? "" : slot.getKey()) + "' yielded root object of type '"
				+ (foundType == null ? "" : foundType.getName()) + "'; expected type '"
				+ (expectedType == null ? "" : expectedType.getName()) + '\'');
		if(slot == null)
			throw new IllegalArgumentException("Slot cannot be null");
		if(expectedType == null)
			throw new IllegalArgumentException("Expected type cannot be null");
		if(foundType == null)
			throw new IllegalArgumentException("Found type cannot be null");
		this.slot = slot;
		this.expectedType = expectedType;
		this.foundType = foundType;
	}

	public Slot getSlot() {
		return slot;
	}

	public Class<?> getExpectedType() {
		return expectedType;
	}

	public Class<?> getFoundType() {
		return foundType;
	}

}
