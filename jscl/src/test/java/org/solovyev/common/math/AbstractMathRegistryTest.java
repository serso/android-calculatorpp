package org.solovyev.common.math;

import org.junit.Test;
import org.solovyev.common.JBuilder;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

/**
 * User: serso
 * Date: 6/15/13
 * Time: 9:20 PM
 */
public abstract class AbstractMathRegistryTest<T extends MathEntity> {

    @Test
    public void testAdd() throws Exception {
        final AbstractMathRegistry<T> registry = getRegistry();

        final int oldSize = registry.getEntities().size();

        registry.add(createBuilder("test"));
        registry.add(createBuilder("test"));
        registry.add(createBuilder("test1"));

        assertEquals(2, registry.getEntities().size() - oldSize);
    }

    protected abstract JBuilder<? extends T> createBuilder(@Nonnull String name);

    @Nonnull
    protected abstract AbstractMathRegistry<T> getRegistry();
}
