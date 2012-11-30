package org.solovyev.android.view;

import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 4/26/12
 * Time: 1:05 PM
 */
public class ListViewBuilder implements ViewBuilder<ListView> {

    @Nullable
    private Integer layoutId;

    @NotNull
    private ListAdapter listAdapter;

    private ListViewBuilder() {
    }

    @NotNull
    public static ViewBuilder<ListView> newInstance(@NotNull ListAdapter listAdapter) {
        final ListViewBuilder result = new ListViewBuilder();

        result.layoutId = null;
        result.listAdapter = listAdapter;

        return result;
    }

    @NotNull
    public static ViewBuilder<ListView> newInstance(int layoutId, @NotNull ListAdapter listAdapter) {
        final ListViewBuilder result = new ListViewBuilder();

        result.layoutId = layoutId;
        result.listAdapter = listAdapter;

        return result;
    }

    @NotNull
    @Override
    public ListView build(@NotNull Context context) {
        final ListView result;
        if (layoutId != null) {
            result = ViewFromLayoutBuilder.<ListView>newInstance(layoutId).build(context);
        } else {
            result = new ListView(context);
        }

        result.setAdapter(listAdapter);

        return result;
    }
}
