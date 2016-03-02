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

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.otto.Bus;
import jscl.NumeralBase;
import jscl.math.Generic;
import org.solovyev.android.calculator.converter.ConverterFragment;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.plotter.Plotter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class DisplayFragment extends BaseFragment implements View.OnClickListener,
        MenuItem.OnMenuItemClickListener {

    private enum ConversionMenuItem {

        to_bin(NumeralBase.bin, R.string.convert_to_bin),
        to_dec(NumeralBase.dec, R.string.convert_to_dec),
        to_hex(NumeralBase.hex, R.string.convert_to_hex);

        @Nonnull
        public final NumeralBase toNumeralBase;
        public final int title;

        ConversionMenuItem(@Nonnull NumeralBase toNumeralBase, @StringRes int title) {
            this.toNumeralBase = toNumeralBase;
            this.title = title;
        }

        @Nullable
        public static ConversionMenuItem getByTitle(int title) {
            switch (title) {
                case R.string.convert_to_bin:
                    return to_bin;
                case R.string.convert_to_dec:
                    return to_dec;
                case R.string.convert_to_hex:
                    return to_hex;
            }
            return null;
        }
    }

    @Bind(R.id.calculator_display)
    DisplayView displayView;
    @Inject
    SharedPreferences preferences;
    @Inject
    ErrorReporter errorReporter;
    @Inject
    Display display;
    @Inject
    ActivityLauncher launcher;
    @Inject
    Bus bus;
    @Inject
    Plotter plotter;
    @Inject
    Calculator calculator;
    @Inject
    Engine engine;

    public DisplayFragment() {
        super(R.layout.cpp_app_display);
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        display.setView(displayView);
        displayView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        display.clearView(displayView);
        super.onDestroyView();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {
        final DisplayState state = display.getState();
        if (!state.valid) {
            return;
        }
        addMenu(menu, R.string.cpp_copy, this);

        final Generic result = state.getResult();
        final JsclOperation operation = state.getOperation();
        if (result != null) {
            if (operation == JsclOperation.numeric && result.getConstants().isEmpty()) {
                for (ConversionMenuItem item : ConversionMenuItem.values()) {
                    if (isMenuItemVisible(item, result)) {
                        addMenu(menu, item.title, this);
                    }
                }
                if (result.toDouble() != null) {
                    addMenu(menu, R.string.c_convert, this);
                }
            }
            if (launcher.canPlot(result)) {
                addMenu(menu, R.string.c_plot, this);
            }
        }
    }

    protected boolean isMenuItemVisible(@NonNull ConversionMenuItem menuItem,
            @Nonnull Generic generic) {
        final NumeralBase fromNumeralBase = engine.getMathEngine().getNumeralBase();
        if (fromNumeralBase != menuItem.toNumeralBase) {
            return calculator.canConvert(generic, fromNumeralBase, menuItem.toNumeralBase);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        final DisplayState state = display.getState();
        if (state.valid) {
            v.setOnCreateContextMenuListener(this);
            v.showContextMenu();
            v.setOnCreateContextMenuListener(null);
        } else {
            showEvaluationError(v.getContext(), state.text);
        }
    }

    public static void showEvaluationError(@Nonnull Context context,
                                           @Nonnull final String errorMessage) {
        new AlertDialog.Builder(context, App.getTheme().alertDialogTheme)
                .setPositiveButton(R.string.cpp_cancel, null)
                .setMessage(errorMessage).create().show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final DisplayState state = display.getState();
        final Generic result = state.getResult();
        switch (item.getItemId()) {
            case R.string.cpp_copy:
                display.copy();
                return true;
            case R.string.convert_to_bin:
            case R.string.convert_to_dec:
            case R.string.convert_to_hex:
                final ConversionMenuItem menuItem = ConversionMenuItem.getByTitle(item.getItemId());
                if (menuItem == null) {
                    return false;
                }
                if (result != null) {
                    calculator.convert(state, menuItem.toNumeralBase);
                }
                return true;
            case R.string.c_convert:
                ConverterFragment.show(getActivity(), getValue(result));
                return true;
            case R.string.c_plot:
                launcher.plot(result);
                return true;
            default:
                return false;
        }
    }

    private static double getValue(@Nullable Generic result) {
        if (result == null) {
            return 1d;
        }
        final Double value = result.toDouble();
        if (value == null) {
            return 1d;
        }
        return value;
    }
}
