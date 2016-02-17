package org.solovyev.android.calculator.errors;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.PreferredPreferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class FixableErrorFragment extends BaseDialogFragment {

    static final String FRAGMENT_TAG = "fixable-error";
    @Nonnull
    private static final String ARG_ERROR = "error";
    @Inject
    PreferredPreferences preferredPreferences;
    private FixableError error;
    @Nullable
    private FixableErrorsActivity activity;

    @Nonnull
    private static FixableErrorFragment create(@Nonnull FixableError error) {
        final FixableErrorFragment fragment = new FixableErrorFragment();
        final Bundle args = new Bundle(1);
        args.putParcelable(ARG_ERROR, error);
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(@Nonnull FixableError error, @Nonnull FragmentManager fm) {
        App.showDialog(create(error), FRAGMENT_TAG, fm);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FixableErrorsActivity) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        error = getArguments().getParcelable(ARG_ERROR);
        Check.isNotNull(error);
    }

    @Override
    protected void inject(@NonNull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setMessage(error.message);
        builder.setNeutralButton(R.string.cpp_dont_show_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Preferences.Gui.showFixableErrorDialog.putPreference(preferences, false);
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.close, null);
        if (error.error != null) {
            builder.setPositiveButton(R.string.fix, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    error.error.fix(preferredPreferences);
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (activity != null) {
            activity.onDialogClosed();
            activity = null;
        }
    }
}
