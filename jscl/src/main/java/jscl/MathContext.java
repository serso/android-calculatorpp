package jscl;

import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import org.solovyev.common.math.MathRegistry;

import javax.annotation.Nonnull;
import java.text.DecimalFormatSymbols;

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

    void setDecimalGroupSymbols(@Nonnull DecimalFormatSymbols decimalGroupSymbols);

    void setRoundResult(boolean roundResult);

    void setPrecision(int precision);

    void setUseGroupingSeparator(boolean useGroupingSeparator);

    void setGroupingSeparator(char groupingSeparator);

    @Nonnull
    String format(@Nonnull Double value) throws NumeralBaseException;

    @Nonnull
    String format(@Nonnull Double value, @Nonnull NumeralBase nb) throws NumeralBaseException;

    @Nonnull
    String addGroupingSeparators(@Nonnull NumeralBase nb, @Nonnull String ungroupedIntValue);

    void setScienceNotation(boolean scienceNotation);
}
