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
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.CollectionsUtils;
import org.solovyev.common.text.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 10:55 PM
 */
public class CalculatorVarsFragment extends AbstractMathEntityListFragment<IConstant> {
	
	public static final String CREATE_VAR_EXTRA_STRING = "org.solovyev.android.calculator.math.edit.CalculatorVarsTabActivity_create_var";

	@Override
	protected int getLayoutId() {
		return R.layout.vars_fragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			final String varValue = bundle.getString(CREATE_VAR_EXTRA_STRING);
			if (!StringUtils.isEmpty(varValue)) {
				VarEditDialogFragment.createEditVariableDialog(this, VarEditDialogFragment.Input.newFromValue(varValue));

				// in order to stop intent for other tabs
                bundle.remove(CREATE_VAR_EXTRA_STRING);
			}
		}

        setHasOptionsMenu(true);
	}

    @Override
    protected int getTitleResId() {
        return R.string.c_vars;
    }

    @Override
    protected AMenuItem<IConstant> getOnClickAction() {
        return LongClickMenuItem.use;
    }

    @NotNull
	@Override
	protected List<LabeledMenuItem<IConstant>> getMenuItemsOnLongClick(@NotNull IConstant item) {
		final List<LabeledMenuItem<IConstant>> result = new ArrayList<LabeledMenuItem<IConstant>>(Arrays.asList(LongClickMenuItem.values()));
		
		if ( item.isSystem() ) {
			result.remove(LongClickMenuItem.edit);
			result.remove(LongClickMenuItem.remove);
		}
		
		if ( StringUtils.isEmpty(CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().getDescription(item.getName())) ) {
			result.remove(LongClickMenuItem.copy_description);
		}

		if ( StringUtils.isEmpty(item.getValue()) ) {
			result.remove(LongClickMenuItem.copy_value);
		}
		
		return result;
	}

	@NotNull
	@Override
	protected MathEntityDescriptionGetter getDescriptionGetter() {
		return new MathEntityDescriptionGetterImpl(CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry());
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void addVarButtonClickHandler(@NotNull View v) {
		VarEditDialogFragment.createEditVariableDialog(this, VarEditDialogFragment.Input.newInstance());
	}

	@NotNull
	@Override
	protected List<IConstant> getMathEntities() {
		final List<IConstant> result = new ArrayList<IConstant>(CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().getEntities());

		CollectionsUtils.removeAll(result, new JPredicate<IConstant>() {
			@Override
			public boolean apply(@Nullable IConstant var) {
				return var != null && CollectionsUtils.contains(var.getName(), MathType.INFINITY_JSCL, MathType.NAN);
			}
		});

		return result;
	}

	@Override
	protected String getMathEntityCategory(@NotNull IConstant var) {
		return CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().getCategory(var);
	}

    public static boolean isValidValue(@NotNull String value) {
		// now every string might be constant
		return true;
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
        inflater.inflate(R.menu.var_menu, menu);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
			case R.id.var_menu_add_var:
				VarEditDialogFragment.createEditVariableDialog(this, VarEditDialogFragment.Input.newInstance());
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

    private static enum LongClickMenuItem implements LabeledMenuItem<IConstant>{
        use(R.string.c_use) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_constant, data);
            }
        },

        edit(R.string.c_edit) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                /*if (context instanceof AbstractMathEntityListFragment) {
                    createEditVariableDialog((AbstractMathEntityListFragment<IConstant>)context, data, data.getName(), StringUtils.getNotEmpty(data.getValue(), ""), data.getDescription());
                }*/
            }
        },

        remove(R.string.c_remove) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                /*if (context instanceof AbstractMathEntityListFragment) {
                    new MathEntityRemover<IConstant>(data, null, CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry(), ((AbstractMathEntityListFragment<IConstant>) context)).showConfirmationDialog();
                }*/
            }
        },

        copy_value(R.string.c_copy_value) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                final String text = data.getValue();
                if (!StringUtils.isEmpty(text)) {
                    final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                }
            }
        },

        copy_description(R.string.c_copy_description) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                final String text = CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().getDescription(data.getName());
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
