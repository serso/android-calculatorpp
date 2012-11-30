package org.solovyev.android.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 4/19/12
 * Time: 11:50 PM
 */
public class TextViewBuilder implements UpdatableViewBuilder<TextView> {

    private int textViewLayoutId;

    @Nullable
    private String tag;

    private TextViewBuilder() {
    }

    @NotNull
    public static UpdatableViewBuilder<TextView> newInstance(int textViewLayoutId, @Nullable String tag) {
        final TextViewBuilder result = new TextViewBuilder();

        result.textViewLayoutId = textViewLayoutId;
        result.tag = tag;

        return result;
    }

    @NotNull
    @Override
    public TextView build(@NotNull Context context) {
        final TextView result = ViewFromLayoutBuilder.<TextView>newInstance(textViewLayoutId).build(context);

        result.setTag(createViewTag());

        return updateView(context, result);
    }

    @NotNull
    private String createViewTag() {
        return tag == null ? this.getClass().getName() : tag;
    }

    @NotNull
    @Override
    public TextView updateView(@NotNull Context context, @NotNull View view) {
        if (createViewTag().equals(view.getTag())) {
            return (TextView) view;
        } else {
            return build(context);
        }
    }
}
