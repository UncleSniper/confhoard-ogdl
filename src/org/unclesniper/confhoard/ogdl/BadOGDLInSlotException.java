package org.unclesniper.confhoard.ogdl;

import org.unclesniper.confhoard.core.Slot;
import org.unclesniper.ogdl.ObjectDescriptionException;
import org.unclesniper.confhoard.core.ConfHoardException;

public class BadOGDLInSlotException extends ConfHoardException {

	private final Slot slot;

	public BadOGDLInSlotException(Slot slot, ObjectDescriptionException cause) {
		super("Slot '" + (slot == null ? "" : slot.getKey()) + "' would not contain valid OGDL"
				+ (cause == null || cause.getMessage() == null || cause.getMessage().length() == 0
				? "" : ": " + cause.getMessage()), cause);
		if(slot == null)
			throw new IllegalArgumentException("Slot cannot be null");
		this.slot = slot;
	}

	public Slot getSlot() {
		return slot;
	}

}
