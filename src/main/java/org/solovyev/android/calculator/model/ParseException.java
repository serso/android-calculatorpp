/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.solovyev.common.exceptions.SersoException;

/**
* User: serso
* Date: 10/6/11
* Time: 9:25 PM
*/
public class ParseException extends SersoException {

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}
}
