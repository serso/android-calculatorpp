package org.solovyev.android.calculator;

import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:10 PM
 */
public final class CalculatorMessages {


	private CalculatorMessages() {
		throw new AssertionError();
	}

	@Nonnull
	public static ResourceBundle getBundle() {
		return getBundle(Locale.getDefault());
	}

	@Nonnull
	public static ResourceBundle getBundle(@Nonnull Locale locale) {
		try {
			return ResourceBundle.getBundle("org/solovyev/android/calculator/messages", locale);
		} catch (MissingResourceException e) {
			return ResourceBundle.getBundle("org/solovyev/android/calculator/messages", Locale.ENGLISH);
		}
	}

	/* Arithmetic error occurred: {0} */
	public static final String msg_001 = "msg_1";

	/* Too complex expression*/
	public static final String msg_002 = "msg_2";

	/* Too long execution time - check the expression*/
	public static final String msg_003 = "msg_3";

	/* Evaluation was cancelled*/
	public static final String msg_004 = "msg_4";

	/* No parameters are specified for function: {0}*/
	public static final String msg_005 = "msg_5";

	/* Infinite loop is detected in expression*/
	public static final String msg_006 = "msg_6";

	/**
	 * Some data could not be loaded. Contact authors of application with information below.\n\nUnable to load:\n{0}
	 */
	public static final String msg_007 = "msg_7";

	/* Error */
	public static final String syntax_error = "syntax_error";

	/* Result copied to clipboard! */
	public static final String result_copied = "result_copied";

	/* Text copied to clipboard! */
	public static final String text_copied = "text_copied";

	/*	Last calculated value */
	public static final String ans_description = "ans_description";

	@Nonnull
	public static CalculatorMessage newErrorMessage(@Nonnull String messageCode, @Nullable Object... parameters) {
		return new CalculatorMessage(messageCode, MessageType.error, parameters);
	}
}
