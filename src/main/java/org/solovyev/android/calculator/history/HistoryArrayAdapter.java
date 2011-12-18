/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.prefs.ResourceCache;
import org.solovyev.common.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
* User: serso
* Date: 12/18/11
* Time: 7:39 PM
*/
public class HistoryArrayAdapter extends ArrayAdapter<CalculatorHistoryState> {

	HistoryArrayAdapter(Context context, int resource, int textViewResourceId, @NotNull List<CalculatorHistoryState> historyList) {
		super(context, resource, textViewResourceId, historyList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

		final CalculatorHistoryState state = getItem(position);

		final TextView time = (TextView) result.findViewById(R.id.history_time);
		time.setText(new SimpleDateFormat().format(new Date(state.getTime())));

		final TextView editor = (TextView) result.findViewById(R.id.history_item);
		editor.setText(AbstractHistoryActivity.getHistoryText(state));

		final TextView commentView = (TextView) result.findViewById(R.id.history_item_comment);
		if (commentView != null) {
			final String comment = state.getComment();
			if (!StringUtils.isEmpty(comment)) {
				commentView.setText(comment);
			} else {
				commentView.setText("");
			}
		}

		final TextView status = (TextView) result.findViewById(R.id.history_item_status);
		if (status != null) {
			if (state.isSaved()) {
				status.setText(ResourceCache.instance.getCaption("c_history_item_saved"));
			} else {
				if ( AbstractHistoryActivity.isAlreadySaved(state) ) {
					status.setText(ResourceCache.instance.getCaption("c_history_item_already_saved"));
				} else {
					status.setText(ResourceCache.instance.getCaption("c_history_item_not_saved"));
				}
			}
		}

		return result;
	}

	@Override
	public void notifyDataSetChanged() {
		this.setNotifyOnChange(false);
		this.sort(AbstractHistoryActivity.COMPARATOR);
		this.setNotifyOnChange(true);
		super.notifyDataSetChanged();
	}
}
