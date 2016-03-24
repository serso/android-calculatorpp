package org.solovyev.android.calculator.plot;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.BaseFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.android.plotter.Dimensions;
import org.solovyev.android.plotter.PlotData;
import org.solovyev.android.plotter.PlotViewFrame;
import org.solovyev.android.plotter.Plotter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlotActivity extends BaseActivity {

    public static class MyFragment extends BaseFragment implements PlotViewFrame.Listener {

        @Inject
        Plotter plotter;
        @Bind(R.id.plot_view_frame)
        PlotViewFrame plotView;

        public MyFragment() {
            super(R.layout.fragment_plot);
        }

        @Override
        protected void inject(@Nonnull AppComponent component) {
            super.inject(component);
            component.inject(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            final View view = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, view);

            final PlotData pd = plotter.getPlotData();
            pd.axisStyle.backgroundColor = ContextCompat.getColor(getActivity(), R.color.cpp_bg);
            plotter.setAxisStyle(pd.axisStyle);
            plotView.addControlView(R.id.plot_add_function);
            plotView.addControlView(R.id.plot_functions);
            plotView.addControlView(R.id.plot_dimensions);
            plotView.setPlotter(plotter);
            plotView.setListener(this);

            return view;
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
        public boolean onButtonPressed(int id) {
            if (id == R.id.plot_dimensions) {
                final Dimensions dimensions = plotter.getDimensions();
                PlotDimensionsFragment.show(dimensions.graph.makeBounds(), plotter.is3d(),
                    getActivity().getSupportFragmentManager());
                return true;
            } else if (id == R.id.plot_functions) {
                PlotFunctionsFragment.show(getActivity().getSupportFragmentManager());
                return true;
            } else if (id == R.id.plot_add_function) {
                PlotEditFunctionFragment.show(null, getActivity().getSupportFragmentManager());
                return true;
            }
            return false;
        }

        @Override
        public void unableToZoom(boolean in) {
            Toast.makeText(getActivity(), "Can't zoom anymore", Toast.LENGTH_SHORT).show();
        }
    }

    public PlotActivity() {
        super(R.layout.activity_empty, R.string.c_plot);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction t = fm.beginTransaction();
            t.add(R.id.main, new MyFragment(), "plotter");
            t.commit();
        }
    }
}
