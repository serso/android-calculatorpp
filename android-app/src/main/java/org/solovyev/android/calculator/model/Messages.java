package org.solovyev.android.calculator.model;

/**
 * User: serso
 * Date: 11/25/11
 * Time: 1:40 PM
 */
public final class Messages {


	// not intended for instantiation
	private Messages() {
		throw new AssertionError();
	}

	/** Arithmetic error occurred: {0} */
	public static final String msg_1 = "msg_1";

	/** Too complex expression */
	public static final String msg_2 = "msg_2";

	/** Too long execution time - check the expression */
	public static final String msg_3 = "msg_3";

	/** Evaluation was cancelled */
	public static final String msg_4 = "msg_4";

	/** No parameters are specified for function: {0} */
	public static final String msg_5 = "msg_5";

	/** Infinite loop is detected in expression */
	public static final String msg_6 = "msg_6";

}
