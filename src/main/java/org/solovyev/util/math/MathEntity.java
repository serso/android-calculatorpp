/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.util.math;

import org.jetbrains.annotations.NotNull;

public enum MathEntity {

	minus("-"),
	equals("="),
	factorial("!"),
	plus("+"),
	multiply("*"),
	divide("/"),
	power("^"),
	sin("sin"),
	asin("asin"),
	cos("cos"),
	acos("acos"),
	tg("tg"),
	atg("atg"),
	exp("exp"),
	log("log"),
	ln("ln"),
	mod("mod"),
	sqrt("sqrt");
		
	@NotNull
	private final String text;
	
	private MathEntity (@NotNull String text) {
		this.text = text;
	}

	@NotNull
	public String getText() {
		return text;
	}
}
