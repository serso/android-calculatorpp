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

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import com.melnykov.fab.FloatingActionButton;
import jscl.math.function.IConstant;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.Collections;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 10:55 PM
 */
public class CalculatorVarsFragment extends AbstractMathEntityListFragment<IConstant> {

	public static final String CREATE_VAR_EXTRA_STRING = "create_var";

	public CalculatorVarsFragment() {
		super(CalculatorFragmentType.variables);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			final String varValue = bundle.getString(CREATE_VAR_EXTRA_STRING);
			if (!Strings.isEmpty(varValue)) {
				VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromValue(varValue), getFragmentManager());

				// in order to stop intent for other tabs
				bundle.remove(CREATE_VAR_EXTRA_STRING);
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
				VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newInstance(), getFragmentManager());
			}
		});
	}

	@Override
	protected AMenuItem<IConstant> getOnClickAction() {
		return LongClickMenuItem.use;
	}

	@Nonnull
	@Override
	protected List<LabeledMenuItem<IConstant>> getMenuItemsOnLongClick(@Nonnull IConstant item) {
		final List<LabeledMenuItem<IConstant>> result = new ArrayList<LabeledMenuItem<IConstant>>(Arrays.asList(LongClickMenuItem.values()));

		if (item.isSystem()) {
			result.remove(LongClickMenuItem.edit);
			result.remove(LongClickMenuItem.remove);
		}

		if (Strings.isEmpty(Locator.getInstance().getEngine().getVarsRegistry().getDescription(item.getName()))) {
			result.remove(LongClickMenuItem.copy_description);
		}

		if (Strings.isEmpty(item.getValue())) {
			result.remove(LongClickMenuItem.copy_value);
		}

		return result;
	}

	@Nonnull
	@Override
	protected MathEntityDescriptionGetter getDescriptionGetter() {
		return new MathEntityDescriptionGetterImpl(Locator.getInstance().getEngine().getVarsRegistry());
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void addVarButtonClickHandler(@Nonnull View v) {
		VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newInstance(), this.getActivity().getSupportFragmentManager());
	}

	@Nonnull
	@Override
	protected List<IConstant> getMathEntities() {
		final List<IConstant> result = new ArrayList<IConstant>(Locator.getInstance().getEngine().getVarsRegistry().getEntities());

		Collections.removeAll(result, new JPredicate<IConstant>() {
			@Override
			public boolean apply(@Nullable IConstant var) {
				return var != null && Collections.contains(var.getName(), MathType.INFINITY_JSCL, MathType.NAN);
			}
		});

		return result;
	}

	@Override
	protected String getMathEntityCategory(@Nonnull IConstant var) {
		return Locator.getInstance().getEngine().getVarsRegistry().getCategory(var);
	}

	public static boolean isValidValue(@Nonnull String value) {
		try {
			final PreparedExpression expression = ToJsclTextProcessor.getInstance().process(value);
			final List<IConstant> constants = expression.getUndefinedVars();
			return constants.isEmpty();
		} catch (RuntimeException e) {
			return true;
		} catch (CalculatorParseException e) {
			return true;
		}
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		super.onCalculatorEvent(calculatorEventData, calculatorEventType, data);

		switch (calculatorEventType) {
			case constant_added:
				processConstantAdded((IConstant) data);
				break;

			case constant_changed:
				processConstantChanged((Change<IConstant>) data);
				break;

			case constant_removed:
				processConstantRemoved((IConstant) data);
				break;
		}
	}

	private void processConstantRemoved(@Nonnull final IConstant constant) {
		if (this.isInCategory(constant)) {
			getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					removeFromAdapter(constant);
					notifyAdapter();
				}
			});
		}
	}

	private void processConstantChanged(@Nonnull final Change<IConstant> change) {
		final IConstant newConstant = change.getNewValue();
		if (this.isInCategory(newConstant)) {
			getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					removeFromAdapter(change.getOldValue());
					addToAdapter(newConstant);
					sort();
				}
			});
		}
	}

	private void processConstantAdded(@Nonnull final IConstant constant) {
		if (this.isInCategory(constant)) {
			getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					addToAdapter(constant);
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

	private static enum LongClickMenuItem implements LabeledMenuItem<IConstant> {
		use(R.string.c_use) {
			@Override
			public void onClick(@Nonnull IConstant data, @Nonnull Context context) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_constant, data);
			}
		},

		edit(R.string.c_edit) {
			@Override
			public void onClick(@Nonnull IConstant constant, @Nonnull Context context) {
				VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromConstant(constant), ((ActionBarActivity) context).getSupportFragmentManager());
			}
		},

		remove(R.string.c_remove) {
			@Override
			public void onClick(@Nonnull IConstant constant, @Nonnull Context context) {
				MathEntityRemover.newConstantRemover(constant, null, context, context).showConfirmationDialog();
			}
		},

		copy_value(R.string.c_copy_value) {
			@Override
			public void onClick(@Nonnull IConstant data, @Nonnull Context context) {
				final String text = data.getValue();
				if (!Strings.isEmpty(text)) {
					if (text == null) throw new AssertionError();
					Locator.getInstance().getClipboard().setText(text);
				}
			}
		},

		copy_description(R.string.c_copy_description) {
			@Override
			public void onClick(@Nonnull IConstant data, @Nonnull Context context) {
				final String text = Locator.getInstance().getEngine().getVarsRegistry().getDescription(data.getName());
				if (!Strings.isEmpty(text)) {
					if (text == null) throw new AssertionError();
					Locator.getInstance().getClipboard().setText(text);
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
