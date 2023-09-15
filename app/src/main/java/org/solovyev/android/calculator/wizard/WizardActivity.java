package org.solovyev.android.calculator.wizard;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.viewpagerindicator.PageIndicator;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.wizard.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class WizardActivity extends BaseActivity implements WizardsAware, SharedPreferences.OnSharedPreferenceChangeListener {
    @Nonnull
    private final WizardUi<WizardActivity> wizardUi = new WizardUi<>(this, this, 0);
    @Nonnull
    private final DialogListener dialogListener = new DialogListener();
    @Nonnull
    private ViewPager pager;
    @Nonnull
    private WizardPagerAdapter pagerAdapter;
    @Nullable
    private AlertDialog dialog;

    @Inject
    SharedPreferences preferences;
    @Inject
    Languages languages;
    @Inject
    Wizards wizards;

    public WizardActivity() {
        super(R.layout.cpp_activity_wizard, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wizardUi.onCreate(savedInstanceState);
        final ListWizardFlow flow = (ListWizardFlow) wizardUi.getFlow();

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new WizardPagerAdapter(flow, getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        final PageIndicator titleIndicator = (PageIndicator) findViewById(R.id.pager_indicator);
        titleIndicator.setViewPager(pager);
        final Wizard wizard = wizardUi.getWizard();
        titleIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                final WizardStep step = flow.getStepAt(position);
                wizardUi.setStep(step);
                WizardUi.tryPutStep(getIntent(), step);
                wizard.saveLastStep(step);
            }
        });

        if (savedInstanceState == null) {
            final int position = flow.getPositionFor(wizardUi.getStep());
            pager.setCurrentItem(position);
        }

        if (wizard.getLastSavedStepName() == null) {
            wizard.saveLastStep(wizardUi.getStep());
        }

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            finishWizardAbruptly();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        wizardUi.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wizardUi.onPause();
    }

    @Nonnull
    @Override
    public Wizards getWizards() {
        return wizards;
    }

    public void setWizards(@Nonnull Wizards wizards) {
        this.wizards = wizards;
    }

    public void finishWizardAbruptly() {
        final String wizardName = wizardUi.getWizard().getName();
        final boolean confirmed = wizardName.equals(CalculatorWizards.RELEASE_NOTES) || wizardName.equals(CalculatorWizards.DEFAULT_WIZARD_FLOW);
        finishWizardAbruptly(confirmed);
    }

    public void finishWizardAbruptly(boolean confirmed) {
        if (!confirmed) {
            if (dialog != null) {
                return;
            }

            final AlertDialog.Builder b = new AlertDialog.Builder(this, App.getTheme().alertDialogTheme);
            b.setTitle(R.string.cpp_wizard_finish_confirmation_title).
                    setMessage(R.string.cpp_wizard_finish_confirmation).
                    setNegativeButton(R.string.cpp_no, dialogListener).
                    setPositiveButton(R.string.cpp_yes, dialogListener).
                    setOnCancelListener(dialogListener);
            dialog = b.create();
            dialog.setOnDismissListener(dialogListener);
            dialog.show();
            return;
        }

        dismissDialog();
        wizardUi.finishWizardAbruptly();
        finish();
    }

    public void finishWizard() {
        wizardUi.finishWizard();
        finish();
    }

    public boolean canGoNext() {
        final int position = pager.getCurrentItem();
        return position != pagerAdapter.getCount() - 1;
    }

    public boolean canGoPrev() {
        final int position = pager.getCurrentItem();
        return position != 0;
    }

    public void goNext() {
        final int position = pager.getCurrentItem();
        if (position < pagerAdapter.getCount() - 1) {
            final WizardFragment fragment = (WizardFragment) pagerAdapter.getItem(position);
            fragment.onNext();
            pager.setCurrentItem(position + 1, true);
        }
    }

    public void goPrev() {
        final int position = pager.getCurrentItem();
        if (position > 0) {
            final WizardFragment fragment = (WizardFragment) pagerAdapter.getItem(position);
            fragment.onPrev();
            pager.setCurrentItem(position - 1, true);
        }
    }

    public WizardFlow getFlow() {
        return wizardUi.getFlow();
    }

    public Wizard getWizard() {
        return wizardUi.getWizard();
    }

    @Override
    protected void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        dismissDialog();
        super.onDestroy();
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private class WizardPagerAdapter extends FragmentStatePagerAdapter {
        @Nonnull
        private final ListWizardFlow flow;

        public WizardPagerAdapter(@Nonnull ListWizardFlow flow, @Nonnull FragmentManager fm) {
            super(fm);
            this.flow = flow;
        }

        @Override
        public Fragment getItem(int position) {
            final WizardStep step = flow.getStepAt(position);
            final String className = step.getFragmentClass().getName();
            final Bundle args = step.getFragmentArgs();
            return Fragment.instantiate(WizardActivity.this, className, args);
        }

        @Override
        public int getCount() {
            return flow.getCount();
        }
    }

    private class DialogListener implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                finishWizardAbruptly(true);
            }
        }

        public void onDismiss(DialogInterface d) {
            dialog = null;
        }

        @Override
        public void onCancel(DialogInterface d) {
            dialog = null;
        }
    }
}
