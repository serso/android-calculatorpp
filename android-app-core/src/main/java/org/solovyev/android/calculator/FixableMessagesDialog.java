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
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.core.R;
import org.solovyev.common.msg.Message;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 3:37 PM
 */
public class FixableMessagesDialog extends SherlockActivity {

	private static final String INPUT = "input";

	@Nonnull
	private Input input = new Input(Collections.<FixableMessage>emptyList(), false);

	public FixableMessagesDialog() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_fixable_messages_dialog);

		final Intent intent = getIntent();
		if (intent != null) {
			parseIntent(intent);
		}

		final CheckBox doNotShowCalculationMessagesCheckbox = (CheckBox) findViewById(R.id.cpp_do_not_show_fixable_messages_checkbox);
		if (input.isShowCheckbox()) {
			doNotShowCalculationMessagesCheckbox.setVisibility(View.VISIBLE);
		} else {
			doNotShowCalculationMessagesCheckbox.setVisibility(View.GONE);
		}

		final Button closeButton = (Button) findViewById(R.id.close_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doNotShowCalculationMessagesCheckbox.isChecked()) {
					final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FixableMessagesDialog.this);
					CalculatorPreferences.Calculations.showCalculationMessagesDialog.putPreference(prefs, false);
				}

				FixableMessagesDialog.this.finish();
			}
		});
	}

	private void parseIntent(@Nonnull Intent intent) {
		final Input input = intent.getParcelableExtra(INPUT);
		if (input != null) {
			this.input = input;
			onInputChanged();
		}
	}

	private void onInputChanged() {
		final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.cpp_fixable_messages_container);
		viewGroup.removeAllViews();

		final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final List<FixableMessage> messages = input.getMessages();
		for (final FixableMessage message : messages) {
			final View view = layoutInflater.inflate(R.layout.cpp_fixable_messages_dialog_message, null);

			final TextView calculationMessagesTextView = (TextView) view.findViewById(R.id.cpp_fixable_messages_text_view);
			calculationMessagesTextView.setText(message.getMessage());

			final Button fixButton = (Button) view.findViewById(R.id.cpp_fix_button);
			final FixableError fixableError = message.getFixableError();
			if (fixableError == null) {
				fixButton.setVisibility(View.GONE);
				fixButton.setOnClickListener(null);
			} else {
				fixButton.setVisibility(View.VISIBLE);
				fixButton.setOnClickListener(new FixErrorOnClickListener(messages, message));

				final CharSequence fixCaption = fixableError.getFixCaption();
				if (!Strings.isEmpty(fixCaption)) {
					fixButton.setText(fixCaption);
				}
			}


			viewGroup.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}

	@Override
	protected void onNewIntent(@Nonnull Intent intent) {
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

	public static void showDialogForMessages(@Nonnull List<Message> messages,
											 @Nonnull Context context,
											 boolean showCheckbox) {
		if (!messages.isEmpty()) {
			final Intent intent = new Intent(context, FixableMessagesDialog.class);

			intent.putExtra(INPUT, Input.fromMessages(messages, showCheckbox));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			context.startActivity(intent);
		}
	}

	public static void showDialog(@Nonnull List<FixableMessage> messages,
								  @Nonnull Context context,
								  boolean showCheckbox) {
		if (!messages.isEmpty()) {
			final Intent intent = new Intent(context, FixableMessagesDialog.class);

			intent.putExtra(INPUT, new Input(messages, showCheckbox));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			context.startActivity(intent);
		}
	}

	private static final class Input implements Parcelable {

		public static final Creator<Input> CREATOR = new Creator<Input>() {
			@Override
			public Input createFromParcel(@Nonnull Parcel in) {
				return Input.fromParcel(in);
			}

			@Override
			public Input[] newArray(int size) {
				return new Input[size];
			}
		};

		@Nonnull
		private static Input fromParcel(@Nonnull Parcel in) {
			final List<FixableMessage> messages = new ArrayList<FixableMessage>();
			boolean showCheckbox = in.readInt() == 1;
			in.readTypedList(messages, FixableMessage.CREATOR);
			return new Input(messages, showCheckbox);
		}


		@Nonnull
		private List<FixableMessage> messages = new ArrayList<FixableMessage>();

		private boolean showCheckbox;

		private Input(@Nonnull List<FixableMessage> messages, boolean showCheckbox) {
			this.messages = messages;
			this.showCheckbox = showCheckbox;
		}

		@Nonnull
		public List<FixableMessage> getMessages() {
			return messages;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(@Nonnull Parcel out, int flags) {
			out.writeInt(showCheckbox ? 1 : 0);
			out.writeTypedList(messages);
		}

		@Nonnull
		public static Input fromMessages(@Nonnull List<Message> messages, boolean showCheckbox) {
			final List<FixableMessage> stringMessages = new ArrayList<FixableMessage>(messages.size());
			for (Message message : messages) {
				stringMessages.add(new FixableMessage(message));
			}

			return new Input(stringMessages, showCheckbox);
		}

		public boolean isShowCheckbox() {
			return showCheckbox;
		}
	}

	private class FixErrorOnClickListener implements View.OnClickListener {

		@Nonnull
		private final List<FixableMessage> messages;

		@Nonnull
		private final FixableMessage currentMessage;

		public FixErrorOnClickListener(@Nonnull List<FixableMessage> messages,
									   @Nonnull FixableMessage message) {
			this.messages = messages;
			this.currentMessage = message;
		}

		@Override
		public void onClick(View v) {
			final List<FixableMessage> filteredMessages = new ArrayList<FixableMessage>(messages.size() - 1);
			for (FixableMessage message : messages) {
				if (message.getFixableError() == null) {
					filteredMessages.add(message);
				} else if (message.getFixableError() != currentMessage.getFixableError()) {
					filteredMessages.add(message);
				}
			}

			currentMessage.getFixableError().fix();

			if (!filteredMessages.isEmpty()) {
				FixableMessagesDialog.this.input = new Input(filteredMessages, FixableMessagesDialog.this.input.showCheckbox);
				onInputChanged();
			} else {
				FixableMessagesDialog.this.finish();
			}
		}
	}
}

