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

package org.solovyev.android.calculator.function;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FunctionParamsView extends LinearLayout {

    @Nonnull
    public static final String PARAM_VIEW_TAG = "param-view";
    private static final int HEADERS = 1;
    private static final int PARAM_VIEW_INDEX = 3;
    private static final int START_ROW_ID = App.generateViewId();
    private int maxRowId = START_ROW_ID;
    private final int clickableAreaSize = getResources().getDimensionPixelSize(R.dimen.cpp_clickable_area_size);

    public FunctionParamsView(Context context) {
        super(context);
        init();
    }

    public FunctionParamsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FunctionParamsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        final Context context = getContext();

        final LinearLayout headerView = makeRowView(context);
        final Button addButton = new Button(context);
        addButton.setText("+");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParam(null);
            }
        });
        headerView.addView(addButton, new LayoutParams(clickableAreaSize, WRAP_CONTENT));
        headerView.addView(new View(context), new LayoutParams(0, WRAP_CONTENT, 1));
        addView(headerView, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    @Nonnull
    private LinearLayout makeRowView(@Nonnull Context context) {
        final LinearLayout rowView = new LinearLayout(context);
        rowView.setOrientation(HORIZONTAL);
        rowView.setMinimumHeight(clickableAreaSize);
        rowView.setGravity(Gravity.CENTER_VERTICAL);
        return rowView;
    }

    public void addParams(@Nonnull List<String> params) {
        for (String param : params) {
            addParam(param);
        }
    }

    public void addParam(@Nullable String param) {
        addParam(param, maxRowId++);
    }

    private void addParam(@Nullable String param, final int id) {
        final Context context = getContext();
        final LinearLayout rowView = makeRowView(context);

        final Button removeButton = new Button(context);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRow(rowView);
            }
        });
        removeButton.setText("−");
        rowView.addView(removeButton, new LayoutParams(clickableAreaSize, WRAP_CONTENT));

        final Button upButton = new Button(context);
        upButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                upRow(rowView);
            }
        });
        upButton.setText("↑");
        rowView.addView(upButton, new LayoutParams(clickableAreaSize, WRAP_CONTENT));

        final Button downButton = new Button(context);
        downButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                downRow(rowView);
            }
        });
        downButton.setText("↓");
        rowView.addView(downButton, new LayoutParams(clickableAreaSize, WRAP_CONTENT));

        final TextInputLayout paramLabel = new TextInputLayout(context);
        final EditText paramView = new EditText(context);
        if (param != null) {
            paramView.setText(param);
        }
        paramView.setOnFocusChangeListener(getOnFocusChangeListener());
        paramView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        paramView.setId(id);
        paramView.setTag(PARAM_VIEW_TAG);
        paramView.setHint(R.string.c_function_parameter);
        paramLabel.addView(paramView, new TextInputLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        rowView.addView(paramLabel, new LayoutParams(0, WRAP_CONTENT, 1));

        addView(rowView, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    private void downRow(@Nonnull ViewGroup row) {
        final int index = indexOfChild(row);
        if (index < getChildCount() - 1) {
            swap(row, getRow(index + 1));
        }
    }

    private void upRow(@Nonnull ViewGroup row) {
        final int index = indexOfChild(row);
        if (index > 1) {
            swap(row, getRow(index - 1));
        }
    }

    private void swap(@Nonnull ViewGroup l, @Nonnull ViewGroup r) {
        final EditText lParam = getParamView(l);
        final EditText rParam = getParamView(r);
        swap(lParam, rParam);
    }

    private void swap(@Nonnull TextView l,
                      @Nonnull TextView r) {
        final CharSequence tmp = l.getText();
        l.setText(r.getText());
        r.setText(tmp);
    }

    @Nonnull
    private ViewGroup getRow(int index) {
        Check.isTrue(index >= 0 && index < getChildCount());
        return (ViewGroup) getChildAt(index);
    }

    public void removeRow(@Nonnull ViewGroup row) {
        removeView(row);
    }

    @Nonnull
    public List<String> getParams() {
        final List<String> params = new ArrayList<>(getChildCount());

        for (int i = HEADERS; i < getChildCount(); i++) {
            final ViewGroup row = getRow(i);
            final EditText paramView = getParamView(row);
            params.add(paramView.getText().toString());
        }

        return params;
    }

    @Nonnull
    private EditText getParamView(@Nonnull ViewGroup row) {
        final TextInputLayout paramLabel = getParamLabel(row);
        return (EditText) paramLabel.getChildAt(0);
    }

    @Nonnull
    private TextInputLayout getParamLabel(@Nonnull ViewGroup row) {
        return (TextInputLayout) row.getChildAt(PARAM_VIEW_INDEX);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getRowIds());
    }

    @Nonnull
    private int[] getRowIds() {
        final int childCount = getChildCount();
        final int[] rowIds = new int[childCount - 1];
        for (int i = HEADERS; i < childCount; i++) {
            final ViewGroup row = getRow(i);
            final EditText paramView = getParamView(row);
            rowIds[i - 1] = paramView.getId();
        }
        return rowIds;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable in) {
        if (!(in instanceof SavedState)) {
            super.onRestoreInstanceState(in);
            return;
        }

        final SavedState state = (SavedState) in;
        for (int i = 0; i < state.rowIds.length; i++) {
            final int rowId = state.rowIds[i];
            addParam(null, rowId);
            maxRowId = Math.max(maxRowId, rowId + 1);
        }

        super.onRestoreInstanceState(state.getSuperState());
    }

    @Nonnull
    public TextInputLayout getParamLabel(int param) {
        return getParamLabel(getRow(param + HEADERS));
    }

    public static final class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(@Nonnull Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        private int[] rowIds;

        public SavedState(@Nonnull Parcelable superState, int[] rowIds) {
            super(superState);
            this.rowIds = rowIds;
        }

        public SavedState(@Nonnull Parcel in) {
            super(in);
            rowIds = in.createIntArray();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeIntArray(rowIds);
        }
    }
}
