package org.solovyev.android.calculator.wizard;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.solovyev.android.calculator.CalculatorApplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.AppWizardFlow.newDefaultWizardFlow;
import static org.solovyev.android.calculator.wizard.AppWizardFlow.newFirstTimeWizardFlow;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:23 PM
 */
public final class Wizards {

	public static final String FIRST_TIME_WIZARD = "first-wizard";
	public static final String DEFAULT_WIZARD_FLOW = "app-wizard";
	static final String FLOW = "flow";
	static final String FLOW_FINISHED = "flow_finished";
	static final String STEP = "step";

	private Wizards() {
		throw new AssertionError();
	}

	@Nonnull
	public static WizardFlow getWizardFlow(@Nonnull String name) {
		if(FIRST_TIME_WIZARD.equals(name)) {
			return newFirstTimeWizardFlow();
		} else if(DEFAULT_WIZARD_FLOW.equals(name)) {
			return newDefaultWizardFlow();
		} else {
			throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
		}
	}

	public static boolean isWizardFinished(@Nonnull String name) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());
		return preferences.getBoolean(makeFinishedPreferenceKey(name), false);
	}

	public static boolean isWizardStarted(@Nonnull String name) {
		return getLastSavedWizardStepName(name) != null;
	}

	static void saveLastWizardStep(@Nonnull WizardFlow flow, @Nonnull WizardStep step) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putString(makeLastStepPreferenceKey(flow), step.name());

		editor.commit();
	}

	@Nullable
	static String getLastSavedWizardStepName(@Nonnull String name) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());

		return preferences.getString(makeLastStepPreferenceKey(name), null);
	}

	static void saveWizardFinished(@Nonnull WizardFlow flow, @Nonnull WizardStep step, boolean forceFinish) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(makeFinishedPreferenceKey(flow), forceFinish || flow.getNextStep(step) == null);

		editor.commit();
	}

	@Nonnull
	private static String makeFinishedPreferenceKey(@Nonnull WizardFlow flow) {
		return makeFinishedPreferenceKey(flow.getName());
	}

	@Nonnull
	private static String makeFinishedPreferenceKey(@Nonnull String flowName) {
		return FLOW_FINISHED + ":" + flowName;
	}

	@Nonnull
	private static String makeLastStepPreferenceKey(@Nonnull WizardFlow flow) {
		return makeLastStepPreferenceKey(flow.getName());
	}

	@Nonnull
	private static String makeLastStepPreferenceKey(@Nonnull String flowName) {
		return FLOW + ":" + flowName;
	}
}
