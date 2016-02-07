package org.solovyev.android.calculator;

import dagger.Component;
import org.solovyev.android.calculator.errors.FixableErrorFragment;
import org.solovyev.android.calculator.errors.FixableErrorsActivity;
import org.solovyev.android.calculator.functions.EditFunctionFragment;
import org.solovyev.android.calculator.functions.FunctionsFragment;
import org.solovyev.android.calculator.history.BaseHistoryFragment;
import org.solovyev.android.calculator.history.EditHistoryFragment;
import org.solovyev.android.calculator.keyboard.BaseKeyboardUi;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenView;
import org.solovyev.android.calculator.operators.OperatorsFragment;
import org.solovyev.android.calculator.preferences.PreferencesActivity;
import org.solovyev.android.calculator.preferences.PurchaseDialogActivity;
import org.solovyev.android.calculator.variables.EditVariableFragment;
import org.solovyev.android.calculator.variables.VariablesFragment;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(CalculatorApplication application);
    void inject(EditorFragment fragment);
    void inject(BaseUi ui);
    void inject(FragmentUi ui);
    void inject(CalculatorOnscreenService service);
    void inject(BaseHistoryFragment fragment);
    void inject(BaseDialogFragment fragment);
    void inject(FixableErrorFragment fragment);
    void inject(EditFunctionFragment fragment);
    void inject(EditVariableFragment fragment);
    void inject(EditHistoryFragment fragment);
    void inject(FunctionsFragment fragment);
    void inject(VariablesFragment fragment);
    void inject(OperatorsFragment fragment);
    void inject(CalculatorActivity activity);
    void inject(FixableErrorsActivity activity);
    void inject(CalculatorReceiver receiver);
    void inject(DisplayFragment fragment);
    void inject(KeyboardFragment fragment);
    void inject(PurchaseDialogActivity activity);
    void inject(PreferencesActivity activity);
    void inject(BaseKeyboardUi ui);
    void inject(CalculatorOnscreenView view);
}
