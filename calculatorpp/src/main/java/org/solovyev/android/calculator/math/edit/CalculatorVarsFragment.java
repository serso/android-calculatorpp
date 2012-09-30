/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
import org.solovyev.android.calculator.model.Var;
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
				createEditVariableDialog(this, null, null, varValue, null);

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
		createEditVariableDialog(this, null, null, null, null);
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

	private static void createEditVariableDialog(@NotNull final AbstractMathEntityListFragment<IConstant> fragment,
												 @Nullable final IConstant var,
												 @Nullable final String name,
												 @Nullable final String value,
												 @Nullable final String description) {
        final FragmentActivity activity = fragment.getActivity();

        if (var == null || !var.isSystem()) {

			final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			final View editView = layoutInflater.inflate(R.layout.var_edit, null);

			final String errorMsg = fragment.getString(R.string.c_char_is_not_accepted);

			final EditText editName = (EditText) editView.findViewById(R.id.var_edit_name);
			editName.setText(name);
			editName.addTextChangedListener(new TextWatcher() {

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					for (int i = 0; i < s.length(); i++) {
						char c = s.charAt(i);
						if (!acceptableChars.contains(c)) {
							s.delete(i, i + 1);
                            Toast.makeText(activity, String.format(errorMsg, c), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
			if (!StringUtils.isEmpty(value)) {
				editValue.setText(value);
			}

			final EditText editDescription = (EditText) editView.findViewById(R.id.var_edit_description);
			editDescription.setText(description);

			final Var.Builder varBuilder;
			if (var != null) {
				varBuilder = new Var.Builder(var);
			} else {
				varBuilder = new Var.Builder();
			}

			final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
					.setCancelable(true)
					.setNegativeButton(R.string.c_cancel, null)
					.setPositiveButton(R.string.c_save, new VarEditorSaver<IConstant>(varBuilder, var, editView, fragment, CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry(), new VarEditorSaver.EditorCreator<IConstant>() {
						@Override
						public void showEditor(@NotNull AbstractMathEntityListFragment<IConstant> activity, @Nullable IConstant editedInstance, @Nullable String name, @Nullable String value, @Nullable String description) {
							createEditVariableDialog(activity, editedInstance, name, value, description);
						}
					}))
					.setView(editView);

			if (var != null) {
				// EDIT mode

				builder.setTitle(R.string.c_var_edit_var);
				builder.setNeutralButton(R.string.c_remove, new MathEntityRemover<IConstant>(var, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						createEditVariableDialog(fragment, var, name, value, description);
					}
				}, CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry(), fragment));
			} else {
				// CREATE mode

				builder.setTitle(R.string.c_var_create_var);
			}

			builder.create().show();
		} else {
			Toast.makeText(activity, fragment.getString(R.string.c_sys_var_cannot_be_changed), Toast.LENGTH_LONG).show();
		}
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
				createEditVariableDialog(this, null, null, null, null);
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
