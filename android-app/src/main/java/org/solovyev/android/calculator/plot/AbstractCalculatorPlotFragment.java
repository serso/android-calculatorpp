package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.solovyev.android.Android;
import org.solovyev.android.Threads;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.IdentifiableMenuItem;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;
import org.solovyev.common.JPredicate;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences;

/**
 * User: serso
 * Date: 12/30/12
 * Time: 3:09 PM
 */
public abstract class AbstractCalculatorPlotFragment extends CalculatorFragment implements CalculatorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

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

	@Nonnull
	private PlotData plotData = new PlotData(Collections.<PlotFunction>emptyList(), false, true, PlotBoundaries.newDefaultInstance());

	@Nonnull
	private ActivityMenu<Menu, MenuItem> fragmentMenu;

	// thread which calculated data for graph view
	@Nonnull
	private final Executor plotExecutor = Executors.newSingleThreadExecutor();

	@Nonnull
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

	private void savePlotBoundaries() {
		final PlotBoundaries plotBoundaries = getPlotBoundaries();
		if (plotBoundaries != null) {
			Locator.getInstance().getPlotter().savePlotBoundaries(plotBoundaries);
		}
	}

	@Nullable
	protected abstract PlotBoundaries getPlotBoundaries();

	@Override
	public void onResume() {
		super.onResume();

		PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);

		plotData = Locator.getInstance().getPlotter().getPlotData();
		updateChart(plotData, getSherlockActivity());
	}

	@Override
	public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(this.getActivity()).unregisterOnSharedPreferenceChangeListener(this);

		savePlotBoundaries();

		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Preferences.angleUnit.getKey().equals(key)) {
			updateChart(this.plotData, getSherlockActivity());
		}
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable final Object data) {
		switch (calculatorEventType) {
			case plot_data_changed:
				final CalculatorEventHolder.Result result = this.lastEventHolder.apply(calculatorEventData);
				if (result.isNewAfter()) {
					assert data != null;
					onNewPlotData((PlotData) data);
				}
				break;
		}

	}

	private void onNewPlotData(@Nonnull final PlotData plotData) {
		this.plotData = plotData;

		final SherlockFragmentActivity activity = getSherlockActivity();
		updateChart(plotData, activity);
	}

	private void updateChart(@Nonnull final PlotData plotData, @Nullable final SherlockFragmentActivity activity) {
		Threads.tryRunOnUiThread(activity, new Runnable() {
			@Override
			public void run() {
				createChart(plotData);

				final View view = getView();
				if (view != null) {
					createGraphicalView(view, plotData);
				}

				assert activity != null;
				activity.invalidateOptionsMenu();
			}
		});
	}

	protected abstract void onError();

	protected abstract void createGraphicalView(@Nonnull View view, @Nonnull PlotData plotData);

	protected abstract void createChart(@Nonnull PlotData plotData);

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

	@Nonnull
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
		menuItems.add(PlotMenu.preferences);
		menuItems.add(PlotMenu.functions);

		final IdentifiableMenuItem<MenuItem> plotRangeMenuItem = new IdentifiableMenuItem<MenuItem>() {
			@Nonnull
			@Override
			public Integer getItemId() {
				return R.id.menu_plot_range;
			}

			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				savePlotBoundaries();

				context.startActivity(new Intent(context, CalculatorPlotRangeActivity.class));
			}
		};
		menuItems.add(plotRangeMenuItem);

		final IdentifiableMenuItem<MenuItem> plot3dMenuItem = new IdentifiableMenuItem<MenuItem>() {
			@Nonnull
			@Override
			public Integer getItemId() {
				return R.id.menu_plot_3d;
			}

			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				savePlotBoundaries();

				Locator.getInstance().getPlotter().setPlot3d(true);
			}
		};
		menuItems.add(plot3dMenuItem);


		final IdentifiableMenuItem<MenuItem> plot2dMenuItem = new IdentifiableMenuItem<MenuItem>() {
			@Nonnull
			@Override
			public Integer getItemId() {
				return R.id.menu_plot_2d;
			}

			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				savePlotBoundaries();

				Locator.getInstance().getPlotter().setPlot3d(false);
			}
		};
		menuItems.add(plot2dMenuItem);

		final IdentifiableMenuItem<MenuItem> fullscreenPlotMenuItem = new IdentifiableMenuItem<MenuItem>() {
			@Nonnull
			@Override
			public Integer getItemId() {
				return R.id.menu_plot_fullscreen;
			}

			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				savePlotBoundaries();

				context.startActivity(new Intent(context, CalculatorPlotActivity.class));
			}
		};
		menuItems.add(fullscreenPlotMenuItem);

		final IdentifiableMenuItem<MenuItem> captureScreenshotMenuItem = new IdentifiableMenuItem<MenuItem>() {
			@Nonnull
			@Override
			public Integer getItemId() {
				return R.id.menu_plot_schreeshot;
			}

			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				captureScreehshot();
			}
		};
		menuItems.add(captureScreenshotMenuItem);

		final boolean plotRangeVisible = !plotData.isPlot3d();
		final boolean plot3dVisible = !plotData.isPlot3d() && is3dPlotSupported();
		final boolean plot2dVisible = plotData.isPlot3d() && Locator.getInstance().getPlotter().is2dPlotPossible();
		final boolean captureScreenshotVisible = isScreenshotSupported();
		final boolean fullscreenVisible = isPaneFragment();
		fragmentMenu = ListActivityMenu.fromResource(R.menu.plot_menu, menuItems, SherlockMenuHelper.getInstance(), new JPredicate<AMenuItem<MenuItem>>() {
			@Override
			public boolean apply(@Nullable AMenuItem<MenuItem> menuItem) {
				if (menuItem == plot3dMenuItem) {
					return !plot3dVisible;
				} else if (menuItem == plot2dMenuItem) {
					return !plot2dVisible;
				} else if (menuItem == captureScreenshotMenuItem) {
					return !captureScreenshotVisible;
				} else if (menuItem == plotRangeMenuItem) {
					return !plotRangeVisible;
				} else if (menuItem == fullscreenPlotMenuItem) {
					return !fullscreenVisible;
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

	@Nonnull
	protected abstract Bitmap getScreehshot();

	private void captureScreehshot() {
		if (isScreenshotSupported()) {
			final Bitmap screenshot = getScreehshot();
			final String screenShotFileName = generateScreenshotFileName();
			final File externalFilesDir = getActivity().getExternalFilesDir(getPicturesDirectory());
			if (externalFilesDir != null) {
				final String path = externalFilesDir.getPath();
				Android.saveBitmap(screenshot, path, screenShotFileName);
				Locator.getInstance().getNotifier().showMessage(R.string.cpp_plot_screenshot_saved, MessageType.info, path + "/" + screenShotFileName);
			} else {
				Locator.getInstance().getNotifier().showMessage(R.string.cpp_plot_unable_to_save_screenshot, MessageType.error);
			}
		}
	}

	private String getPicturesDirectory() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return Environment.DIRECTORY_PICTURES;
		} else {
			return "Pictures";
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

		functions(R.id.menu_plot_functions) {
			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				context.startActivity(new Intent(context, CalculatorPlotFunctionsActivity.class));
			}
		},

		preferences(R.id.menu_plot_settings) {
			@Override
			public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
				context.startActivity(new Intent(context, CalculatorPlotPreferenceActivity.class));
			}
		};

		private final int itemId;

		private PlotMenu(int itemId) {
			this.itemId = itemId;
		}


		@Nonnull
		@Override
		public Integer getItemId() {
			return itemId;
		}
	}

	public static void applyToPaint(@Nonnull PlotLineDef plotLineDef, @Nonnull Paint paint) {
		paint.setColor(plotLineDef.getLineColor());
		paint.setStyle(Paint.Style.STROKE);

		paint.setStrokeWidth(plotLineDef.getLineWidth());

		final AndroidPlotLineStyle androidPlotLineStyle = AndroidPlotLineStyle.valueOf(plotLineDef.getLineStyle());
		if (androidPlotLineStyle != null) {
			androidPlotLineStyle.applyToPaint(paint);
		}
	}

}
