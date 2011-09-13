package org.solovyev.util.date;

/**
 * User: serso
 * Date: 9/13/11
 * Time: 6:46 PM
 */
public class MutableObject<T> {

	private T object;

	public MutableObject() {
	}

	public MutableObject(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}
