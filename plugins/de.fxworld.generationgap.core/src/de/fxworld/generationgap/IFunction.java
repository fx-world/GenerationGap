package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap.core
 * %%
 * Copyright (C) 2016 - 2018 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 * 
 * Contributors:
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */


public interface IFunction<T1, T2> {

	public T1 apply(T2 input);

}
