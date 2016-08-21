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

package org.solovyev.android.calculator.functions;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FunctionParamsView extends LinearLayout {

    @Nonnull
    public static final String PARAM_VIEW_TAG = "param-view";
    private static final List<String> PARAM_NAMES = Arrays.asList("x", "y", "z", "t", "a", "b", "c");
    private static final int FOOTERS = 1;
    private static final int PARAM_VIEW_INDEX = 3;
    private static final int START_ROW_ID = App.generateViewId();
    private final int clickableAreaSize;
    private final int imageButtonSize;
    private final int imageButtonPadding;
    @Nonnull
    private final Preferences.Gui.Theme theme = App.getTheme();
    private int maxRowId = START_ROW_ID;
    private int maxParams = Integer.MAX_VALUE;
    @Nonnull
    private LinearLayout headerView;

    {
        final Resources resources = getResources();
        clickableAreaSize = resources.getDimensionPixelSize(R.dimen.cpp_clickable_area_size);
        imageButtonSize = resources.getDimensionPixelSize(R.dimen.cpp_image_button_size);
        imageButtonPadding = resources.getDimensionPixelSize(R.dimen.cpp_image_button_padding);
    }

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

        headerView = makeRowView(context);
        final ImageButton addButton = makeButton(theme.light ? R.drawable.ic_add_black_24dp : R.drawable.ic_add_white_24dp);
        addButton.setId(R.id.function_params_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout rowView = addParam(generateParamName());
                final EditText paramView = getParamView(rowView);
                paramView.requestFocus();
            }
        });
        headerView.addView(addButton, makeButtonParams());
        headerView.addView(new View(context), new LayoutParams(3 * clickableAreaSize, WRAP_CONTENT));
        addView(headerView, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    @Nullable
    private String generateParamName() {
        final List<String> available = new ArrayList<>(PARAM_NAMES);
        available.removeAll(getParams());
        return available.size() > 0 ? available.get(0) : null;
    }

    @NonNull
    private ImageButton makeButton(int icon) {
        final ImageButton addButton = new ImageButton(getContext());
        addButton.setImageResource(icon);
        addButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addButton.setPadding(imageButtonPadding, imageButtonPadding, imageButtonPadding, imageButtonPadding);
        final TypedValue value = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.selectableItemBackgroundBorderless, value, true)) {
            addButton.setBackgroundResource(value.resourceId);
        }
        return addButton;
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

    @NonNull
    public LinearLayout addParam(@Nullable String param) {
        return addParam(param, maxRowId++);
    }

    @NonNull
    private LinearLayout addParam(@Nullable String param, final int id) {
        final Context context = getContext();
        final LinearLayout rowView = makeRowView(context);

        final ImageButton removeButton = makeButton(theme.light ? R.drawable.ic_remove_black_24dp : R.drawable.ic_remove_white_24dp);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRow(rowView);
            }
        });
        rowView.addView(removeButton, makeButtonParams());

        final ImageButton upButton = makeButton(theme.light ? R.drawable.ic_arrow_upward_black_24dp : R.drawable.ic_arrow_upward_white_24dp);
        upButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                upRow(rowView);
            }
        });
        rowView.addView(upButton, makeButtonParams());

        final ImageButton downButton = makeButton(theme.light ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow_downward_white_24dp);
        downButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                downRow(rowView);
            }
        });
        rowView.addView(downButton, makeButtonParams());

        final TextInputLayout paramLabel = new TextInputLayout(context);
        final EditText paramView = new EditText(context);
        if (param != null) {
            paramView.setText(param);
        }
        paramView.setOnFocusChangeListener(getOnFocusChangeListener());
        paramView.setSelectAllOnFocus(true);
        paramView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        paramView.setId(id);
        paramView.setTag(PARAM_VIEW_TAG);
        paramView.setHint(R.string.cpp_parameter);
        paramLabel.addView(paramView, new TextInputLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        rowView.addView(paramLabel, new LayoutParams(0, WRAP_CONTENT, 1));

        // for row is added at 0 position, the consequent rows
        addView(rowView, Math.max(0, getChildCount() - 1), new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        onParamsChanged();
        return rowView;
    }

    private void onParamsChanged() {
        final boolean enabled = getParamsCount() < maxParams;
        headerView.setVisibility(enabled ? VISIBLE : GONE);
    }

    @NonNull
    private LayoutParams makeButtonParams() {
        return new LayoutParams(imageButtonSize, imageButtonSize);
    }

    private void downRow(@Nonnull ViewGroup row) {
        final int index = indexOfChild(row);
        if (index < getChildCount() - 1 - FOOTERS) {
            swap(row, getRow(index + 1));
        }
    }

    private void upRow(@Nonnull ViewGroup row) {
        final int index = indexOfChild(row);
        if (index > 0) {
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
        Check.isTrue(index >= 0 && index < getParamsCount());
        return (ViewGroup) getChildAt(index);
    }

    public void removeRow(@Nonnull ViewGroup row) {
        removeView(row);
        onParamsChanged();
    }

    @Nonnull
    public List<String> getParams() {
        final List<String> params = new ArrayList<>(getParamsCount());

        for (int i = 0; i < getParamsCount(); i++) {
            final ViewGroup row = getRow(i);
            final EditText paramView = getParamView(row);
            params.add(paramView.getText().toString());
        }

        return params;
    }

    private int getParamsCount() {
        return getChildCount() - FOOTERS;
    }

    public void setMaxParams(int maxParams) {
        this.maxParams = maxParams;
        onParamsChanged();
    }

    @Nonnull
    private EditText getParamView(@Nonnull ViewGroup row) {
        final TextInputLayout paramLabel = getParamLabel(row);
        final EditText paramView = App.find(paramLabel, EditText.class);
        if (paramView != null) {
            return paramView;
        }
        Check.shouldNotHappen();
        return null;
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
        final int[] rowIds = new int[childCount - FOOTERS];
        for (int i = 0; i < childCount - FOOTERS; i++) {
            final ViewGroup row = getRow(i);
            final EditText paramView = getParamView(row);
            rowIds[i] = paramView.getId();
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
        return getParamLabel(getRow(param));
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
