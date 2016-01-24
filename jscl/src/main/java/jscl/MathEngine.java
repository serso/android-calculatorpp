package jscl;

import jscl.math.Generic;
import jscl.text.ParseException;
import org.solovyev.common.msg.MessageRegistry;

import javax.annotation.Nonnull;

public interface MathEngine extends MathContext {

    @Nonnull
    String evaluate(@Nonnull String expression) throws ParseException;

    @Nonnull
    String simplify(@Nonnull String expression) throws ParseException;

    @Nonnull
    String elementary(@Nonnull String expression) throws ParseException;

    @Nonnull
    Generic evaluateGeneric(@Nonnull String expression) throws ParseException;

    @Nonnull
    Generic simplifyGeneric(@Nonnull String expression) throws ParseException;

    @Nonnull
    Generic elementaryGeneric(@Nonnull String expression) throws ParseException;

    @Nonnull
    String convert(@Nonnull Double value, @Nonnull NumeralBase to);

    @Nonnull
    MessageRegistry getMessageRegistry();

    void setMessageRegistry(@Nonnull MessageRegistry messageRegistry);
}
