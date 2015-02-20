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

package org.solovyev.android.calculator.math.edit;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import jscl.math.function.IConstant;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.model.Var;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.graphics.Paint.UNDERLINE_TEXT_FLAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
import static android.widget.LinearLayout.HORIZONTAL;

public class VarEditDialogFragment extends DialogFragment implements CalculatorEventListener {

	private final static String greekAlphabet = "αβγδεζηθικλμνξοπρστυφχψω";
	private final static List<Character> acceptableChars = Arrays.asList(Strings.toObjects(("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_" + greekAlphabet).toCharArray()));

	private Input input;

	public VarEditDialogFragment() {
		input = Input.newInstance();
	}

	@Nonnull
	public static VarEditDialogFragment create(@Nonnull Input input) {
		final VarEditDialogFragment fragment = new VarEditDialogFragment();
		fragment.input = input;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.var_edit, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		Locator.getInstance().getCalculator().addCalculatorEventListener(this);
	}

	@Override
	public void onPause() {
		Locator.getInstance().getCalculator().removeCalculatorEventListener(this);

		super.onPause();
	}

	@Override
	public void onViewCreated(@Nonnull View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		final String errorMsg = this.getString(R.string.c_char_is_not_accepted);

		final EditText editName = (EditText) root.findViewById(R.id.var_edit_name);
		editName.setText(input.getName());
		editName.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				for (int i = 0; i < s.length(); i++) {
					char c = s.charAt(i);
					if (!acceptableChars.contains(Character.toLowerCase(c))) {
						s.delete(i, i + 1);
						Toast.makeText(getActivity(), String.format(errorMsg, c), Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		fillGreekKeyboard(root, editName);

		// show soft keyboard automatically
		editName.requestFocus();
		getDialog().getWindow().setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);

		final EditText editValue = (EditText) root.findViewById(R.id.var_edit_value);
		editValue.setText(input.getValue());

		final EditText editDescription = (EditText) root.findViewById(R.id.var_edit_description);
		editDescription.setText(input.getDescription());

		final Var.Builder varBuilder;
		final IConstant constant = input.getConstant();
		if (constant != null) {
			varBuilder = new Var.Builder(constant);
		} else {
			varBuilder = new Var.Builder();
		}

		root.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		root.findViewById(R.id.save_button).setOnClickListener(new VarEditorSaver<IConstant>(varBuilder, constant, root, Locator.getInstance().getEngine().getVarsRegistry(), this));

		if (constant == null) {
			// CREATE MODE
			getDialog().setTitle(R.string.c_var_create_var);

			root.findViewById(R.id.remove_button).setVisibility(View.GONE);
		} else {
			// EDIT MODE
			getDialog().setTitle(R.string.c_var_edit_var);

			root.findViewById(R.id.remove_button).setOnClickListener(MathEntityRemover.newConstantRemover(constant, null, getActivity(), VarEditDialogFragment.this));
		}
	}

	private void fillGreekKeyboard(View root, final EditText editName) {
		final TextView greekKeyboardToggle = (TextView) root.findViewById(R.id.var_toggle_greek_keyboard);
		final ViewGroup greekKeyboard = (ViewGroup) root.findViewById(R.id.var_greek_keyboard);
		greekKeyboardToggle.setPaintFlags(greekKeyboardToggle.getPaintFlags() | UNDERLINE_TEXT_FLAG);
		greekKeyboardToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (greekKeyboard.getVisibility() == VISIBLE) {
					greekKeyboard.setVisibility(GONE);
					greekKeyboardToggle.setText(R.string.cpp_var_show_greek_keyboard);
				} else {
					greekKeyboard.setVisibility(VISIBLE);
					greekKeyboardToggle.setText(R.string.cpp_var_hide_greek_keyboard);
				}
			}
		});
		LinearLayout keyboardRow = null;
		final View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!(view instanceof Button)) throw new AssertionError();
				editName.append(((Button) view).getText());
			}
		};
		for (int i = 0; i < greekAlphabet.length(); i++) {
			if (i % 5 == 0) {
				keyboardRow = new LinearLayout(getActivity());
				keyboardRow.setOrientation(HORIZONTAL);
				greekKeyboard.addView(keyboardRow, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
			}
			final Button button = new Button(getActivity());
			button.setText(String.valueOf(greekAlphabet.charAt(i)));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				fixCapitalization(button);
			}
			button.setOnClickListener(buttonOnClickListener);
			assert keyboardRow != null;
			keyboardRow.addView(button, new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1F));
		}
		final Button button = new Button(getActivity());
		button.setText("↑");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final boolean upperCase = button.getText().equals("↑");
				Views.processViewsOfType(greekKeyboard, Button.class, new Views.ViewProcessor<Button>() {
					@Override
					public void process(@Nonnull Button key) {
						final String letter = key.getText().toString();
						if (upperCase) {
							key.setText(letter.toUpperCase(Locale.US));
						} else {
							key.setText(letter.toLowerCase(Locale.US));
						}
					}
				});
				if (upperCase) {
					button.setText("↓");
				} else {
					button.setText("↑");
				}
			}
		});
		keyboardRow.addView(button, new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1F));
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void fixCapitalization(Button button) {
		button.setAllCaps(false);
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case constant_removed:
			case constant_added:
			case constant_changed:
				if (calculatorEventData.getSource() == this) {
					dismiss();
				}
				break;

		}
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	public static void showDialog(@Nonnull Input input, @Nonnull FragmentManager fm) {
		App.showDialog(create(input), "constant-editor", fm);
	}

	public static class Input {

		@Nullable
		private IConstant constant;

		@Nullable
		private String name;

		@Nullable
		private String value;

		@Nullable
		private String description;

		private Input() {
		}

		@Nonnull
		public static Input newInstance() {
			return new Input();
		}

		@Nonnull
		public static Input newFromConstant(@Nonnull IConstant constant) {
			final Input result = new Input();
			result.constant = constant;
			return result;
		}

		@Nonnull
		public static Input newFromValue(@Nullable String value) {
			final Input result = new Input();
			result.value = value;
			return result;
		}

		@Nonnull
		public static Input newInstance(@Nullable IConstant constant, @Nullable String name, @Nullable String value, @Nullable String description) {
			final Input result = new Input();
			result.constant = constant;
			result.name = name;
			result.value = value;
			result.description = description;
			return result;
		}

		@Nullable
		public IConstant getConstant() {
			return constant;
		}

		@Nullable
		public String getName() {
			return name == null ? (constant == null ? null : constant.getName()) : name;
		}

		@Nullable
		public String getValue() {
			return value == null ? (constant == null ? null : constant.getValue()) : value;
		}

		@Nullable
		public String getDescription() {
			return description == null ? (constant == null ? null : constant.getDescription()) : description;
		}
	}
}
