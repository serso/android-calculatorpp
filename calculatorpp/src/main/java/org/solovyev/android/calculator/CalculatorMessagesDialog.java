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
    private Input input = new Input(Collections.<CalculatorFixableMessage>emptyList());

    public CalculatorMessagesDialog() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calculator_messages_dialog);

        final Intent intent = getIntent();
        if (intent != null) {
            parseIntent(intent);
        }

        final CheckBox doNotShowCalculationMessagesCheckbox = (CheckBox) findViewById(R.id.do_not_show_calculation_messages_checkbox);

        final Button closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
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

        final List<CalculatorFixableMessage> messages = input.getMessages();
        for (final CalculatorFixableMessage message : messages) {
            final View view = layoutInflater.inflate(R.layout.calculator_messages_dialog_message, null);

            final TextView calculationMessagesTextView = (TextView) view.findViewById(R.id.calculation_messages_text_view);
            calculationMessagesTextView.setText(message.getMessage());

            final Button fixButton = (Button) view.findViewById(R.id.fix_button);
            final CalculatorFixableError fixableError = message.getFixableError();
            if (fixableError == null) {
                fixButton.setVisibility(View.GONE);
                fixButton.setOnClickListener(null);
            } else {
                fixButton.setVisibility(View.VISIBLE);
                fixButton.setOnClickListener(new FixErrorOnClickListener(messages, message));
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

    public static void showDialogForMessages(@NotNull List<Message> messages, @NotNull Context context) {
        if (!messages.isEmpty()) {
            final Intent intent = new Intent(context, CalculatorMessagesDialog.class);

            intent.putExtra(INPUT, Input.fromMessages(messages));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    public static void showDialog(@NotNull List<CalculatorFixableMessage> messages, @NotNull Context context) {
        if (!messages.isEmpty()) {
            final Intent intent = new Intent(context, CalculatorMessagesDialog.class);

            intent.putExtra(INPUT, new Input(messages));
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
            final List<CalculatorFixableMessage> messages = new ArrayList<CalculatorFixableMessage>();
            in.readTypedList(messages, CalculatorFixableMessage.CREATOR);
            return new Input(messages);
        }


        @NotNull
        private List<CalculatorFixableMessage> messages = new ArrayList<CalculatorFixableMessage>();

        private Input(@NotNull List<CalculatorFixableMessage> messages) {
            this.messages = messages;
        }

        @NotNull
        public List<CalculatorFixableMessage> getMessages() {
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
            final List<CalculatorFixableMessage> stringMessages = new ArrayList<CalculatorFixableMessage>(messages.size());
            for (Message message : messages) {
                stringMessages.add(new CalculatorFixableMessage(message));
            }

            return new Input(stringMessages);
        }
    }

    private class FixErrorOnClickListener implements View.OnClickListener {

        @NotNull
        private final List<CalculatorFixableMessage> messages;

        @NotNull
        private final CalculatorFixableMessage currentMessage;

        public FixErrorOnClickListener(@NotNull List<CalculatorFixableMessage> messages,
                                       @NotNull CalculatorFixableMessage message) {
            this.messages = messages;
            this.currentMessage = message;
        }

        @Override
        public void onClick(View v) {
            final List<CalculatorFixableMessage> filteredMessages = new ArrayList<CalculatorFixableMessage>(messages.size() - 1);
            for (CalculatorFixableMessage message : messages) {
                if ( message.getFixableError() == null ) {
                    filteredMessages.add(message);
                } else if ( message.getFixableError() != currentMessage.getFixableError() ) {
                    filteredMessages.add(message);
                }
            }

            currentMessage.getFixableError().fix();

            if (!filteredMessages.isEmpty()) {
                CalculatorMessagesDialog.this.input = new Input(filteredMessages);
                onInputChanged();
            } else {
                CalculatorMessagesDialog.this.finish();
            }
        }
    }
}

