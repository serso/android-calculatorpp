package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorPreferences;

import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 11:03 PM
 */
public class AndroidCalculatorPlotter implements CalculatorPlotter, SharedPreferences.OnSharedPreferenceChangeListener {

    @NotNull
    private final CalculatorPlotter plotter;

    public AndroidCalculatorPlotter(@NotNull Context context,
                                    @NotNull CalculatorPlotter plotter) {
        this.plotter = plotter;

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.registerOnSharedPreferenceChangeListener(this);

        onSharedPreferenceChanged(preferences, CalculatorPreferences.Graph.plotImag.getKey());
        onSharedPreferenceChanged(preferences, CalculatorPreferences.Graph.lineColorReal.getKey());
        onSharedPreferenceChanged(preferences, CalculatorPreferences.Graph.lineColorImag.getKey());
    }

    @Override
    @NotNull
    public PlotData getPlotData() {
        return plotter.getPlotData();
    }

    @Override
    public boolean addFunction(@NotNull Generic expression) {
        return plotter.addFunction(expression);
    }

    @Override
    public boolean addFunction(@NotNull PlotFunction plotFunction) {
        return plotter.addFunction(plotFunction);
    }

    @Override
    public boolean addFunction(@NotNull XyFunction xyFunction) {
        return plotter.addFunction(xyFunction);
    }

    @Override
    public boolean addFunction(@NotNull XyFunction xyFunction, @NotNull PlotFunctionLineDef functionLineDef) {
        return plotter.addFunction(xyFunction, functionLineDef);
    }

    @Override
    public boolean updateFunction(@NotNull PlotFunction newFunction) {
        return plotter.updateFunction(newFunction);
    }

    @Override
    public boolean updateFunction(@NotNull XyFunction xyFunction, @NotNull PlotFunctionLineDef functionLineDef) {
        return plotter.updateFunction(xyFunction, functionLineDef);
    }

    @Override
    public boolean removeFunction(@NotNull PlotFunction plotFunction) {
        return plotter.removeFunction(plotFunction);
    }

    @Override
    public boolean removeFunction(@NotNull XyFunction xyFunction) {
        return plotter.removeFunction(xyFunction);
    }

    @Override
    public void pin(@NotNull PlotFunction plotFunction) {
        plotter.pin(plotFunction);
    }

    @Override
    public void unpin(@NotNull PlotFunction plotFunction) {
        plotter.unpin(plotFunction);
    }

    @Override
    public void show(@NotNull PlotFunction plotFunction) {
        plotter.show(plotFunction);
    }

    @Override
    public void hide(@NotNull PlotFunction plotFunction) {
        plotter.hide(plotFunction);
    }

    @Override
    public void clearAllFunctions() {
        plotter.clearAllFunctions();
    }

    @Override
    @NotNull
    public List<PlotFunction> getFunctions() {
        return plotter.getFunctions();
    }

    @Override
    @NotNull
    public List<PlotFunction> getVisibleFunctions() {
        return plotter.getVisibleFunctions();
    }

    @Override
    public void plot() {
        plotter.plot();
    }

    @Override
    public boolean isPlotPossible(@NotNull Generic expression) {
        return plotter.isPlotPossible(expression);
    }

    @Override
    public void setPlot3d(boolean plot3d) {
        plotter.setPlot3d(plot3d);
    }

    @Override
    public void removeAllUnpinned() {
        plotter.removeAllUnpinned();
    }

    @Override
    public void setPlotImag(boolean plotImag) {
        plotter.setPlotImag(plotImag);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (CalculatorPreferences.Graph.plotImag.getKey().equals(key)) {
            setPlotImag(CalculatorPreferences.Graph.plotImag.getPreference(preferences));
        }

        if (CalculatorPreferences.Graph.lineColorReal.getKey().equals(key)) {
            setRealLineColor(CalculatorPreferences.Graph.lineColorReal.getPreference(preferences));
        }

        if (CalculatorPreferences.Graph.lineColorImag.getKey().equals(key)) {
            setImagLineColor(CalculatorPreferences.Graph.lineColorImag.getPreference(preferences));
        }
    }

    public void setImagLineColor(@NotNull GraphLineColor imagLineColor) {
        plotter.setImagLineColor(imagLineColor);
    }

    public void setRealLineColor(@NotNull GraphLineColor realLineColor) {
        plotter.setRealLineColor(realLineColor);
    }
}
