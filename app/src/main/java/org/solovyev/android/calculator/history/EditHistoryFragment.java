package org.solovyev.android.calculator.history;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditHistoryFragment extends BaseDialogFragment {

    public static final String ARG_STATE = "state";

    @Bind(R.id.history_edit_expression)
    TextView expressionView;

    @Bind(R.id.history_edit_comment)
    EditText commentView;
    private HistoryState state;

    public static void show(@NonNull HistoryState state, @NonNull FragmentManager fm) {
        final EditHistoryFragment fragment = new EditHistoryFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_STATE, state);
        fragment.setArguments(args);
        fragment.show(fm, "edit-history-fragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = getArguments().getParcelable(ARG_STATE);
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {

    }

    @NonNull
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.history_edit, null);
        ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            expressionView.setText(BaseHistoryFragment.getHistoryText(state));
            commentView.setText(state.getComment());
        }
        return view;
    }
}
