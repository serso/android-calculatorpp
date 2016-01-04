package org.solovyev.android.calculator.wizard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import org.solovyev.android.calculator.release.ChooseThemeReleaseNoteStep;
import org.solovyev.android.calculator.release.ReleaseNoteStep;
import org.solovyev.android.calculator.release.ReleaseNotes;
import org.solovyev.android.wizard.BaseWizard;
import org.solovyev.android.wizard.ListWizardFlow;
import org.solovyev.android.wizard.Wizard;
import org.solovyev.android.wizard.WizardFlow;
import org.solovyev.android.wizard.WizardStep;
import org.solovyev.android.wizard.Wizards;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.last;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.welcome;

public class CalculatorWizards implements Wizards {

    public static final String FIRST_TIME_WIZARD = "first-wizard";
    public static final String RELEASE_NOTES = "release-notes";
    public static final String RELEASE_NOTES_VERSION = "version";
    public static final String DEFAULT_WIZARD_FLOW = "app-wizard";

    @Nonnull
    private final Context context;

    public CalculatorWizards(@Nonnull Context context) {
        this.context = context;
    }

    @Nonnull
    static WizardFlow newDefaultWizardFlow() {
        final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
        for (WizardStep wizardStep : CalculatorWizardStep.values()) {
            if (wizardStep != welcome && wizardStep != last && wizardStep.isVisible()) {
                wizardSteps.add(wizardStep);
            }
        }
        return new ListWizardFlow(wizardSteps);
    }

    @Nonnull
    static WizardFlow newReleaseNotesWizardFlow(@Nonnull Context context, @Nullable Bundle arguments) {
        final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
        final int startVersion = arguments != null ? arguments.getInt(RELEASE_NOTES_VERSION, 0) : 0;
        List<Integer> versions = ReleaseNotes.getReleaseNotesVersions(context, startVersion);
        final int size = versions.size();
        if (size > 7) {
            versions = versions.subList(0, 7);
        }

        for (Integer version : versions) {
            switch (version) {
                case ChooseThemeReleaseNoteStep.VERSION_CODE:
                    wizardSteps.add(new ChooseThemeReleaseNoteStep(version));
                    break;
                default:
                    wizardSteps.add(new ReleaseNoteStep(version));
                    break;
            }
        }
        return new ListWizardFlow(wizardSteps);
    }

    @Nonnull
    static WizardFlow newFirstTimeWizardFlow() {
        final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
        for (WizardStep wizardStep : CalculatorWizardStep.values()) {
            if (wizardStep.isVisible()) {
                wizardSteps.add(wizardStep);
            }
        }
        return new ListWizardFlow(wizardSteps);
    }

    @Nonnull
    @Override
    public Class<? extends Activity> getActivityClassName() {
        return WizardActivity.class;
    }

    @Nonnull
    @Override
    public Wizard getWizard(@Nullable String name, @Nullable Bundle arguments) {
        if (name == null) {
            return getWizard(FIRST_TIME_WIZARD, arguments);
        }

        if (FIRST_TIME_WIZARD.equals(name)) {
            return newBaseWizard(FIRST_TIME_WIZARD, newFirstTimeWizardFlow());
        } else if (DEFAULT_WIZARD_FLOW.equals(name)) {
            return newBaseWizard(DEFAULT_WIZARD_FLOW, newDefaultWizardFlow());
        } else if (RELEASE_NOTES.equals(name)) {
            return newBaseWizard(RELEASE_NOTES, newReleaseNotesWizardFlow(context, arguments));
        } else {
            throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
        }
    }

    @Nonnull
    @Override
    public Wizard getWizard(@Nullable String name) throws IllegalArgumentException {
        return getWizard(name, null);
    }

    @Nonnull
    private BaseWizard newBaseWizard(@Nonnull String name, @Nonnull WizardFlow flow) {
        return new BaseWizard(name, context, flow);
    }

}
