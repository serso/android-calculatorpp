/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Transient;

import java.util.Date;

/**
 * User: serso
 * Date: 10/15/11
 * Time: 1:45 PM
 */
public class AbstractHistoryState {

	@Element
	@NotNull
	private Date time = new Date();

	@Element(required = false)
	@Nullable
	private String comment;

	@Transient
	private boolean saved;

	@NotNull
	public Date getTime() {
		return time;
	}

	public void setTime(@NotNull Date time) {
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
}
