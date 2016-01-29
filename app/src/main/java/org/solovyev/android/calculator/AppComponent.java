package org.solovyev.android.calculator;

import org.solovyev.android.calculator.functions.EditFunctionFragment;
import org.solovyev.android.calculator.history.BaseHistoryFragment;
import org.solovyev.android.calculator.history.EditHistoryFragment;
import org.solovyev.android.calculator.math.edit.FunctionsFragment;
import org.solovyev.android.calculator.math.edit.VariablesFragment;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;

import javax.inject.Singleton;

import dagger.Component;

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
    void inject(EditHistoryFragment fragment);
    void inject(FunctionsFragment fragment);
    void inject(VariablesFragment fragment);
}
