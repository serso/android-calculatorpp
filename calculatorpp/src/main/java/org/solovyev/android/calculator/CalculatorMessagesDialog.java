package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.msg.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 3:37 PM
 */
public class CalculatorMessagesDialog extends SherlockActivity {

    private static final String INPUT = "input";

    @NotNull
    private Input input = new Input(Collections.<CalculationMessage>emptyList());

    public CalculatorMessagesDialog() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calculation_messages_dialog);

        final Intent intent = getIntent();
        if (intent != null) {
            parseIntent(intent);
        }

        final CheckBox doNotShowCalculationMessagesCheckbox = (CheckBox) findViewById(R.id.do_not_show_calculation_messages_checkbox);

        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doNotShowCalculationMessagesCheckbox.isChecked()) {
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CalculatorMessagesDialog.this);
                    CalculatorPreferences.Calculations.showCalculationMessagesDialog.putPreference(prefs, false);
                }

                CalculatorMessagesDialog.this.finish();
            }
        });
    }

    private void parseIntent(@NotNull Intent intent) {
        final Input input = intent.getParcelableExtra(INPUT);
        if (input != null) {
            this.input = input;
            onInputChanged();
        }
    }

    private void onInputChanged() {
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.calculation_messages_container);
        viewGroup.removeAllViews();

        final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final List<CalculationMessage> messages = input.getMessages();
        for (final CalculationMessage message : messages) {
            final View view = layoutInflater.inflate(R.layout.calculation_messages_dialog_message, null);

            final TextView calculationMessagesTextView = (TextView) view.findViewById(R.id.calculation_messages_text_view);
            calculationMessagesTextView.setText(message.getMessage());

            final Button fixButton = (Button) view.findViewById(R.id.fix_button);
            final CalculatorFixableError fixableError = message.getFixableError();
            if (fixableError == null) {
                fixButton.setVisibility(View.GONE);
                fixButton.setOnClickListener(null);
            } else {
                fixButton.setVisibility(View.VISIBLE);
                fixButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final List<CalculationMessage> filteredMessages = new ArrayList<CalculationMessage>(messages.size() - 1);
                        for (CalculationMessage calculationMessage : messages) {
                            if ( calculationMessage.getFixableError() == null || calculationMessage.getFixableError() != message.getFixableError() ) {
                                filteredMessages.add(message);
                            }
                        }

                        fixableError.fix();

                        if (!filteredMessages.isEmpty()) {
                            CalculatorMessagesDialog.this.input = new Input(filteredMessages);
                            onInputChanged();
                        } else {
                            CalculatorMessagesDialog.this.finish();
                        }
                    }
                });
            }

            viewGroup.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    @Override
    protected void onNewIntent(@NotNull Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    public static void showDialog(@NotNull List<Message> messages, @NotNull Context context) {
        if (!messages.isEmpty()) {
            final Intent intent = new Intent(context, CalculatorMessagesDialog.class);

            intent.putExtra(INPUT, Input.fromMessages(messages));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    private static final class Input implements Parcelable {

        public static final Creator<Input> CREATOR = new Creator<Input>() {
            @Override
            public Input createFromParcel(@NotNull Parcel in) {
                return Input.fromParcel(in);
            }

            @Override
            public Input[] newArray(int size) {
                return new Input[size];
            }
        };

        @NotNull
        private static Input fromParcel(@NotNull Parcel in) {
            final List<CalculationMessage> messages = new ArrayList<CalculationMessage>();
            in.readTypedList(messages, CalculationMessage.CREATOR);
            return new Input(messages);
        }


        @NotNull
        private List<CalculationMessage> messages = new ArrayList<CalculationMessage>();

        private Input(@NotNull List<CalculationMessage> messages) {
            this.messages = messages;
        }

        @NotNull
        public List<CalculationMessage> getMessages() {
            return messages;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NotNull Parcel out, int flags) {
            out.writeTypedList(messages);
        }

        @NotNull
        public static Input fromMessages(@NotNull List<Message> messages) {
            final List<CalculationMessage> stringMessages = new ArrayList<CalculationMessage>(messages.size());
            for (Message message : messages) {
                stringMessages.add(new CalculationMessage(message));
            }

            return new Input(stringMessages);
        }
    }
}

