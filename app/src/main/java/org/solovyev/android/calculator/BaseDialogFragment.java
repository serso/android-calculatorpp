package org.solovyev.android.calculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

public abstract class BaseDialogFragment extends DialogFragment {

    @Inject
    SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(((CalculatorApplication) getActivity().getApplication()).getComponent());
    }

    protected void inject(@NonNull AppComponent component) {
        component.inject(this);
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(preferences);
        final Context context = getActivity();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = onCreateDialogView(context, inflater, savedInstanceState);
        final int spacing = context.getResources().getDimensionPixelSize(R.dimen.cpp_dialog_spacing);
        final AlertDialog.Builder b = new AlertDialog.Builder(context, theme.alertDialogTheme);
        b.setView(view, spacing, spacing, spacing, spacing);
        onPrepareDialog(b);
        final AlertDialog dialog = b.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                onShowDialog(dialog);
            }
        });
        return dialog;
    }

    protected void onShowDialog(@NonNull AlertDialog dialog) {
    }

    protected abstract void onPrepareDialog(@NonNull AlertDialog.Builder builder);

    @NonNull
    protected abstract View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState);

    protected void setError(@NonNull TextInputLayout textInput, @StringRes int error, Object... errorArgs) {
        setError(textInput, getString(error, errorArgs));
    }
    protected void setError(@NonNull TextInputLayout textInput, @NonNull String error) {
        textInput.setError(error);
        textInput.setErrorEnabled(true);
    }

    protected void clearError(@NonNull TextInputLayout textInput) {
        textInput.setError(null);
        textInput.setErrorEnabled(false);
    }

    protected final void showIme(@NonNull View view) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}
