package org.solovyev.android.calculator;

import dagger.Component;
import org.solovyev.android.calculator.history.BaseHistoryFragment;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(CalculatorApplication application);
    void inject(CalculatorEditorFragment fragment);
    void inject(BaseUi ui);
    void inject(CalculatorOnscreenService service);
    void inject(BaseHistoryFragment fragment);
}
