package org.solovyev.android.calculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.textfield.TextInputLayout;
import org.solovyev.android.calculator.ga.Ga;

import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public abstract class BaseDialogFragment extends DialogFragment implements View.OnClickListener, DialogInterface.OnClickListener {

    @Inject
    protected SharedPreferences preferences;
    @Inject
    Ga ga;
    @Inject
    Typeface typeface;
    @Nullable
    private Button positiveButton;
    @Nullable
    private Button negativeButton;
    @Nullable
    private Button neutralButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(cast(getActivity().getApplication()).getComponent());
    }

    protected void inject(@NonNull AppComponent component) {
        component.inject(this);
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(preferences);
        final Context context = getActivity();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final AlertDialog.Builder b = new AlertDialog.Builder(context, theme.alertDialogTheme);
        final View view = onCreateDialogView(context, inflater, savedInstanceState);
        if (view != null) {
            final int spacing = context.getResources().getDimensionPixelSize(R.dimen.cpp_dialog_spacing);
            b.setView(view, spacing, spacing, spacing, spacing);
            BaseActivity.fixFonts(view, typeface);
        }
        onPrepareDialog(b);
        final AlertDialog dialog = b.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                positiveButton = getButton(dialog, AlertDialog.BUTTON_POSITIVE);
                negativeButton = getButton(dialog, AlertDialog.BUTTON_NEGATIVE);
                neutralButton = getButton(dialog, AlertDialog.BUTTON_NEUTRAL);
                onShowDialog(dialog, savedInstanceState == null);
            }
        });
        return dialog;
    }

    protected void onShowDialog(@NonNull AlertDialog dialog, boolean firstTime) {
    }

    @Nullable
    private Button getButton(@NonNull AlertDialog dialog, int buttonId) {
        final Button button = dialog.getButton(buttonId);
        if (button != null) {
            button.setOnClickListener(this);
        }
        return button;
    }

    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
    }

    @Nullable
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        return null;
    }

    protected void setError(@NonNull TextInputLayout textInput, @StringRes int error, Object... errorArgs) {
        setError(textInput, getString(error, errorArgs));
    }
    protected void setError(@NonNull TextInputLayout textInput, @NonNull String error) {
        textInput.setError(error);
        textInput.setErrorEnabled(true);
    }

    protected void clearError(@NonNull TextInputLayout textInput) {
        textInput.setErrorEnabled(false);
        textInput.setError(null);
    }

    protected final void showIme(@NonNull View view) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View v) {
        if (v == positiveButton) {
            onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
        } else if (v == negativeButton) {
            onClick(getDialog(), DialogInterface.BUTTON_NEGATIVE);
        } else if (v == neutralButton) {
            onClick(getDialog(), DialogInterface.BUTTON_NEUTRAL);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
            case DialogInterface.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }
}
