package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.IdentifiableMenuItem;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;
import org.solovyev.common.JPredicate;
import org.solovyev.common.msg.MessageType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: serso
 * Date: 12/30/12
 * Time: 3:09 PM
 */
public abstract class AbstractCalculatorPlotFragment extends CalculatorFragment implements CalculatorEventListener {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    protected static final String TAG = "CalculatorPlotFragment";

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    private int bgColor;

    @NotNull
    private PlotData plotData = new PlotData(Collections.<PlotFunction>emptyList(), false, PlotBoundaries.newDefaultInstance());

	@NotNull
    private ActivityMenu<Menu, MenuItem> fragmentMenu;

    // thread which calculated data for graph view
    @NotNull
    private final Executor plotExecutor = Executors.newSingleThreadExecutor();

    @NotNull
    private final CalculatorEventHolder lastEventHolder = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());


    public AbstractCalculatorPlotFragment() {
        super(CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_plot_fragment, R.string.c_graph, false));
    }


    @Override
    public void onCreate(@Nullable Bundle in) {
        super.onCreate(in);

        if (isPaneFragment()) {
            this.bgColor = getResources().getColor(R.color.cpp_pane_background);
        } else {
            this.bgColor = getResources().getColor(android.R.color.transparent);
        }

		setHasOptionsMenu(true);
    }

    private void saveBoundaries(@NotNull PlotBoundaries boundaries) {
        Locator.getInstance().getPlotter().setPlotBoundaries(boundaries);
    }

    @Nullable
    protected abstract PlotBoundaries getPlotBoundaries();

    @Override
    public void onPause() {
        final PlotBoundaries plotBoundaries = getPlotBoundaries();
        if (plotBoundaries != null) {
            saveBoundaries(plotBoundaries);
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        plotData = Locator.getInstance().getPlotter().getPlotData();
        createChart(plotData);
        createGraphicalView(getView(), plotData);
        getSherlockActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable final Object data) {
        if (calculatorEventType.isOfType(CalculatorEventType.plot_data_changed)) {
            final CalculatorEventHolder.Result result = this.lastEventHolder.apply(calculatorEventData);
            if (result.isNewAfter()) {
                onNewPlotData((PlotData) data);
            }
        }
    }

    private void onNewPlotData(@NotNull final PlotData plotData) {
        this.plotData = plotData;

        final SherlockFragmentActivity activity = getSherlockActivity();
        Threads.tryRunOnUiThread(activity, new Runnable() {
            @Override
            public void run() {
                createChart(plotData);

                final View view = getView();
                if (view != null) {
                    createGraphicalView(view, plotData);
                }

                activity.invalidateOptionsMenu();
            }
        });
    }

    protected abstract void onError();

    protected abstract void createGraphicalView(@NotNull View view, @NotNull PlotData plotData);

    protected abstract void createChart(@NotNull PlotData plotData);

    /*
    **********************************************************************
    *
    *                           GETTERS
    *
    **********************************************************************
    */

    public int getBgColor() {
        return bgColor;
    }

    @NotNull
    public Executor getPlotExecutor() {
        return plotExecutor;
    }

    /*
    **********************************************************************
    *
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final List<IdentifiableMenuItem<MenuItem>> menuItems = new ArrayList<IdentifiableMenuItem<MenuItem>>();
        menuItems.add(PlotMenu.range);
        menuItems.add(PlotMenu.preferences);
        menuItems.add(PlotMenu.functions);

        final IdentifiableMenuItem<MenuItem> plot3dMenuItem = new IdentifiableMenuItem<MenuItem>() {
            @NotNull
            @Override
            public Integer getItemId() {
                return R.id.menu_plot_3d;
            }

            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                Locator.getInstance().getPlotter().setPlot3d(true);
            }
        };
        menuItems.add(plot3dMenuItem);


        final IdentifiableMenuItem<MenuItem> plot2dMenuItem = new IdentifiableMenuItem<MenuItem>() {
            @NotNull
            @Override
            public Integer getItemId() {
                return R.id.menu_plot_2d;
            }

            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
				Locator.getInstance().getPlotter().setPlot3d(false);
            }
        };
        menuItems.add(plot2dMenuItem);

		final IdentifiableMenuItem<MenuItem> captureScreenshotMenuItem = new IdentifiableMenuItem<MenuItem>() {
			@NotNull
			@Override
			public Integer getItemId() {
				return R.id.menu_plot_schreeshot;
			}

			@Override
			public void onClick(@NotNull MenuItem data, @NotNull Context context) {
				captureScreehshot();
			}
		};
		menuItems.add(captureScreenshotMenuItem);

        final boolean plotRangeVisible = !plotData.isPlot3d();
        final boolean plot3dVisible = !plotData.isPlot3d() && is3dPlotSupported();
        final boolean plot2dVisible = plotData.isPlot3d() && Locator.getInstance().getPlotter().is2dPlotPossible();
        final boolean captureScreenshotVisible = isScreenshotSupported();
        fragmentMenu = ListActivityMenu.fromResource(R.menu.plot_menu, menuItems, SherlockMenuHelper.getInstance(), new JPredicate<AMenuItem<MenuItem>>() {
            @Override
            public boolean apply(@Nullable AMenuItem<MenuItem> menuItem) {
                if ( menuItem == plot3dMenuItem ) {
                    return !plot3dVisible;
                } else if ( menuItem == plot2dMenuItem ) {
                    return !plot2dVisible;
                } else if ( menuItem == captureScreenshotMenuItem ) {
					return !captureScreenshotVisible;
				} else if ( menuItem == PlotMenu.range ) {
                    return !plotRangeVisible;
                }

                return false;
            }
        });

        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            fragmentMenu.onCreateOptionsMenu(activity, menu);
        }
    }

	protected abstract boolean isScreenshotSupported();

	@NotNull
	protected abstract Bitmap getScreehshot();

	private void captureScreehshot() {
		if ( isScreenshotSupported() ) {
			final Bitmap screenshot = getScreehshot();
			final String screenShotFileName = generateScreenshotFileName();
			final File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			if (externalFilesDir != null) {
				final String path = externalFilesDir.getPath();
				AndroidUtils2.saveBitmap(screenshot, path, screenShotFileName);
				Locator.getInstance().getNotifier().showMessage(R.string.cpp_plot_screenshot_saved, MessageType.info, path + "/" + screenShotFileName);
			} else {
				Locator.getInstance().getNotifier().showMessage(R.string.cpp_plot_unable_to_save_screenshot, MessageType.error);
			}
		}
	}

	private String generateScreenshotFileName() {
		final Date now = new Date();
		final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss.S").format(now);

		return "cpp-screenshot-" + timestamp + ".png";
	}

	protected abstract boolean is3dPlotSupported();

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            fragmentMenu.onPrepareOptionsMenu(activity, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item) || fragmentMenu.onOptionsItemSelected(this.getActivity(), item);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    private static enum PlotMenu implements IdentifiableMenuItem<MenuItem> {

        range(R.id.menu_plot_range) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                context.startActivity(new Intent(context, CalculatorPlotRangeActivity.class));
            }
        },

        functions(R.id.menu_plot_functions) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                context.startActivity(new Intent(context, CalculatorPlotFunctionsActivity.class));
            }
        },

        preferences(R.id.menu_plot_settings) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                context.startActivity(new Intent(context, CalculatorPlotPreferenceActivity.class));
            }
        };

        private final int itemId;

        private PlotMenu(int itemId) {
            this.itemId = itemId;
        }


        @NotNull
        @Override
        public Integer getItemId() {
            return itemId;
        }
    }

    public static void applyToPaint(@NotNull PlotLineDef plotLineDef, @NotNull Paint paint) {
        paint.setColor(plotLineDef.getLineColor());
        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(plotLineDef.getLineWidth());

        final AndroidPlotLineStyle androidPlotLineStyle = AndroidPlotLineStyle.valueOf(plotLineDef.getLineStyle());
        if (androidPlotLineStyle != null) {
            androidPlotLineStyle.applyToPaint(paint);
        }
    }

}
