package org.solovyev.util.math;

import org.jetbrains.annotations.NotNull;

public class MathUtils {

	public static float getDistance(@NotNull Point2d startPoint,
			@NotNull Point2d endPoint) {
		return getNorm(subtract(endPoint, startPoint));
	}

	public static Point2d subtract(@NotNull Point2d p1, @NotNull Point2d p2) {
		return new Point2d(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}
	
	public static Point2d sum(@NotNull Point2d p1, @NotNull Point2d p2) {
		return new Point2d(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}

	public static float getNorm(@NotNull Point2d point) {
		return (float) Math.pow(
				Math.pow(point.getX(), 2) + Math.pow(point.getY(), 2), 0.5);
	}

	public static float getAngle(@NotNull Point2d startPoint,
			@NotNull Point2d axisEndPoint, @NotNull Point2d endPoint) {
		final Point2d axisVector = subtract(axisEndPoint, startPoint);
		final Point2d vector = subtract(endPoint, startPoint);

		double a_2 = Math.pow(getDistance(vector, axisVector), 2);
		double b = getNorm(vector);
		double b_2 = Math.pow(b, 2);
		double c = getNorm(axisVector);
		double c_2 = Math.pow(c, 2);

		return (float) Math.acos((-a_2 + b_2 + c_2) / (2 * b * c));
	}

}
