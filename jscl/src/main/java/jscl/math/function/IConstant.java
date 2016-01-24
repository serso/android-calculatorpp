package jscl.math.function;

import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/10/11
 * Time: 6:01 PM
 */
public interface IConstant extends MathEntity {

    @Nonnull
    Constant getConstant();

    @Nullable
    String getDescription();

    boolean isDefined();

    @Nullable
    String getValue();

    @Nullable
    Double getDoubleValue();

    @Nonnull
    String toJava();
}
