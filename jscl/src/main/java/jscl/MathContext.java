package jscl;

import org.solovyev.common.math.MathRegistry;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;

public interface MathContext {

    @Nonnull
    MathRegistry<Function> getFunctionsRegistry();

    @Nonnull
    MathRegistry<Operator> getOperatorsRegistry();

    @Nonnull
    MathRegistry<IConstant> getConstantsRegistry();

    @Nonnull
    MathRegistry<Operator> getPostfixFunctionsRegistry();

    @Nonnull
    AngleUnit getAngleUnits();

    void setAngleUnits(@Nonnull AngleUnit defaultAngleUnits);

    @Nonnull
    NumeralBase getNumeralBase();


    // OUTPUT NUMBER FORMATTING
    // todo serso: maybe gather all formatting data in one object?

    void setNumeralBase(@Nonnull NumeralBase numeralBase);

    void setRoundResult(boolean roundResult);

    void setPrecision(int precision);

    void setGroupingSeparator(char separator);

    @Nonnull
    String format(double value);

    String format(@Nonnull BigInteger value);

    @Nonnull
    String format(double value, @Nonnull NumeralBase nb);

    @Nonnull
    String addGroupingSeparators(@Nonnull NumeralBase nb, @Nonnull String ungroupedIntValue);

    void setNotation(int notation);
}
