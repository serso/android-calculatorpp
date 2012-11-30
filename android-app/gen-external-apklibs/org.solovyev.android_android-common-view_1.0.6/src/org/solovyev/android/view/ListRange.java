package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.Formatter;

import java.util.List;

/**
 * User: serso
 * Date: 8/10/12
 * Time: 1:55 AM
 */
public class ListRange<T> implements Picker.Range<T> {

    @NotNull
    private List<T> elements;

    private int startPosition;

    @Nullable
    private Formatter<T> formatter;

    public ListRange(@NotNull List<T> elements, @Nullable T selected) {
        this.elements = elements;
        this.startPosition = elements.indexOf(selected);
        if ( this.startPosition < 0 ) {
            this.startPosition = 0;
        }
        this.formatter = null;
    }

    public ListRange(@NotNull List<T> elements, @Nullable T selected, @Nullable Formatter<T> formatter) {
        this(elements, selected);
        this.formatter = formatter;
    }

    @Override
    public int getStartPosition() {
        return this.startPosition;
    }

    @Override
    public int getCount() {
        return this.elements.size();
    }

    @NotNull
    @Override
    public String getStringValueAt(int position) {
        final T value = getValueAt(position);
        return formatter == null ? value.toString() : formatter.formatValue(value);
    }

    @NotNull
    @Override
    public T getValueAt(int position) {
        return this.elements.get(position);
    }
}
