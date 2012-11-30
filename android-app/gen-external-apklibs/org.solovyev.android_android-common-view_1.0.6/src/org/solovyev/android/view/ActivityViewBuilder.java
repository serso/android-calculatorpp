package org.solovyev.android.view;

import android.app.Activity;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/19/12
 * Time: 4:23 PM
 */
public interface ActivityViewBuilder {

    void buildView(@NotNull Activity activity);
}
