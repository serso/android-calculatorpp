package org.solovyev.android.calculator.view;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.view.ViewBuilder;
import org.solovyev.android.view.ViewFromLayoutBuilder;
import org.solovyev.common.units.*;

import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 4:50 PM
 */
public class UnitConverterViewBuilder implements ViewBuilder<View> {

    @NotNull
    private List<? extends UnitType<String>> fromUnitTypes = Collections.emptyList();

    @NotNull
    private List<? extends UnitType<String>> toUnitTypes = Collections.emptyList();

    @Nullable
    private Unit<String> fromValue;

    @NotNull
    private UnitConverter<String> converter = UnitConverter.Dummy.getInstance();

    @Nullable
    private View.OnClickListener okButtonOnClickListener;

    @Nullable
    private CustomButtonData customButtonData;

    public void setFromUnitTypes(@NotNull List<? extends UnitType<String>> fromUnitTypes) {
        this.fromUnitTypes = fromUnitTypes;
    }

    public void setToUnitTypes(@NotNull List<? extends UnitType<String>> toUnitTypes) {
        this.toUnitTypes = toUnitTypes;
    }

    public void setFromValue(@Nullable Unit<String> fromValue) {
        this.fromValue = fromValue;
    }

    public void setConverter(@NotNull UnitConverter<String> converter) {
        this.converter = converter;
    }

    public void setOkButtonOnClickListener(@Nullable View.OnClickListener okButtonOnClickListener) {
        this.okButtonOnClickListener = okButtonOnClickListener;
    }

    public void setCustomButtonData(@Nullable CustomButtonData customButtonData) {
        this.customButtonData = customButtonData;
    }

    @NotNull
    @Override
    public View build(@NotNull final Context context) {
        final View main = ViewFromLayoutBuilder.newInstance(R.layout.cpp_unit_converter).build(context);

        final Spinner fromSpinner = (Spinner) main.findViewById(R.id.unit_types_from);
        final EditText fromEditText = (EditText) main.findViewById(R.id.units_from);
        fromEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                doConversion(main, context, UnitConverterViewBuilder.this.converter);
            }
        });

        fillSpinner(main, context, R.id.unit_types_from, fromUnitTypes);
        fillSpinner(main, context, R.id.unit_types_to, toUnitTypes);

        if (fromValue != null) {
            fromEditText.setText(fromValue.getValue());

            int i = fromUnitTypes.indexOf(fromValue.getUnitType());
            if ( i >= 0 ) {
                fromSpinner.setSelection(i);
            }
        }

        final Button copyButton = (Button) main.findViewById(R.id.unit_converter_copy_button);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText toEditText = (EditText) main.findViewById(R.id.units_to);

                final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
                clipboard.setText(toEditText.getText().toString());
                Toast.makeText(context, context.getText(R.string.c_result_copied), Toast.LENGTH_SHORT).show();
            }
        });

        final Button okButton = (Button) main.findViewById(R.id.unit_converter_ok_button);
        if ( okButtonOnClickListener == null ) {
            ((ViewGroup) okButton.getParent()).removeView(okButton);
        } else {
            okButton.setOnClickListener(this.okButtonOnClickListener);
        }

        final Button customButton = (Button) main.findViewById(R.id.unit_converter_custom_button);
        if ( customButtonData == null ) {
            ((ViewGroup) customButton.getParent()).removeView(customButton);
        } else {
            customButton.setText(customButtonData.text);
            customButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customButtonData.clickListener.onClick(getFromUnit(main), getToUnit(main));
                }
            });
        }



        return main;
    }

    private void fillSpinner(@NotNull final View main,
                             @NotNull final Context context,
                             final int spinnerId,
                             @NotNull List<? extends UnitType<String>> unitTypes) {
        final Spinner spinner = (Spinner) main.findViewById(spinnerId);

        final ArrayAdapter<UnitType<String>> adapter = new ArrayAdapter<UnitType<String>>(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (UnitType<String> fromUnitType : unitTypes) {
            adapter.add(fromUnitType);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                doConversion(main, context, UnitConverterViewBuilder.this.converter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(adapter);
    }

    private static void doConversion(@NotNull View main, @NotNull Context context, @NotNull UnitConverter<String> converter) {
        final EditText fromEditText = (EditText) main.findViewById(R.id.units_from);

        final EditText toEditText = (EditText) main.findViewById(R.id.units_to);

        final String from = fromEditText.getText().toString();
        try {
            toEditText.setText(Conversions.doConversion(converter, from, getFromUnitType(main), getToUnitType(main)));
        } catch (ConversionException e) {
            toEditText.setText(context.getString(R.string.c_error));
        }
    }

    @NotNull
    private static Unit<String> getToUnit(@NotNull View main) {
        final EditText toUnits = (EditText) main.findViewById(R.id.units_to);
        return UnitImpl.newInstance(toUnits.getText().toString(), getToUnitType(main));
    }

    @NotNull
    private static UnitType<String> getToUnitType(@NotNull View main) {
        final Spinner toSpinner = (Spinner) main.findViewById(R.id.unit_types_to);
        return (UnitType<String>) toSpinner.getSelectedItem();
    }

    @NotNull
    private static Unit<String> getFromUnit(@NotNull View main) {
        final EditText fromUnits = (EditText) main.findViewById(R.id.units_from);
        return UnitImpl.newInstance(fromUnits.getText().toString(), getFromUnitType(main));
    }

    @NotNull
    private static UnitType<String> getFromUnitType(@NotNull View main) {
        final Spinner fromSpinner = (Spinner) main.findViewById(R.id.unit_types_from);
        return (UnitType<String>) fromSpinner.getSelectedItem();
    }

    public static class CustomButtonData {

        @NotNull
        private String text;

        @NotNull
        private CustomButtonOnClickListener clickListener;


        public CustomButtonData(@NotNull String text, @NotNull CustomButtonOnClickListener clickListener) {
            this.text = text;
            this.clickListener = clickListener;
        }
    }

    public static interface CustomButtonOnClickListener {
        void onClick(@NotNull Unit<String> fromUnits, @NotNull Unit<String> toUnits);
    }
}
