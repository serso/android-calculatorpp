package jscl;

import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import org.solovyev.common.math.MathRegistry;

import javax.annotation.Nonnull;

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

    void setUseGroupingSeparator(boolean useGroupingSeparator);

    void setGroupingSeparator(char groupingSeparator);

    @Nonnull
    String format(double value) throws NumeralBaseException;

    @Nonnull
    String format(double value, @Nonnull NumeralBase nb) throws NumeralBaseException;

    @Nonnull
    String addGroupingSeparators(@Nonnull NumeralBase nb, @Nonnull String ungroupedIntValue);

    void setScienceNotation(boolean scienceNotation);
}
