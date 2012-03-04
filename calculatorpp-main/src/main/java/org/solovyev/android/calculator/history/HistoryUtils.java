/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.List;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:59 PM
 */
class HistoryUtils {

	// not intended for instantiation
	private HistoryUtils() {
		throw new AssertionError();
	}

	public static void fromXml(@Nullable String xml, @NotNull List<CalculatorHistoryState> historyItems) {
		if (xml != null) {
			final Serializer serializer = new Persister();
			try {
				final History history = serializer.read(History.class, xml);
				for (CalculatorHistoryState historyItem : history.getHistoryItems()) {
					historyItems.add(historyItem);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@NotNull
	public static String toXml(@NotNull List<CalculatorHistoryState> historyItems) {
		final History history = new History();
		for (CalculatorHistoryState historyState : historyItems) {
			if (historyState.isSaved()) {
				history.getHistoryItems().add(historyState);
			}
		}

		final StringWriter xml = new StringWriter();
		final Serializer serializer = new Persister();
		try {
			serializer.write(history, xml);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return xml.toString();
	}
}
