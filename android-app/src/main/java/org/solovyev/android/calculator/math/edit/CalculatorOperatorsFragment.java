package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.text.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 1:53 PM
 */

public class CalculatorOperatorsFragment extends AbstractMathEntityListFragment<Operator> {

    public CalculatorOperatorsFragment() {
        super(CalculatorFragmentType.operators);
    }

    @Override
    protected AMenuItem<Operator> getOnClickAction() {
        return LongClickMenuItem.use;
    }

    @NotNull
	@Override
	protected List<LabeledMenuItem<Operator>> getMenuItemsOnLongClick(@NotNull Operator item) {
		final List<LabeledMenuItem<Operator>> result = new ArrayList<LabeledMenuItem<Operator>>(Arrays.asList(LongClickMenuItem.values()));

		if ( StringUtils.isEmpty(OperatorDescriptionGetter.instance.getDescription(this.getActivity(), item.getName())) ) {
			result.remove(LongClickMenuItem.copy_description);
		}

		return result;
	}

	@NotNull
    @Override
    protected MathEntityDescriptionGetter getDescriptionGetter() {
        return OperatorDescriptionGetter.instance;
    }


    @NotNull
    @Override
    protected List<Operator> getMathEntities() {
        final List<Operator> result = new ArrayList<Operator>();

        result.addAll(Locator.getInstance().getEngine().getOperatorsRegistry().getEntities());
        result.addAll(Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getEntities());

        return result;
    }

    @Override
    protected String getMathEntityCategory(@NotNull Operator operator) {
        String result = Locator.getInstance().getEngine().getOperatorsRegistry().getCategory(operator);
        if (result == null) {
            result = Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getCategory(operator);
        }

        return result;
    }

    private static enum OperatorDescriptionGetter implements MathEntityDescriptionGetter {

		instance;

        @Override
        public String getDescription(@NotNull Context context, @NotNull String mathEntityName) {
            String result = Locator.getInstance().getEngine().getOperatorsRegistry().getDescription(mathEntityName);
            if (StringUtils.isEmpty(result)) {
                result = Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getDescription(mathEntityName);
            }

            return result;
        }
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    private static enum LongClickMenuItem implements LabeledMenuItem<Operator> {

        use(R.string.c_use) {
            @Override
            public void onClick(@NotNull Operator data, @NotNull Context context) {
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_operator, data);
            }
        },

        copy_description(R.string.c_copy_description) {
            @Override
            public void onClick(@NotNull Operator data, @NotNull Context context) {
                final String text = OperatorDescriptionGetter.instance.getDescription(context, data.getName());
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

