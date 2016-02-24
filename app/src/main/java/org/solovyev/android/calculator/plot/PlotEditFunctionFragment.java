package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.functions.BaseFunctionFragment;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.plotter.Color;
import org.solovyev.android.plotter.PlotFunction;
import org.solovyev.android.plotter.PlotIconView;
import org.solovyev.android.plotter.Plotter;
import org.solovyev.android.plotter.meshes.MeshSpec;

import butterknife.Bind;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PlotEditFunctionFragment extends BaseFunctionFragment
    implements SeekBar.OnSeekBarChangeListener {
    @Inject
    Plotter plotter;
    @Bind(R.id.fn_meshspec_views)
    View meshSpecViews;
    @Bind(R.id.fn_color_label)
    TextView colorLabel;
    @Bind(R.id.fn_color_picker)
    LineColorPicker colorPicker;
    @Bind(R.id.fn_linewidth_label)
    TextView lineWidthLabel;
    @Bind(R.id.fn_linewidth_seekbar)
    SeekBar lineWidthSeekBar;
    @Bind(R.id.fn_iconview)
    PlotIconView iconView;
    private PlotFunction plotFunction;

    public PlotEditFunctionFragment() {
        super(R.layout.fragment_plot_function_edit);
    }

    public static void show(@Nullable PlotFunction function, @Nonnull
    FragmentManager fm) {
        App.showDialog(create(function), "plot-function-editor", fm);
    }

    @NonNull
    public static PlotEditFunctionFragment create(@Nullable PlotFunction pf) {
        final PlotEditFunctionFragment fragment = new PlotEditFunctionFragment();
        if (pf != null && pf.function instanceof ExpressionFunction) {
            final Bundle args = new Bundle();
            final String name =
                pf.function.hasName() ? Strings.nullToEmpty(pf.function.getName()) : "";
            final List<String> parameters = new ArrayList<>();
            final ExpressionFunction ef = (ExpressionFunction) pf.function;
            if (ef.xVariable != null) {
                parameters.add(ef.xVariable.getName());
            }
            if (ef.yVariable != null) {
                parameters.add(ef.yVariable.getName());
            }
            args.putParcelable(ARG_FUNCTION, CppFunction
                .builder(name,
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
        return MeshSpec.create(color, width);
    }

    protected boolean applyData(@Nonnull CppFunction function) {
        try {
            final List<String> parameters = function.getParameters();
            final Constant x = parameters.size() > 0 ? new Constant(parameters.get(0)) : null;
            final Constant y = parameters.size() > 1 ? new Constant(parameters.get(1)) : null;
            final ExpressionFunction expressionFunction =
                new ExpressionFunction(function.toJsclBuilder().create(), x, y, false);
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
            setError(bodyLabel, e.getLocalizedMessage());
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
}
