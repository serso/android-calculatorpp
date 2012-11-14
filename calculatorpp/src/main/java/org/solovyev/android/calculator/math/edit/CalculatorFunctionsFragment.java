/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.calculator.function.FunctionEditDialogFragment;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.text.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 4:55 PM
 */
public class CalculatorFunctionsFragment extends AbstractMathEntityListFragment<Function> {

	public static final String CREATE_FUNCTION_EXTRA_STRING = "create_function";

    public CalculatorFunctionsFragment() {
        super(CalculatorFragmentType.functions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final Function function = (Function) parent.getItemAtPosition(position);
				if (function instanceof CustomFunction) {
					createEditVariableDialog(CalculatorFunctionsTabActivity.this,
							((CustomFunction) function),
							function.getName(),
							((CustomFunction) function).getContent(),
							((CustomFunction) function).getParameterNames(),
							null);
				}
				return true;
			}
		});*/

		/*final Intent intent = getIntent();
		if (intent != null) {
			final String varValue = intent.getStringExtra(CREATE_FUN_EXTRA_STRING);
			if (!StringUtils.isEmpty(varValue)) {
				createEditVariableDialog(this, null, null, varValue, null, null);

				// in order to stop intent for other tabs
				intent.removeExtra(CREATE_FUN_EXTRA_STRING);
			}
		}*/
        setHasOptionsMenu(true);

	}

    @Override
    protected AMenuItem<Function> getOnClickAction() {
        return LongClickMenuItem.use;
    }

    @NotNull
	@Override
	protected List<LabeledMenuItem<Function>> getMenuItemsOnLongClick(@NotNull Function item) {
		List<LabeledMenuItem<Function>> result = new ArrayList<LabeledMenuItem<Function>>(Arrays.asList(LongClickMenuItem.values()));

        final CalculatorMathRegistry<Function> functionsRegistry = CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry();
        if ( StringUtils.isEmpty(functionsRegistry.getDescription(item.getName())) ) {
			result.remove(LongClickMenuItem.copy_description);
		}

        final Function function = functionsRegistry.get(item.getName());
        if (function == null || function.isSystem()) {
            result.remove(LongClickMenuItem.edit);
            result.remove(LongClickMenuItem.remove);
        }
		
		return result;
	}
	@NotNull
	@Override
	protected MathEntityDescriptionGetter getDescriptionGetter() {
		return new MathEntityDescriptionGetterImpl(CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry());
	}

	@NotNull
	@Override
	protected List<Function> getMathEntities() {
		return new ArrayList<Function>(CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry().getEntities());
	}

	@Override
	protected String getMathEntityCategory(@NotNull Function function) {
		return CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry().getCategory(function);
	}

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        super.onCalculatorEvent(calculatorEventData, calculatorEventType, data);

        switch (calculatorEventType) {
            case function_added:
                processFunctionAdded((Function) data);
                break;

            case function_changed:
                processFunctionChanged((Change<Function>) data);
                break;

            case function_removed:
                processFunctionRemoved((Function) data);
                break;
        }
    }


    private void processFunctionRemoved(@NotNull final Function function) {
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

    private void processFunctionChanged(@NotNull final Change<Function> change) {
        final Function newFunction = change.getNewValue();
        if (this.isInCategory(newFunction)) {
            getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    removeFromAdapter(change.getOldValue());
                    addToAdapter(newFunction);
                    sort();
                }
            });
        }
    }

    private void processFunctionAdded(@NotNull final Function function) {
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
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.functions_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;

        switch (item.getItemId()) {
            case R.id.functions_menu_add_function:
                FunctionEditDialogFragment.showDialog(FunctionEditDialogFragment.Input.newInstance(), this.getActivity().getSupportFragmentManager());
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
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
            public void onClick(@NotNull Function function, @NotNull Context context) {
                CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_function, function);
            }
        },

        edit(R.string.c_edit) {
            @Override
            public void onClick(@NotNull Function function, @NotNull Context context) {
				if (function instanceof IFunction) {
					FunctionEditDialogFragment.showDialog(FunctionEditDialogFragment.Input.newFromFunction((IFunction) function), ((SherlockFragmentActivity) context).getSupportFragmentManager());
				}
			}
        },

        remove(R.string.c_remove) {
            @Override
            public void onClick(@NotNull Function function, @NotNull Context context) {
                MathEntityRemover.newFunctionRemover(function, null, context, context).showConfirmationDialog();
            }
        },

        copy_description(R.string.c_copy_description) {
            @Override
            public void onClick(@NotNull Function function, @NotNull Context context) {
                final String text = CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry().getDescription(function.getName());
                if (!StringUtils.isEmpty(text)) {
                    final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                }
            }
        };
        private final int captionId;

        LongClickMenuItem(int captionId) {
            this.captionId = captionId;
        }

        @NotNull
        @Override
        public String getCaption(@NotNull Context context) {
            return context.getString(captionId);
        }
    }
}
