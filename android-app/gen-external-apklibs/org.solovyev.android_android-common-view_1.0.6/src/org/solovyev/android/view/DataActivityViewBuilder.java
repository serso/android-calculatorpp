package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 12:01 AM
 */
public interface DataActivityViewBuilder<D> extends ActivityViewBuilder {

    void setData(@NotNull D data);
}
