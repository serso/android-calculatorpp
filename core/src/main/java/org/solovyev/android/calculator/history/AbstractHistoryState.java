/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Transient;

import java.util.Date;

/**
 * User: serso
 * Date: 10/15/11
 * Time: 1:45 PM
 */
public class AbstractHistoryState implements Cloneable {

	@Element
	private long time = new Date().getTime();

	@Element(required = false)
	@Nullable
	private String comment;

	@Transient
	private boolean saved;

	@Transient
	private int id = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Nullable
	public String getComment() {
		return comment;
	}

	public void setComment(@Nullable String comment) {
		this.comment = comment;
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	@Override
	protected AbstractHistoryState clone() {
		AbstractHistoryState clone;

		try {
			clone = (AbstractHistoryState) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException(e);
		}

		return clone;
	}
}
