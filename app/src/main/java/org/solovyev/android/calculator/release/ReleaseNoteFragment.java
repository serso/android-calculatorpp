package org.solovyev.android.calculator.release;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.wizard.WizardFragment;

import javax.annotation.Nonnull;

public class ReleaseNoteFragment extends WizardFragment {
    @Nonnull
    public static final String ARG_VERSION = "version";

    private int version;

    @Override
    protected int getViewResId() {
        return R.layout.cpp_release_note_step;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        version = getArguments().getInt(ARG_VERSION, 0);
    }

    @Nonnull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        final TextView title = (TextView) view.findViewById(R.id.release_note_title);
        title.setText(getString(R.string.cpp_new_in_version, ReleaseNotes.getReleaseNoteVersion(version)));
        final TextView message = (TextView) view.findViewById(R.id.release_note_message);
        message.setText(Html.fromHtml(ReleaseNotes.getReleaseNoteDescription(getActivity(), version)));
        return view;
    }
}
