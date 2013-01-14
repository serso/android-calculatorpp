/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.CalculatorFragmentType;
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
            if (!StringUtils.isEmpty(varValue)) {
                VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromValue(varValue), this.getActivity().getSupportFragmentManager());

                // in order to stop intent for other tabs
                bundle.remove(CREATE_VAR_EXTRA_STRING);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    protected AMenuItem<IConstant> getOnClickAction() {
        return LongClickMenuItem.use;
    }

    @NotNull
    @Override
    protected List<LabeledMenuItem<IConstant>> getMenuItemsOnLongClick(@NotNull IConstant item) {
        final List<LabeledMenuItem<IConstant>> result = new ArrayList<LabeledMenuItem<IConstant>>(Arrays.asList(LongClickMenuItem.values()));

        if (item.isSystem()) {
            result.remove(LongClickMenuItem.edit);
            result.remove(LongClickMenuItem.remove);
        }

        if (StringUtils.isEmpty(Locator.getInstance().getEngine().getVarsRegistry().getDescription(item.getName()))) {
            result.remove(LongClickMenuItem.copy_description);
        }

        if (StringUtils.isEmpty(item.getValue())) {
            result.remove(LongClickMenuItem.copy_value);
        }

        return result;
    }

    @NotNull
    @Override
    protected MathEntityDescriptionGetter getDescriptionGetter() {
        return new MathEntityDescriptionGetterImpl(Locator.getInstance().getEngine().getVarsRegistry());
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void addVarButtonClickHandler(@NotNull View v) {
        VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newInstance(), this.getActivity().getSupportFragmentManager());
    }

    @NotNull
    @Override
    protected List<IConstant> getMathEntities() {
        final List<IConstant> result = new ArrayList<IConstant>(Locator.getInstance().getEngine().getVarsRegistry().getEntities());

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
        return Locator.getInstance().getEngine().getVarsRegistry().getCategory(var);
    }

    public static boolean isValidValue(@NotNull String value) {
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

    /*
    **********************************************************************
    *
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vars_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;

        switch (item.getItemId()) {
            case R.id.var_menu_add_var:
                VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newInstance(), this.getActivity().getSupportFragmentManager());
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
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

    private void processConstantRemoved(@NotNull final IConstant constant) {
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

    private void processConstantChanged(@NotNull final Change<IConstant> change) {
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

    private void processConstantAdded(@NotNull final IConstant constant) {
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
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_constant, data);
            }
        },

        edit(R.string.c_edit) {
            @Override
            public void onClick(@NotNull IConstant constant, @NotNull Context context) {
                VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromConstant(constant), ((SherlockFragmentActivity) context).getSupportFragmentManager());
            }
        },

        remove(R.string.c_remove) {
            @Override
            public void onClick(@NotNull IConstant constant, @NotNull Context context) {
                MathEntityRemover.newConstantRemover(constant, null, context, context).showConfirmationDialog();
            }
        },

        copy_value(R.string.c_copy_value) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                final String text = data.getValue();
                if (!StringUtils.isEmpty(text)) {
                    assert text != null;
                    Locator.getInstance().getClipboard().setText(text);
                }
            }
        },

        copy_description(R.string.c_copy_description) {
            @Override
            public void onClick(@NotNull IConstant data, @NotNull Context context) {
                final String text = Locator.getInstance().getEngine().getVarsRegistry().getDescription(data.getName());
                if (!StringUtils.isEmpty(text)) {
                    assert text != null;
                    Locator.getInstance().getClipboard().setText(text);
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
