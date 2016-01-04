package org.solovyev.android.calculator.plot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.preferences.PreferencesActivity;
import org.solovyev.android.plotter.PlotView;
import org.solovyev.android.plotter.Plotter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlotterFragment extends CalculatorFragment {

    @Nonnull
    private final Plotter plotter = App.getPlotter();

    private PlotView plotView;

    public PlotterFragment() {
        super(CalculatorFragmentType.plotter);
    }

    @Override
    public void onCreate(@Nullable Bundle in) {
        super.onCreate(in);
        setHasOptionsMenu(true);
    }

    @ColorInt
    private int getBgColor() {
        if (isPaneFragment()) {
            return getBgColor(R.color.cpp_pane_bg_light, R.color.cpp_pane_bg);
        } else {
            return getBgColor(R.color.cpp_main_bg_light, R.color.cpp_main_bg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        plotView = (PlotView) view.findViewById(R.id.plotview);
        plotView.setPlotter(plotter);
        plotView.setBackgroundColor(getBgColor());

        if (savedInstanceState != null) {
            final Parcelable plotviewState = savedInstanceState.getParcelable("plotview");
            if (plotviewState != null) {
                plotView.onRestoreInstanceState(plotviewState);
            }
        }

        final View zoomOutButton = view.findViewById(R.id.zoom_out_button);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotView.zoom(false);
            }
        });
        final View zoom0Button = view.findViewById(R.id.zoom_0_button);
        zoom0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotView.resetCamera();
                plotView.resetZoom();
            }
        });
        final View zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotView.zoom(true);
            }
        });
        final View plotModeButton = view.findViewById(R.id.plot_mode_button);
        plotModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotter.set3d(!plotter.is3d());
            }
        });

        return view;
    }

    @ColorInt
    @SuppressWarnings("deprecation")
    private int getBgColor(int lightColor, int darkColor) {
        return getResources().getColor(App.getTheme().isLight() ? lightColor : darkColor);
    }

    @Override
    public void onSaveInstanceState(@Nonnull Bundle out) {
        super.onSaveInstanceState(out);
        final Parcelable plotViewState = plotView.onSaveInstanceState();
        out.putParcelable("plotview", plotViewState);
    }

    @Override
    public void onPause() {
        plotView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        plotView.onResume();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.plot_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_plot_functions:
                startActivity(new Intent(getActivity(), CalculatorPlotFunctionsActivity.class));
                return true;
            case R.id.menu_plot_settings:
                PreferencesActivity.start(getActivity(), R.xml.preferences_plot, R.string.prefs_graph_screen_title);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
