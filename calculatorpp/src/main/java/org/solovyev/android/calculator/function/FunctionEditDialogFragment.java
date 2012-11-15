package org.solovyev.android.calculator.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.math.edit.CalculatorFunctionsActivity;
import org.solovyev.android.calculator.math.edit.CalculatorFunctionsFragment;
import org.solovyev.android.calculator.math.edit.MathEntityRemover;
import org.solovyev.android.calculator.model.AFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: serso
 * Date: 11/13/12
 * Time: 11:34 PM
 */
public class FunctionEditDialogFragment extends DialogFragment implements CalculatorEventListener {

	private static final String INPUT = "input";

	@NotNull
    private Input input;

    public FunctionEditDialogFragment() {
        this(Input.newInstance());
    }

    public FunctionEditDialogFragment(@NotNull Input input) {
        this.input = input;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View result = inflater.inflate(R.layout.function_edit, container, false);

		if (savedInstanceState != null) {
			final  Parcelable input = savedInstanceState.getParcelable(INPUT);
			if ( input instanceof Input ) {
				this.input = (Input)input;
			}
		}

		return result;
    }

    @Override
    public void onViewCreated(@NotNull View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final FunctionParamsView paramsView = (FunctionParamsView) root.findViewById(R.id.function_params_layout);

		final AFunction.Builder builder;
		final AFunction function = input.getFunction();
		if (function != null) {
			builder = new AFunction.Builder(function);
		} else {
			builder = new AFunction.Builder();
        }

        final List<String> parameterNames = input.getParameterNames();
        if (parameterNames != null) {
            paramsView.init(parameterNames);
        } else {
            paramsView.init();
        }

        final EditText editName = (EditText) root.findViewById(R.id.function_edit_name);
        // show soft keyboard automatically
        editName.requestFocus();
        editName.setText(input.getName());

        final EditText editDescription = (EditText) root.findViewById(R.id.function_edit_description);
        editDescription.setText(input.getDescription());

        final EditText editContent = (EditText) root.findViewById(R.id.function_edit_value);
        editContent.setText(input.getContent());

		root.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		root.findViewById(R.id.save_button).setOnClickListener(new FunctionEditorSaver(builder, function, root, CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry(), this));

		if ( function == null ) {
			// CREATE MODE
			getDialog().setTitle(R.string.function_create_function);

			root.findViewById(R.id.remove_button).setVisibility(View.GONE);
		} else {
			// EDIT MODE
			getDialog().setTitle(R.string.function_edit_function);

			final Function customFunction = new CustomFunction.Builder(function).create();
			root.findViewById(R.id.remove_button).setOnClickListener(MathEntityRemover.newFunctionRemover(customFunction, null, this.getActivity(), FunctionEditDialogFragment.this));
		}
	}

	@Override
	public void onSaveInstanceState(@NotNull Bundle out) {
		super.onSaveInstanceState(out);

		out.putParcelable(INPUT, FunctionEditorSaver.readInput(input.getFunction(), getView()));
	}

	@Override
    public void onResume() {
        super.onResume();

        CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener(this);
    }

    @Override
    public void onPause() {
        CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener(this);

        super.onPause();
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case function_removed:
			case function_added:
			case function_changed:
				if ( calculatorEventData.getSource() == FunctionEditDialogFragment.this ) {
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

    public static void showDialog(@NotNull Input input, @NotNull Context context) {
        if (context instanceof SherlockFragmentActivity) {
            FunctionEditDialogFragment.showDialog(input, ((SherlockFragmentActivity) context).getSupportFragmentManager());
        } else {
            final Intent intent = new Intent(context, CalculatorFunctionsActivity.class);
            intent.putExtra(CalculatorFunctionsFragment.CREATE_FUNCTION_EXTRA, input);
            context.startActivity(intent);
        }
    }

    public static void showDialog(@NotNull Input input, @NotNull FragmentManager fm) {
        AndroidUtils2.showDialog(new FunctionEditDialogFragment(input), "function-editor", fm);
    }

    public static class Input implements Parcelable {

		public static final Parcelable.Creator<Input> CREATOR = new Creator<Input>() {
			@Override
			public Input createFromParcel(@NotNull Parcel in) {
				return Input.fromParcel(in);
			}

			@Override
			public Input[] newArray(int size) {
				return new Input[size];
			}
		};

		private static final Parcelable.Creator<String> STRING_CREATOR = new Creator<String>() {
			@Override
			public String createFromParcel(@NotNull Parcel in) {
				return in.readString();
			}

			@Override
			public String[] newArray(int size) {
				return new String[size];
			}
		};

		@NotNull
		private static Input fromParcel(@NotNull Parcel in) {
			final Input result = new Input();
			result.name = in.readString();
			result.content = in.readString();
			result.description = in.readString();

			final List<String> parameterNames = new ArrayList<String>();
			in.readTypedList(parameterNames, STRING_CREATOR);
			result.parameterNames = parameterNames;

			result.function = (AFunction) in.readSerializable();

			return result;
		}

		@Nullable
        private AFunction function;

        @Nullable
        private String name;

        @Nullable
        private String content;

        @Nullable
        private String description;

        @Nullable
        private List<String> parameterNames;

        private Input() {
        }

        @NotNull
        public static Input newInstance() {
            return new Input();
        }

        @NotNull
        public static Input newFromFunction(@NotNull IFunction function) {
            final Input result = new Input();
            result.function = AFunction.fromIFunction(function);
            return result;
        }

        @NotNull
        public static Input newInstance(@Nullable IFunction function,
                                        @Nullable String name,
                                        @Nullable String value,
                                        @Nullable String description,
										@NotNull List<String> parameterNames) {

            final Input result = new Input();
			if (function != null) {
				result.function = AFunction.fromIFunction(function);
			}
			result.name = name;
            result.content = value;
            result.description = description;
            result.parameterNames = new ArrayList<String>(parameterNames);

            return result;
        }

        @Nullable
        public AFunction getFunction() {
            return function;
        }

        @Nullable
        public String getName() {
            return name == null ? (function == null ? null : function.getName()) : name;
        }

        @Nullable
        public String getContent() {
            return content == null ? (function == null ? null : function.getContent()) : content;
        }

        @Nullable
        public String getDescription() {
            return description == null ? (function == null ? null : function.getDescription()) : description;
        }

        @Nullable
        public List<String> getParameterNames() {
            return parameterNames == null ? (function == null ? null : function.getParameterNames()) : parameterNames;
        }

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(@NotNull Parcel out, int flags) {
			out.writeString(name);
			out.writeString(content);
			out.writeString(description);
			out.writeList(parameterNames);
			out.writeSerializable(function);
		}

        @NotNull
        public static Input newFromDisplay(@NotNull CalculatorDisplayViewState viewState) {
            final Input result = new Input();

            result.content = viewState.getText();
            final Generic generic = viewState.getResult();
            if ( generic != null ) {
                final Set<Constant> constants = CalculatorUtils.getNotSystemConstants(generic);
                final List<String> parameterNames = new ArrayList<String>(constants.size());
                for (Constant constant : constants) {
                    parameterNames.add(constant.getName());
                }
                result.parameterNames = parameterNames;
            }

            return result;
        }
    }
}
