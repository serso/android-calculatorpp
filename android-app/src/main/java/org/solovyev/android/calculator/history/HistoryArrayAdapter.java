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
import android.widget.ImageView;
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.solovyev.android.calculator.CalculatorFragmentType.saved_history;
import static org.solovyev.android.calculator.history.AbstractCalculatorHistoryFragment.isAlreadySaved;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:39 PM
 */
public class HistoryArrayAdapter extends ArrayAdapter<CalculatorHistoryState> {

	private boolean showDatetime;

	HistoryArrayAdapter(Context context, int resource, int textViewResourceId, @Nonnull List<CalculatorHistoryState> historyList, boolean showDatetime) {
		super(context, resource, textViewResourceId, historyList);
		this.showDatetime = showDatetime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

		final CalculatorHistoryState state = getItem(position);

		final TextView time = (TextView) result.findViewById(R.id.history_time);
		if (showDatetime) {
			time.setVisibility(VISIBLE);
			time.setText(new SimpleDateFormat().format(new Date(state.getTime())));
		} else {
			time.setVisibility(GONE);
			time.setText(null);
		}

		final TextView editor = (TextView) result.findViewById(R.id.history_item);
		editor.setText(AbstractCalculatorHistoryFragment.getHistoryText(state));

		final View commentLayout = result.findViewById(R.id.history_item_comment_layout);
		final TextView commentView = (TextView) result.findViewById(R.id.history_item_comment);
		if (commentLayout != null && commentView != null) {
			final String comment = state.getComment();
			if (!Strings.isEmpty(comment)) {
				commentView.setText(comment);
				commentLayout.setVisibility(VISIBLE);
			} else {
				commentView.setText(null);
				commentLayout.setVisibility(GONE);
			}
		}

		final ImageView status = (ImageView) result.findViewById(R.id.history_item_status_icon);
		if (status != null) {
			if (state.isSaved() || isAlreadySaved(state)) {
				status.setVisibility(VISIBLE);
				status.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final Context context = getContext();
						if(context instanceof CalculatorHistoryActivity) {
							final CalculatorHistoryActivity activity = (CalculatorHistoryActivity) context;
							activity.getActivityHelper().selectTab(activity, saved_history);
						}
					}
				});
			} else {
				status.setVisibility(INVISIBLE);
				status.setOnClickListener(null);
			}
		}

		return result;
	}

	@Override
	public void notifyDataSetChanged() {
		this.setNotifyOnChange(false);
		this.sort(AbstractCalculatorHistoryFragment.COMPARATOR);
		this.setNotifyOnChange(true);
		super.notifyDataSetChanged();
	}

	public void setShowDatetime(boolean showDatetime) {
		if (this.showDatetime != showDatetime) {
			this.showDatetime = showDatetime;
			notifyDataSetChanged();
		}
	}
}
