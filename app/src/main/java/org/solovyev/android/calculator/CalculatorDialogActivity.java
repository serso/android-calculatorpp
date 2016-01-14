/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.solovyev.android.Android;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:50 PM
 */
public class CalculatorDialogActivity extends ActionBarActivity {

    @Nonnull
    private static final String TAG = CalculatorDialogActivity.class.getSimpleName();

    @Nonnull
    private static final String DIALOG_DATA_EXTRA = "dialog_data";

    public static void showDialog(@Nonnull Context context, @Nonnull DialogData dialogData) {
        final Intent intent = new Intent();
        intent.setClass(context, CalculatorDialogActivity.class);
        intent.putExtra(DIALOG_DATA_EXTRA, ParcelableDialogData.wrap(dialogData));
        Android.addIntentFlags(intent, false, context);
        context.startActivity(intent);
    }

    @Nullable
    private static DialogData readDialogData(@Nullable Intent in) {
        if (in != null) {
            final Parcelable parcelable = in.getParcelableExtra(DIALOG_DATA_EXTRA);
            if (parcelable instanceof DialogData) {
                return (DialogData) parcelable;
            }
        }

        return null;
    }

    @Nullable
    private static DialogData readDialogData(@Nullable Bundle in) {
        if (in != null) {
            final Parcelable parcelable = in.getParcelable(DIALOG_DATA_EXTRA);
            if (parcelable instanceof DialogData) {
                return (DialogData) parcelable;
            }
        }

        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        super.onCreate(in);

        final DialogData dialogData = readDialogData(getIntent());
        if (dialogData == null) {
            this.finish();
        } else {
            setContentView(R.layout.cpp_dialog);

            final String title = dialogData.getTitle();
            if (!Strings.isEmpty(title)) {
                setTitle(title);
            }


            final Bundle args = new Bundle();
            args.putParcelable(DIALOG_DATA_EXTRA, ParcelableDialogData.wrap(dialogData));
            FragmentUtils.createFragment(this, CalculatorDialogFragment.class, R.id.dialog_layout, "dialog", args);
        }
    }

    public static class CalculatorDialogFragment extends CalculatorFragment {

        public CalculatorDialogFragment() {
            super(CalculatorFragmentType.dialog);
        }

        @Override
        public void onViewCreated(@Nonnull View root, Bundle savedInstanceState) {
            super.onViewCreated(root, savedInstanceState);

            final DialogData dialogData = readDialogData(getArguments());

            if (dialogData != null) {
                final TextView messageTextView = (TextView) root.findViewById(R.id.cpp_dialog_message_textview);
                messageTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
                messageTextView.setText(dialogData.getMessage());

                if (dialogData.getMessageLevel() == MessageType.error || dialogData.getMessageLevel() == MessageType.warning) {
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

