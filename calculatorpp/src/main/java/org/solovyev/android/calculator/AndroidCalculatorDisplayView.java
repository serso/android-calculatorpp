/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.android.view.AutoResizeTextView;
import org.solovyev.common.collections.CollectionsUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:58 PM
 */
public class AndroidCalculatorDisplayView extends AutoResizeTextView implements CalculatorDisplayView {

    public static enum MenuItem implements LabeledMenuItem<CalculatorDisplayViewState> {

        copy(R.string.c_copy) {
            @Override
            public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
                CalculatorModel.copyResult(context, data);
            }
        },

        convert_to_bin(R.string.convert_to_bin) {
            @Override
            public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
                ConversionMenuItem.convert_to_bin.onClick(data, context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return ConversionMenuItem.convert_to_bin.isItemVisibleFor(generic, operation);
            }
        },

        convert_to_dec(R.string.convert_to_dec) {
            @Override
            public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
                ConversionMenuItem.convert_to_dec.onClick(data, context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return ConversionMenuItem.convert_to_dec.isItemVisibleFor(generic, operation);
            }
        },

        convert_to_hex(R.string.convert_to_hex) {
            @Override
            public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
                ConversionMenuItem.convert_to_hex.onClick(data, context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return ConversionMenuItem.convert_to_hex.isItemVisibleFor(generic, operation);
            }
        },

        convert(R.string.c_convert) {
            @Override
            public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
                final Generic result = data.getResult();
                if (result != null) {
                    new NumeralBaseConverterDialog(result.toString()).show(context);
                }
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return operation == JsclOperation.numeric && generic.getConstants().isEmpty();
            }
        },

        plot(R.string.c_plot) {
            @Override
            public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
                final Generic generic = data.getResult();
                assert generic != null;

                final Constant constant = CollectionsUtils.getFirstCollectionElement(getNotSystemConstants(generic));
                assert constant != null;
                CalculatorActivityLauncher.plotGraph(context, generic, constant);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                boolean result = false;

                if (operation == JsclOperation.simplify) {
                    if (getNotSystemConstants(generic).size() == 1) {
                        result = true;
                    }
                }

                return result;
            }

            @NotNull
            private Set<Constant> getNotSystemConstants(@NotNull Generic generic) {
                final Set<Constant> notSystemConstants = new HashSet<Constant>();

                for (Constant constant : generic.getConstants()) {
                    IConstant var = CalculatorEngine.instance.getVarsRegistry().get(constant.getName());
                    if (var != null && !var.isSystem() && !var.isDefined()) {
                        notSystemConstants.add(constant);
                    }
                }

                return notSystemConstants;
            }
        };

        private final int captionId;

        MenuItem(int captionId) {
            this.captionId = captionId;
        }

        public final boolean isItemVisible(@NotNull CalculatorDisplayViewState displayViewState) {
            //noinspection ConstantConditions
            return displayViewState.isValid() && displayViewState.getResult() != null && isItemVisibleFor(displayViewState.getResult(), displayViewState.getOperation());
        }

        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            return true;
        }

        @NotNull
        @Override
        public String getCaption(@NotNull Context context) {
            return context.getString(captionId);
        }
    }

    @NotNull
    private CalculatorDisplayViewState state = CalculatorDisplayViewStateImpl.newDefaultInstance();

    @NotNull
    private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, false, CalculatorEngine.instance.getEngine());

    public AndroidCalculatorDisplayView(Context context) {
        super(context);
    }

    public AndroidCalculatorDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AndroidCalculatorDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isValid() {
        return this.state.isValid();
    }


    @Override
    public void setState(@NotNull CalculatorDisplayViewState state) {
        this.state = state;
        if ( state.isValid() ) {
            setTextColor(getResources().getColor(R.color.default_text_color));
            setText(state.getStringResult());
        } else {
            setTextColor(getResources().getColor(R.color.display_error_text_color));
            setText(state.getErrorMessage());
        }
    }

    @NotNull
    @Override
    public CalculatorDisplayViewState getState() {
        return this.state;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public synchronized void redraw() {
        if (isValid()) {
            String text = getText().toString();

            Log.d(this.getClass().getName(), text);

            try {
                TextHighlighter.Result result = textHighlighter.process(text);
                text = result.toString();
            } catch (CalculatorParseException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }

            Log.d(this.getClass().getName(), text);
            super.setText(Html.fromHtml(text), BufferType.EDITABLE);
        }

        // todo serso: think where to move it (keep in mind org.solovyev.android.view.AutoResizeTextView.resetTextSize())
        setAddEllipsis(false);
        setMinTextSize(10);
        resizeText();
    }

    @Override
    public int getSelection() {
        return this.getSelectionStart();
    }

    @Override
    public void setSelection(int selection) {
        // not supported by TextView
    }
}
