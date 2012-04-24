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
import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.CalculatorParseException;
import org.solovyev.android.calculator.model.TextProcessor;
import org.solovyev.android.calculator.model.ToJsclTextProcessor;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.calculator.view.UnitConverterViewBuilder;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.android.view.AutoResizeTextView;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:58 PM
 */
public class CalculatorDisplay extends AutoResizeTextView implements ICalculatorDisplay{

    private static enum ConversionMenuItem implements AMenuItem<CalculatorDisplay> {
        convert_to_bin(NumeralBase.bin),
        convert_to_dec(NumeralBase.dec),
        convert_to_hex(NumeralBase.hex);

        @NotNull
        private final NumeralBase toNumeralBase;

        private ConversionMenuItem(@NotNull NumeralBase toNumeralBase) {
            this.toNumeralBase = toNumeralBase;
        }

        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            boolean result = false;

            if (operation == JsclOperation.numeric) {
                if (generic.getConstants().isEmpty()) {
                    try {
                        convert(generic);

                        // conversion possible => return true
                        result = true;

                    } catch (UnitConverterViewBuilder.ConversionException e) {
                        // conversion is not possible => return false
                    }
                }
            }

            return result;
        }

        @Override
        public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
            final NumeralBase fromNumeralBase = CalculatorEngine.instance.getEngine().getNumeralBase();

            String to;
            try {
                to = convert(data.getGenericResult());

                // add prefix
                if (fromNumeralBase != toNumeralBase) {
                    to = toNumeralBase.getJsclPrefix() + to;
                }
            } catch (UnitConverterViewBuilder.ConversionException e) {
                to = context.getString(R.string.c_error);
            }

            data.setText(to);
            data.redraw();
        }

        @NotNull
        private String convert(@NotNull Generic generic) throws UnitConverterViewBuilder.ConversionException {
            final NumeralBase fromNumeralBase = CalculatorEngine.instance.getEngine().getNumeralBase();

            if (fromNumeralBase != toNumeralBase) {
                String from = generic.toString();
                if (!StringUtils.isEmpty(from)) {
                    try {
                        from = ToJsclTextProcessor.getInstance().process(from).getExpression();
                    } catch (CalculatorParseException e) {
                        // ok, problems while processing occurred
                    }
                }

                return UnitConverterViewBuilder.doConversion(AndroidNumeralBase.getConverter(), from, AndroidNumeralBase.valueOf(fromNumeralBase), AndroidNumeralBase.valueOf(toNumeralBase));
            } else {
                return generic.toString();
            }
        }
    }

    public static enum MenuItem implements LabeledMenuItem<CalculatorDisplay> {

        copy(R.string.c_copy) {
            @Override
            public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
                CalculatorModel.copyResult(context, data);
            }
        },

        convert_to_bin(R.string.convert_to_bin) {
            @Override
            public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
                ConversionMenuItem.convert_to_bin.onClick(data, context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return ConversionMenuItem.convert_to_bin.isItemVisibleFor(generic, operation);
            }
        },

        convert_to_dec(R.string.convert_to_dec) {
            @Override
            public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
                ConversionMenuItem.convert_to_dec.onClick(data, context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return ConversionMenuItem.convert_to_dec.isItemVisibleFor(generic, operation);
            }
        },

        convert_to_hex(R.string.convert_to_hex) {
            @Override
            public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
                ConversionMenuItem.convert_to_hex.onClick(data, context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return ConversionMenuItem.convert_to_hex.isItemVisibleFor(generic, operation);
            }
        },

        convert(R.string.c_convert) {
            @Override
            public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
                new NumeralBaseConverterDialog(data.getGenericResult().toString()).show(context);
            }

            @Override
            protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
                return operation == JsclOperation.numeric && generic.getConstants().isEmpty();
            }
        },

		plot(R.string.c_plot) {
            @Override
            public void onClick(@NotNull CalculatorDisplay data, @NotNull Context context) {
                final Generic generic = data.getGenericResult();
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

		public final boolean isItemVisible(@NotNull CalculatorDisplay display) {
			//noinspection ConstantConditions
			return display.isValid() && display.getGenericResult() != null && isItemVisibleFor(display.getGenericResult(), display.getJsclOperation());
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

	private boolean valid = true;

	@Nullable
	private String errorMessage;

	@NotNull
	private JsclOperation jsclOperation = JsclOperation.numeric;

	@NotNull
	private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, false, CalculatorEngine.instance.getEngine());

	@Nullable
	private Generic genericResult;

	public CalculatorDisplay(Context context) {
		super(context);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			errorMessage = null;
			setTextColor(getResources().getColor(R.color.default_text_color));
		} else {
			setTextColor(getResources().getColor(R.color.display_error_text_color));
		}
	}

	@Override
	@Nullable
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public void setErrorMessage(@Nullable String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void setJsclOperation(@NotNull JsclOperation jsclOperation) {
		this.jsclOperation = jsclOperation;
	}

	@Override
	@NotNull
	public JsclOperation getJsclOperation() {
		return jsclOperation;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);

		setValid(true);
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
	public void setGenericResult(@Nullable Generic genericResult) {
		this.genericResult = genericResult;
	}

	@Override
	@Nullable
	public Generic getGenericResult() {
		return genericResult;
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
