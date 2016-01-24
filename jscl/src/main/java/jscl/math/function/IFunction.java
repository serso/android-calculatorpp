package jscl.math.function;

import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IFunction extends MathEntity {

    @Nonnull
    String getContent();

    @Nullable
    String getDescription();

    String toJava();

    @Nonnull
    List<String> getParameterNames();
}
