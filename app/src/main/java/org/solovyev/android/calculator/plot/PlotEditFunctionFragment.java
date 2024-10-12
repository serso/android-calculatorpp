package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.RemovalConfirmationDialog;
import org.solovyev.android.calculator.Utils;
import org.solovyev.android.calculator.databinding.FragmentPlotFunctionEditBinding;
import org.solovyev.android.calculator.functions.BaseFunctionFragment;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.plotter.Color;
import org.solovyev.android.plotter.PlotFunction;
import org.solovyev.android.plotter.PlotIconView;
import org.solovyev.android.plotter.Plotter;
import org.solovyev.android.plotter.meshes.MeshSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import jscl.math.function.CustomFunction;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

public class PlotEditFunctionFragment extends BaseFunctionFragment
    implements SeekBar.OnSeekBarChangeListener {
    @Inject
    Plotter plotter;
    View meshSpecViews;
    TextView colorLabel;
    LineColorPicker colorPicker;
    TextView lineWidthLabel;
    SeekBar lineWidthSeekBar;
    PlotIconView iconView;
    private PlotFunction plotFunction;

    public PlotEditFunctionFragment() {
        super(R.layout.fragment_plot_function_edit);
    }

    public static void show(@Nullable PlotFunction function, @Nonnull FragmentManager fm) {
        App.showDialog(create(function), "plot-function-editor", fm);
    }

    @NonNull
    public static PlotEditFunctionFragment create(@Nullable PlotFunction pf) {
        final PlotEditFunctionFragment fragment = new PlotEditFunctionFragment();
        if (pf != null && pf.function instanceof ExpressionFunction) {
            final Bundle args = new Bundle();
            final ExpressionFunction ef = (ExpressionFunction) pf.function;
            final List<String> parameters = new ArrayList<>(((CustomFunction) ef.function).getParameterNames());
            args.putParcelable(ARG_FUNCTION, CppFunction
                    .builder(ef.function.getName(),
                            ((CustomFunction) ef.function).getContent())
                    .withParameters(parameters)
                    .withId(pf.function.getId())
                    .build());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (function != null) {
            plotFunction = plotter.getPlotData().get(function.getId());
            if (plotFunction == null) {
                dismiss();
            }
        }
    }

    @Override
    protected void inject(@NonNull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @NonNull
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater,
        @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateDialogView(context, inflater, savedInstanceState);
        FragmentPlotFunctionEditBinding binding = FragmentPlotFunctionEditBinding.bind(view);
        meshSpecViews = binding.fnMeshspecViews;
        colorLabel = binding.fnColorLabel;
        colorPicker = binding.fnColorPicker;
        lineWidthLabel = binding.fnLinewidthLabel;
        lineWidthSeekBar = binding.fnLinewidthSeekbar;
        iconView = binding.fnIconview;
        colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int c) {
                iconView.setMeshSpec(applyMeshSpec());
            }
        });
        lineWidthSeekBar.setMax(MeshSpec.MAX_WIDTH - MeshSpec.MIN_WIDTH);
        lineWidthSeekBar.setOnSeekBarChangeListener(this);

        final int[] colors = MeshSpec.LightColors.asIntArray();
        colorPicker.setColors(colors);
        paramsView.setMaxParams(2);
        // no descriptions for functions in plotter
        descriptionLabel.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            if (plotFunction != null) {
                setupViews(plotFunction.meshSpec);
            } else {
                setupViews();
            }
        }
        return view;
    }

    private void setupViews(@NonNull MeshSpec meshSpec) {
        final int color = meshSpec.color.toInt();
        final int[] colors = colorPicker.getColors();
        final int i = indexOf(colors, color);
        colorPicker.setSelectedColorPosition(Math.max(0, i));
        lineWidthSeekBar.setProgress(meshSpec.width - MeshSpec.MIN_WIDTH);
        iconView.setMeshSpec(meshSpec);
    }

    private void setupViews() {
        colorPicker.setSelectedColorPosition(0);
        lineWidthSeekBar.setProgress(MeshSpec.defaultWidth(getActivity()) - MeshSpec.MIN_WIDTH);
        iconView.setMeshSpec(applyMeshSpec());
    }

    private static int indexOf(int[] integers, int integer) {
        for (int i = 0; i < integers.length; i++) {
            if (integers[i] == integer) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    protected MeshSpec applyMeshSpec() {
        final Color color = Color.create(colorPicker.getColor());
        final int width = MeshSpec.MIN_WIDTH + lineWidthSeekBar.getProgress();
        final MeshSpec meshSpec = MeshSpec.create(color, width);
        meshSpec.pointsCount = PlotActivity.POINTS_COUNT;
        return meshSpec;
    }

    protected boolean applyData(@Nonnull CppFunction function) {
        try {
            final ExpressionFunction expressionFunction =
                new ExpressionFunction(function.toJsclBuilder().create());
            final PlotFunction plotFunction = PlotFunction.create(expressionFunction,
                applyMeshSpec());
            final int id = function.getId();
            if (id != CppFunction.NO_ID) {
                plotter.update(id, plotFunction);
            } else {
                plotter.add(plotFunction);
            }
            return true;
        } catch (RuntimeException e) {
            setError(bodyLabel, Utils.getErrorMessage(e));
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        iconView.setMeshSpec(applyMeshSpec());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    protected void showRemovalDialog(@NonNull final CppFunction function) {
        Check.isNotNull(plotFunction);
        final String functionName = plotFunction.function.getName();
        Check.isNotNull(functionName);
        RemovalConfirmationDialog.showForFunction(getActivity(), functionName,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Check.isTrue(which == DialogInterface.BUTTON_POSITIVE);
                        plotter.remove(plotFunction);
                        dismiss();
                    }
                });
    }
}
