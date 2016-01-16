package org.solovyev.android.calculator.history;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;

import javax.inject.Inject;

public class EditHistoryFragment extends BaseDialogFragment {

    public static final String ARG_STATE = "state";
    public static final String ARG_NEW = "new";

    @Inject
    History history;

    HistoryState state;

    boolean newState;

    @Bind(R.id.history_expression)
    TextView expressionView;

    @Bind(R.id.history_comment)
    EditText commentView;

    @Bind(R.id.history_comment_label)
    TextInputLayout commentLabel;

    public static void show(@NonNull HistoryState state, boolean newState, @NonNull FragmentManager fm) {
        final EditHistoryFragment fragment = new EditHistoryFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_STATE, state);
        args.putBoolean(ARG_NEW, newState);
        fragment.setArguments(args);
        fragment.show(fm, "edit-history-fragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        state = arguments.getParcelable(ARG_STATE);
        newState = arguments.getBoolean(ARG_NEW);
    }

    @Override
    protected void inject(@NonNull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.c_cancel, null);
        builder.setPositiveButton(R.string.c_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final HistoryState.Builder b = HistoryState.builder(state, newState)
                        .withComment(commentView.getText().toString());
                history.updateSaved(b.build());
            }
        });
    }

    @NonNull
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history_edit, null);
        ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            expressionView.setText(BaseHistoryFragment.getHistoryText(state));
            commentView.setText(state.getComment());
        }
        return view;
    }
}
