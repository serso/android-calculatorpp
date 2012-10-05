/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import org.achartengine.model.XYSeries;
import org.achartengine.util.MathHelper;

import java.util.*;

/**
 * User: serso
 * Date: 12/5/11
 * Time: 8:43 PM
 */

/**
 * BEST SOLUTION IS TO MODIFY LIBRARY CLASS
 * NOTE: this class is a copy of XYSeries with some modifications:
 * 1. Possibility to insert point in th emiddle og the range
 */

public class MyXYSeries extends XYSeries {
	/**
	 * The series title.
	 */
	private String mTitle;
	/**
	 * A list to contain the values for the X axis.
	 */
	private List<Double> mX = new ArrayList<Double>();
	/**
	 * A list to contain the values for the Y axis.
	 */
	private List<Double> mY = new ArrayList<Double>();
	/**
	 * The minimum value for the X axis.
	 */
	private double mMinX = MathHelper.NULL_VALUE;
	/**
	 * The maximum value for the X axis.
	 */
	private double mMaxX = -MathHelper.NULL_VALUE;
	/**
	 * The minimum value for the Y axis.
	 */
	private double mMinY = MathHelper.NULL_VALUE;
	/**
	 * The maximum value for the Y axis.
	 */
	private double mMaxY = -MathHelper.NULL_VALUE;
	/**
	 * The scale number for this series.
	 */
	private int mScaleNumber;

	/**
	 * Builds a new XY series.
	 *
	 * @param title the series title.
	 */
	public MyXYSeries(String title) {
		this(title, 10);
	}

	public MyXYSeries(String title, int initialCapacity) {
		super(title, 0);

		this.mX = new ArrayList<Double>(initialCapacity);
		this.mY = new ArrayList<Double>(initialCapacity);

		mTitle = title;
		mScaleNumber = 0;

		initRange();
	}

	public int getScaleNumber() {
		return mScaleNumber;
	}

	/**
	 * Initializes the range for both axes.
	 */
	private void initRange() {
		mMinX = MathHelper.NULL_VALUE;
		mMaxX = -MathHelper.NULL_VALUE;
		mMinY = MathHelper.NULL_VALUE;
		mMaxY = -MathHelper.NULL_VALUE;
		int length = getItemCount();
		for (int k = 0; k < length; k++) {
			double x = getX(k);
			double y = getY(k);
			updateRange(x, y);
		}
	}

	/**
	 * Updates the range on both axes.
	 *
	 * @param x the new x value
	 * @param y the new y value
	 */
	private void updateRange(double x, double y) {
		mMinX = Math.min(mMinX, x);
		mMaxX = Math.max(mMaxX, x);
		mMinY = Math.min(mMinY, y);
		mMaxY = Math.max(mMaxY, y);
	}

	/**
	 * Returns the series title.
	 *
	 * @return the series title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * Sets the series title.
	 *
	 * @param title the series title
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * Adds a new value to the series.
	 *
	 * @param x the value for the X axis
	 * @param y the value for the Y axis
	 */
	public void add(double x, double y) {
		boolean added = false;
		for (int i = 0; i < mX.size(); i++ ) {
			if ( mX.get(i) > x ) {
				mX.add(i, x);
				mY.add(i, y);
				added = true;
				break;
			}
		}

		if ( !added ) {
			mX.add(x);
			mY.add(y);
		}

		updateRange(x, y);
	}


	public boolean needToAdd(double density, double x) {
		boolean result = true;

		for (Double x1 : mX) {
			if (Math.abs(x - x1) < density) {
				result = false;
				break;
			}
		}

		return result;
	}

	/**
	 * Removes an existing value from the series.
	 *
	 * @param index the index in the series of the value to remove
	 */
	public void remove(int index) {
		double removedX = mX.remove(index);
		double removedY = mY.remove(index);
		if (removedX == mMinX || removedX == mMaxX || removedY == mMinY || removedY == mMaxY) {
			initRange();
		}
	}

	/**
	 * Removes all the existing values from the series.
	 */
	public void clear() {
		mX.clear();
		mY.clear();
		initRange();
	}

	/**
	 * Returns the X axis value at the specified index.
	 *
	 * @param index the index
	 * @return the X value
	 */
	public double getX(int index) {
		return mX.get(index);
	}

	/**
	 * Returns the Y axis value at the specified index.
	 *
	 * @param index the index
	 * @return the Y value
	 */
	public double getY(int index) {
		return mY.get(index);
	}

	/**
	 * Returns the series item count.
	 *
	 * @return the series item count
	 */
	public int getItemCount() {
		return mX == null ? 0 : mX.size();
	}

	/**
	 * Returns the minimum value on the X axis.
	 *
	 * @return the X axis minimum value
	 */
	public double getMinX() {
		return mMinX;
	}

	/**
	 * Returns the minimum value on the Y axis.
	 *
	 * @return the Y axis minimum value
	 */
	public double getMinY() {
		return mMinY;
	}

	/**
	 * Returns the maximum value on the X axis.
	 *
	 * @return the X axis maximum value
	 */
	public double getMaxX() {
		return mMaxX;
	}

	/**
	 * Returns the maximum value on the Y axis.
	 *
	 * @return the Y axis maximum value
	 */
	public double getMaxY() {
		return mMaxY;
	}
}

