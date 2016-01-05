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

package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.view.ViewBuilder;
import org.solovyev.android.view.ViewFromLayoutBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlotFunctionListItem implements ListItem {

    private static final String PREFIX = "plot_function_";

    @Nonnull
    private PlotFunction plotFunction;

    @Nonnull
    private ViewBuilder<View> viewBuilder;

    @Nonnull
    private String tag;

    public PlotFunctionListItem(@Nonnull PlotFunction plotFunction) {
        this.plotFunction = plotFunction;
        this.viewBuilder = ViewFromLayoutBuilder.newInstance(R.layout.cpp_plot_function_list_item);
        this.tag = PREFIX + plotFunction.getXyFunction().getExpressionString();
    }

    @Nullable
    @Override
    public OnClickAction getOnClickAction() {
        return null;
    }

    @Nullable
    @Override
    public OnClickAction getOnLongClickAction() {
        return null;
    }

    @Nonnull
    @Override
    public View updateView(@Nonnull Context context, @Nonnull View view) {
        final Object viewTag = view.getTag();
        if (viewTag instanceof String) {
            if (this.tag.equals(viewTag)) {
                return view;
            } else if (((String) viewTag).startsWith(PREFIX)) {
                fillView(view, context);
                return view;
            } else {
                return build(context);
            }
        }

        return build(context);
    }

    @Nonnull
    @Override
    public View build(@Nonnull Context context) {
        final View root = buildView(context);
        fillView(root, context);
        return root;
    }

    private View buildView(@Nonnull Context context) {
        return viewBuilder.build(context);
    }

    private void fillView(@Nonnull View root, @Nonnull final Context context) {
        root.setTag(tag);

        final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

        final TextView expressionTextView = (TextView) root.findViewById(R.id.cpp_plot_function_expression_textview);
        expressionTextView.setText(plotFunction.getXyFunction().getExpressionString());

        final CheckBox pinnedCheckBox = (CheckBox) root.findViewById(R.id.cpp_plot_function_pinned_checkbox);
        pinnedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean pin) {
                if (pin) {
                    if (!plotFunction.isPinned()) {
                        plotFunction = plotter.pin(plotFunction);
                    }
                } else {
                    if (plotFunction.isPinned()) {
                        plotFunction = plotter.unpin(plotFunction);
                    }
                }
            }
        });
        pinnedCheckBox.setChecked(plotFunction.isPinned());

        final CheckBox visibleCheckBox = (CheckBox) root.findViewById(R.id.cpp_plot_function_visible_checkbox);
        visibleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean show) {
                if (show) {
                    if (!plotFunction.isVisible()) {
                        plotFunction = plotter.show(plotFunction);
                    }
                } else {
                    if (plotFunction.isVisible()) {
                        plotFunction = plotter.hide(plotFunction);
                    }
                }
            }
        });
        visibleCheckBox.setChecked(plotFunction.isVisible());

        final ImageButton settingsButton = (ImageButton) root.findViewById(R.id.cpp_plot_function_settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorPlotFunctionSettingsActivity.startActivity(context, plotFunction);
            }
        });
    }
}
