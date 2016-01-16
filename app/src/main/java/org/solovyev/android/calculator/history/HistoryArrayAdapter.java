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
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class HistoryArrayAdapter extends ArrayAdapter<HistoryState> {

    private static final int DATETIME_FORMAT = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_TIME;

    HistoryArrayAdapter(Context context, int resource, int textViewResourceId, @Nonnull List<HistoryState> historyList) {
        super(context, resource, textViewResourceId, historyList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

        final HistoryState state = getItem(position);

        final TextView time = (TextView) result.findViewById(R.id.history_item_time);
        time.setText(DateUtils.formatDateTime(getContext(), state.getTime(), DATETIME_FORMAT));

        final TextView editor = (TextView) result.findViewById(R.id.history_item_value);
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
        return result;
    }
}
