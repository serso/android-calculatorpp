package org.solovyev.android.calculator;

import dagger.Component;
import org.solovyev.android.calculator.functions.EditFunctionFragment;
import org.solovyev.android.calculator.history.BaseHistoryFragment;
import org.solovyev.android.calculator.history.EditHistoryFragment;
import org.solovyev.android.calculator.functions.FunctionsFragment;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;
import org.solovyev.android.calculator.variables.EditVariableFragment;
import org.solovyev.android.calculator.variables.VariablesFragment;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(CalculatorApplication application);
    void inject(CalculatorEditorFragment fragment);
    void inject(BaseUi ui);
    void inject(CalculatorOnscreenService service);
    void inject(BaseHistoryFragment fragment);
    void inject(BaseDialogFragment fragment);
    void inject(EditFunctionFragment fragment);
    void inject(EditVariableFragment fragment);
    void inject(EditHistoryFragment fragment);
    void inject(FunctionsFragment fragment);
    void inject(VariablesFragment fragment);
}
