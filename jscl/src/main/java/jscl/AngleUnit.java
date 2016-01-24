package jscl;

import jscl.math.Generic;
import jscl.math.NumericWrapper;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;

import javax.annotation.Nonnull;

public enum AngleUnit {

    deg {
        @Override
        protected double getCoefficientTo(@Nonnull AngleUnit to) {
            switch (to) {
                case deg:
                    return 1d;
                case rad:
                    return FROM_DEG_TO_RAD;
                case grad:
                    return FROM_DEG_TO_GRAD;
                case turns:
                    return FROM_DEG_TO_TURNS;
                default:
                    throw new UnsupportedOperationException("Conversion from " + this + " to " + to + " is not supported!");
            }
        }
    },

    rad {
        @Override
        protected double getCoefficientTo(@Nonnull AngleUnit to) {
            switch (to) {
                case deg:
                    return FROM_RAD_TO_DEG;
                case rad:
                    return 1d;
                case grad:
                    return FROM_RAD_TO_GRAD;
                case turns:
                    return FROM_RAD_TO_TURNS;
                default:
                    throw new UnsupportedOperationException("Conversion from " + this + " to " + to + " is not supported!");
            }
        }
    },

    grad {
        @Override
        protected double getCoefficientTo(@Nonnull AngleUnit to) {
            switch (to) {
                case deg:
                    return FROM_GRAD_TO_DEG;
                case rad:
                    return FROM_GRAD_TO_RAD;
                case grad:
                    return 1d;
                case turns:
                    return FROM_GRAD_TO_TURNS;
                default:
                    throw new UnsupportedOperationException("Conversion from " + this + " to " + to + " is not supported!");
            }
        }
    },

    turns {
        @Override
        protected double getCoefficientTo(@Nonnull AngleUnit to) {
            switch (to) {
                case deg:
                    return FROM_TURNS_TO_DEG;
                case rad:
                    return FROM_TURNS_TO_RAD;
                case grad:
                    return FROM_TURNS_TO_GRAD;
                case turns:
                    return 1d;
                default:
                    throw new UnsupportedOperationException("Conversion from " + this + " to " + to + " is not supported!");
            }
        }
    };


    private static final double FROM_RAD_TO_DEG = 180d / Math.PI;
    private static final double FROM_RAD_TO_GRAD = 200d / Math.PI;
    private static final double FROM_RAD_TO_TURNS = 0.5d / Math.PI;

    private static final double FROM_DEG_TO_RAD = Math.PI / 180d;
    private static final double FROM_DEG_TO_TURNS = 0.5d / 180d;
    private static final double FROM_DEG_TO_GRAD = 10d / 9d;

    private static final double FROM_GRAD_TO_RAD = Math.PI / 200d;
    private static final double FROM_GRAD_TO_TURNS = 0.5d / 200d;
    private static final double FROM_GRAD_TO_DEG = 9d / 10d;

    private static final double FROM_TURNS_TO_GRAD = 200d / 0.5d;
    private static final double FROM_TURNS_TO_RAD = Math.PI / 0.5d;
    private static final double FROM_TURNS_TO_DEG = 180d / 0.5d;


    public final double transform(@Nonnull AngleUnit to, double value) {
        return value * getCoefficientTo(to);
    }

    protected abstract double getCoefficientTo(@Nonnull AngleUnit to);

    public final Numeric transform(@Nonnull AngleUnit to, @Nonnull Numeric value) {
        return value.multiply(getRealCoefficientTo(to));
    }

    private Real getRealCoefficientTo(@Nonnull AngleUnit to) {
        return Real.valueOf(getCoefficientTo(to));
    }

    public final Generic transform(@Nonnull AngleUnit to, @Nonnull Generic value) {
        return value.multiply(new NumericWrapper(getRealCoefficientTo(to)));
    }
}
