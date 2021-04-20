package org.unclesniper.confhoard.ogdl;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Collections;
import java.io.InputStreamReader;
import org.unclesniper.ogdl.Lexer;
import org.unclesniper.ogdl.Parser;
import java.util.function.Function;
import org.unclesniper.ogdl.TokenSink;
import org.unclesniper.confhoard.core.Slot;
import org.unclesniper.ogdl.TokenSinkWrapper;
import org.unclesniper.ogdl.StringClassMapper;
import org.unclesniper.ogdl.NullObjectBuilder;
import org.unclesniper.confhoard.core.Fragment;
import org.unclesniper.ogdl.TokenSinkWrapperChain;
import org.unclesniper.confhoard.core.SlotListener;
import org.unclesniper.ogdl.ObjectDescriptionException;
import org.unclesniper.confhoard.core.listener.SelectingSlotListener;

public class OGDLCheckingSlotListener extends SelectingSlotListener {

	private boolean registerBuiltinStringClassMappers;

	private Set<StringClassMapper> stringClassMappers = new HashSet<StringClassMapper>();

	private final List<TokenSinkWrapper> sinkWrappers = new LinkedList<TokenSinkWrapper>();

	private boolean checkConstants = true;

	private String charset;

	public OGDLCheckingSlotListener() {}

	public boolean isRegisterBuiltinStringClassMappers() {
		return registerBuiltinStringClassMappers;
	}

	public void setRegisterBuiltinStringClassMappers(boolean registerBuiltinStringClassMappers) {
		this.registerBuiltinStringClassMappers = registerBuiltinStringClassMappers;
	}

	public Set<StringClassMapper> getStringClassMappers() {
		return Collections.unmodifiableSet(stringClassMappers);
	}

	public void addStringClassMapper(StringClassMapper mapper) {
		if(mapper == null)
			throw new IllegalArgumentException("String class mapper cannot be null");
		stringClassMappers.add(mapper);
	}

	public boolean removeStringClassMapper(StringClassMapper mapper) {
		return stringClassMappers.remove(mapper);
	}

	public List<TokenSinkWrapper> getTokenSinkWrappers() {
		return Collections.unmodifiableList(sinkWrappers);
	}

	public void addTokenSinkWrapper(TokenSinkWrapper wrapper) {
		if(wrapper == null)
			throw new IllegalArgumentException("Token sink wrapper cannot be null");
		sinkWrappers.add(wrapper);
	}

	public boolean removeTokenSinkWrapper(TokenSinkWrapper wrapper) {
		return sinkWrappers.remove(wrapper);
	}

	public boolean isCheckConstants() {
		return checkConstants;
	}

	public void setCheckConstants(boolean checkConstants) {
		this.checkConstants = checkConstants;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	private void checkOGDL(SlotListener.SlotEvent event, Fragment fragment, Function<String, Object> parameters)
			throws IOException, BadOGDLInSlotException {
		if(fragment == null)
			return;
		Slot slot = event.getSlot();
		NullObjectBuilder builder = new NullObjectBuilder();
		builder.setCheckConstants(checkConstants);
		Parser parser = new Parser(builder);
		TokenSinkWrapperChain chain = new TokenSinkWrapperChain(builder, sinkWrappers);
		TokenSink mbparser = chain.rewrapTokenSink(parser, null);
		Lexer lexer = new Lexer(mbparser);
		lexer.setFile(slot.getKey());
		try(InputStream is = fragment.retrieve(event.getCredentials(), event.getConfState(), parameters)) {
			InputStreamReader isr = new InputStreamReader(is, charset == null ? "UTF-8" : charset);
			lexer.pushStream(isr);
		}
		catch(ObjectDescriptionException ode) {
			throw new BadOGDLInSlotException(slot, ode);
		}
	}

	@Override
	protected void selectedSlotLoaded(SlotLoadedEvent event) throws IOException, BadOGDLInSlotException {
		checkOGDL(event, event.getSlot().getFragment(), null);
	}

	@Override
	protected void selectedSlotUpdated(SlotUpdatedEvent event) throws IOException, BadOGDLInSlotException {
		checkOGDL(event, event.getNextFragment(), event::getRequestParameter);
	}

}
