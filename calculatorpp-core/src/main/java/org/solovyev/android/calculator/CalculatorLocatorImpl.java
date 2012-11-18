package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.history.CalculatorHistory;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public class CalculatorLocatorImpl implements CalculatorLocator {

    @NotNull
    private CalculatorEngine calculatorEngine;

    @NotNull
    private Calculator calculator;

    @NotNull
    private CalculatorEditor calculatorEditor;

    @NotNull
    private CalculatorDisplay calculatorDisplay;

    @NotNull
    private CalculatorKeyboard calculatorKeyboard;

    @NotNull
    private CalculatorHistory calculatorHistory;

    @NotNull
    private CalculatorNotifier calculatorNotifier = new DummyCalculatorNotifier();

    @NotNull
    private CalculatorLogger calculatorLogger = new SystemOutCalculatorLogger();

    @NotNull
    private CalculatorClipboard calculatorClipboard = new DummyCalculatorClipboard();

    @NotNull
    private static final CalculatorLocator instance = new CalculatorLocatorImpl();

    @NotNull
    private CalculatorPreferenceService calculatorPreferenceService;

    public CalculatorLocatorImpl() {
    }

    @Override
    public void init(@NotNull Calculator calculator,
                     @NotNull CalculatorEngine engine,
                     @NotNull CalculatorClipboard clipboard,
                     @NotNull CalculatorNotifier notifier,
                     @NotNull CalculatorHistory history,
                     @NotNull CalculatorLogger logger,
                     @NotNull CalculatorPreferenceService preferenceService,
                     @NotNull CalculatorKeyboard keyboard) {

        this.calculator = calculator;
        this.calculatorEngine = engine;
        this.calculatorClipboard = clipboard;
        this.calculatorNotifier = notifier;
        this.calculatorHistory = history;
        this.calculatorLogger = logger;
        this.calculatorPreferenceService = preferenceService;

        calculatorEditor = new CalculatorEditorImpl(this.calculator);
        calculatorDisplay = new CalculatorDisplayImpl(this.calculator);
        calculatorKeyboard = keyboard;
    }

    @NotNull
    public static CalculatorLocator getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public CalculatorEngine getEngine() {
        return calculatorEngine;
    }

    @NotNull
    @Override
    public Calculator getCalculator() {
        return this.calculator;
    }

    @Override
    @NotNull
    public CalculatorDisplay getDisplay() {
        return calculatorDisplay;
    }

    @NotNull
    @Override
    public CalculatorEditor getEditor() {
        return calculatorEditor;
    }

    @Override
    @NotNull
    public CalculatorKeyboard getKeyboard() {
        return calculatorKeyboard;
    }

    @Override
    @NotNull
    public CalculatorClipboard getClipboard() {
        return calculatorClipboard;
    }

    @Override
    @NotNull
    public CalculatorNotifier getNotifier() {
        return calculatorNotifier;
    }

    @Override
    @NotNull
    public CalculatorHistory getHistory() {
        return calculatorHistory;
    }

    @Override
    @NotNull
    public CalculatorLogger getLogger() {
        return calculatorLogger;
    }

    @NotNull
    @Override
    public CalculatorPreferenceService getPreferenceService() {
        return this.calculatorPreferenceService;
    }
}
