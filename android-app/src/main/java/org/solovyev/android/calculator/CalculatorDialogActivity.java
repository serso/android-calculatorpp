package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.StringUtils;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:50 PM
 */
public class CalculatorDialogActivity extends SherlockFragmentActivity {

    @NotNull
    private static final String TAG = CalculatorDialogActivity.class.getSimpleName();

    @NotNull
    private static final String DIALOG_DATA_EXTRA = "dialog_data";

    public static void showDialog(@NotNull Context context, @NotNull DialogData dialogData) {
        final Intent intent = new Intent();
        intent.setClass(context, CalculatorDialogActivity.class);
        intent.putExtra(DIALOG_DATA_EXTRA, ParcelableDialogData.wrap(dialogData));
        AndroidUtils2.addFlags(intent, false, context);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        super.onCreate(in);

        final DialogData dialogData = readDialogData(getIntent());
        if ( dialogData == null ) {
            Locator.getInstance().getLogger().error(TAG, "Dialog data is null!");
            this.finish();
        } else {
            setContentView(R.layout.cpp_dialog);

            final String title = dialogData.getTitle();
            if (!StringUtils.isEmpty(title)) {
                setTitle(title);
            }


            final Bundle args = new Bundle();
            args.putParcelable(DIALOG_DATA_EXTRA, ParcelableDialogData.wrap(dialogData));
            FragmentUtils.createFragment(this, CalculatorDialogFragment.class, R.id.dialog_layout, "dialog", args);
        }
    }

    @Nullable
    private static DialogData readDialogData(@Nullable Intent in) {
        if ( in != null ) {
            final Parcelable parcelable = in.getParcelableExtra(DIALOG_DATA_EXTRA);
            if ( parcelable instanceof DialogData ) {
                return  (DialogData) parcelable;
            }
        }

        return null;
    }

    @Nullable
    private static DialogData readDialogData(@Nullable Bundle in) {
        if ( in != null ) {
            final Parcelable parcelable = in.getParcelable(DIALOG_DATA_EXTRA);
            if ( parcelable instanceof DialogData ) {
                return  (DialogData) parcelable;
            }
        }

        return null;
    }

    public static class CalculatorDialogFragment extends CalculatorFragment {

        public CalculatorDialogFragment() {
            super(CalculatorFragmentType.dialog);
        }

        @Override
        public void onViewCreated(@NotNull View root, Bundle savedInstanceState) {
            super.onViewCreated(root, savedInstanceState);

            final DialogData dialogData = readDialogData(getArguments());

            if (dialogData != null) {
                final TextView messageTextView = (TextView) root.findViewById(R.id.cpp_dialog_message_textview);
                messageTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
                messageTextView.setText(dialogData.getMessage());

                if ( dialogData.getMessageType() == MessageType.error || dialogData.getMessageType() == MessageType.warning ) {
                    final Button copyButton = (Button) root.findViewById(R.id.cpp_copy_button);
                    copyButton.setVisibility(View.VISIBLE);
                    copyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Locator.getInstance().getClipboard().setText(dialogData.getMessage());
                            Locator.getInstance().getNotifier().showMessage(CalculatorMessage.newInfoMessage(CalculatorMessages.text_copied));
                        }
                    });

                }
            }

            root.findViewById(R.id.cpp_ok_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

}

