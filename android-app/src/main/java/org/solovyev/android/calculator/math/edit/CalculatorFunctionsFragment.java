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

package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.ListView;
import com.melnykov.fab.FloatingActionButton;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.function.FunctionEditDialogFragment;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 4:55 PM
 */
public class CalculatorFunctionsFragment extends AbstractMathEntityListFragment<Function> {

	public static final String CREATE_FUNCTION_EXTRA = "create_function";

	public CalculatorFunctionsFragment() {
		super(CalculatorFragmentType.functions);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			final Parcelable parcelable = bundle.getParcelable(CREATE_FUNCTION_EXTRA);
			if (parcelable instanceof FunctionEditDialogFragment.Input) {
				FunctionEditDialogFragment.showDialog((FunctionEditDialogFragment.Input) parcelable, getFragmentManager());

				// in order to stop intent for other tabs
				bundle.remove(CREATE_FUNCTION_EXTRA);
			}
		}

		setHasOptionsMenu(true);

	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		final ListView lv = getListView();
		final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
		fab.setVisibility(View.VISIBLE);
		fab.attachToListView(lv);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FunctionEditDialogFragment.showDialog(FunctionEditDialogFragment.Input.newInstance(), getFragmentManager());
			}
		});
	}

	@Override
	protected AMenuItem<Function> getOnClickAction() {
		return LongClickMenuItem.use;
	}

	@Nonnull
	@Override
	protected List<LabeledMenuItem<Function>> getMenuItemsOnLongClick(@Nonnull Function item) {
		List<LabeledMenuItem<Function>> result = new ArrayList<LabeledMenuItem<Function>>(Arrays.asList(LongClickMenuItem.values()));

		final CalculatorMathRegistry<Function> functionsRegistry = Locator.getInstance().getEngine().getFunctionsRegistry();
		if (Strings.isEmpty(functionsRegistry.getDescription(item.getName()))) {
			result.remove(LongClickMenuItem.copy_description);
		}

		final Function function = functionsRegistry.get(item.getName());
		if (function == null || function.isSystem()) {
			result.remove(LongClickMenuItem.edit);
			result.remove(LongClickMenuItem.remove);
		}

		return result;
	}

	@Nonnull
	@Override
	protected MathEntityDescriptionGetter getDescriptionGetter() {
		return new MathEntityDescriptionGetterImpl(Locator.getInstance().getEngine().getFunctionsRegistry());
	}

	@Nonnull
	@Override
	protected List<Function> getMathEntities() {
		return new ArrayList<Function>(Locator.getInstance().getEngine().getFunctionsRegistry().getEntities());
	}

	@Override
	protected String getMathEntityCategory(@Nonnull Function function) {
		return Locator.getInstance().getEngine().getFunctionsRegistry().getCategory(function);
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		super.onCalculatorEvent(calculatorEventData, calculatorEventType, data);

		switch (calculatorEventType) {
			case function_added:
				processFunctionAdded((Function) data);
				break;

			case function_changed:
				processFunctionChanged((Change<IFunction>) data);
				break;

			case function_removed:
				processFunctionRemoved((Function) data);
				break;
		}
	}


	private void processFunctionRemoved(@Nonnull final Function function) {
		if (this.isInCategory(function)) {
			getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					removeFromAdapter(function);
					notifyAdapter();
				}
			});
		}
	}

	private void processFunctionChanged(@Nonnull final Change<IFunction> change) {
		final IFunction newFunction = change.getNewValue();

		if (newFunction instanceof Function) {

			if (this.isInCategory((Function) newFunction)) {

				getUiHandler().post(new Runnable() {
					@Override
					public void run() {
						IFunction oldValue = change.getOldValue();

						if (oldValue.isIdDefined()) {
							final MathEntityArrayAdapter<Function> adapter = getAdapter();
							if (adapter != null) {
								for (int i = 0; i < adapter.getCount(); i++) {
									final Function functionFromAdapter = adapter.getItem(i);
									if (functionFromAdapter.isIdDefined() && oldValue.getId().equals(functionFromAdapter.getId())) {
										adapter.remove(functionFromAdapter);
										break;
									}
								}
							}
						}

						addToAdapter((Function) newFunction);
						sort();
					}
				});
			}
		} else {
			throw new IllegalArgumentException("Function must be instance of jscl.math.function.Function class!");
		}
	}

	private void processFunctionAdded(@Nonnull final Function function) {
		if (this.isInCategory(function)) {
			getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					addToAdapter(function);
					sort();
				}
			});
		}
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	private static enum LongClickMenuItem implements LabeledMenuItem<Function> {
		use(R.string.c_use) {
			@Override
			public void onClick(@Nonnull Function function, @Nonnull Context context) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_function, function);
			}
		},

		edit(R.string.c_edit) {
			@Override
			public void onClick(@Nonnull Function function, @Nonnull Context context) {
				if (function instanceof IFunction) {
					FunctionEditDialogFragment.showDialog(FunctionEditDialogFragment.Input.newFromFunction((IFunction) function), ((ActionBarActivity) context).getSupportFragmentManager());
				}
			}
		},

		remove(R.string.c_remove) {
			@Override
			public void onClick(@Nonnull Function function, @Nonnull Context context) {
				MathEntityRemover.newFunctionRemover(function, null, context, context).showConfirmationDialog();
			}
		},

		copy_description(R.string.c_copy_description) {
			@Override
			public void onClick(@Nonnull Function function, @Nonnull Context context) {
				final String text = Locator.getInstance().getEngine().getFunctionsRegistry().getDescription(function.getName());
				if (!Strings.isEmpty(text)) {
					final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
					clipboard.setText(text);
				}
			}
		};
		private final int captionId;

		LongClickMenuItem(int captionId) {
			this.captionId = captionId;
		}

		@Nonnull
		@Override
		public String getCaption(@Nonnull Context context) {
			return context.getString(captionId);
		}
	}
}
