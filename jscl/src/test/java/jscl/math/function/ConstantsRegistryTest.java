package jscl.math.function;

import org.solovyev.common.JBuilder;
import org.solovyev.common.math.AbstractMathRegistry;
import org.solovyev.common.math.AbstractMathRegistryTest;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 6/15/13
 * Time: 9:23 PM
 */
public class ConstantsRegistryTest extends AbstractMathRegistryTest<IConstant> {

    @Override
    protected JBuilder<? extends IConstant> createBuilder(@Nonnull final String name) {
        return new JBuilder<IConstant>() {
            @Nonnull
            @Override
            public IConstant create() {
                return new ExtendedConstant(new Constant(name), name, name);
            }
        };
    }

    @Nonnull
    @Override
    protected AbstractMathRegistry<IConstant> getRegistry() {
        return new ConstantsRegistry();
    }
}
