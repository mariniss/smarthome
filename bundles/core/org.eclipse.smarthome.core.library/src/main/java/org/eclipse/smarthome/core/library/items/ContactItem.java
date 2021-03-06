/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.library.items;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.core.library.CoreItemFactory;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

/**
 * A ContactItem can be used for sensors that return an "open" or "close" as a state.
 * This is useful for doors, windows, etc.
 * 
 * @author Kai Kreuzer - Initial contribution and API
 *
 */
public class ContactItem extends GenericItem {
	
	private static List<Class<? extends State>> acceptedDataTypes = new ArrayList<Class<? extends State>>();
	private static List<Class<? extends Command>> acceptedCommandTypes = new ArrayList<Class<? extends Command>>();
	
	static {
		acceptedDataTypes.add(OpenClosedType.class);
		acceptedDataTypes.add(UnDefType.class);
	}
	
	public ContactItem(String name) {
		super(CoreItemFactory.CONTACT, name);
	}

	public void send(OpenClosedType command) {
		internalSend(command);
	}

	public List<Class<? extends State>> getAcceptedDataTypes() {
		return acceptedDataTypes;
	}

	public List<Class<? extends Command>> getAcceptedCommandTypes() {
		return acceptedCommandTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public State getStateAs(Class<? extends State> typeClass) {
		if(typeClass==DecimalType.class) {
			return state==OpenClosedType.OPEN ? new DecimalType(1) : DecimalType.ZERO;
		} else if(typeClass==PercentType.class) {
			return state==OpenClosedType.OPEN ? PercentType.HUNDRED : PercentType.ZERO;
		} else {
			return super.getStateAs(typeClass);
		}
	}
}
