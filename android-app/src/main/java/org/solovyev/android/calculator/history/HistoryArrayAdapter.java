/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.solovyev.android.calculator.R;
import org.solovyev.common.text.Strings;

import java.util.List;

import javax.annotation.Nonnull;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.solovyev.android.calculator.CalculatorFragmentType.saved_history;
import static org.solovyev.android.calculator.history.BaseHistoryFragment.isAlreadySaved;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:39 PM
 */
public class HistoryArrayAdapter extends ArrayAdapter<CalculatorHistoryState> {

    private static final int DATETIME_FORMAT = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_TIME;
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
            time.setText(DateUtils.formatDateTime(getContext(), state.getTime(), DATETIME_FORMAT));
        } else {
            time.setVisibility(GONE);
            time.setText(null);
        }

        final TextView editor = (TextView) result.findViewById(R.id.history_item);
        editor.setText(BaseHistoryFragment.getHistoryText(state));

        final TextView commentView = (TextView) result.findViewById(R.id.history_item_comment);
        if (commentView != null) {
            final String comment = state.getComment();
            if (!Strings.isEmpty(comment)) {
                commentView.setText(comment);
                commentView.setVisibility(VISIBLE);
            } else {
                commentView.setText(null);
                commentView.setVisibility(GONE);
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
                        if (context instanceof CalculatorHistoryActivity) {
                            final CalculatorHistoryActivity activity = (CalculatorHistoryActivity) context;
                            activity.getUi().selectTab(activity, saved_history);
                        }
                    }
                });
            } else {
                status.setVisibility(GONE);
                status.setOnClickListener(null);
            }
        }

        return result;
    }

    @Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false);
        this.sort(BaseHistoryFragment.COMPARATOR);
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
