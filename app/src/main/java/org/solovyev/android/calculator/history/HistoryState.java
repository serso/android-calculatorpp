package org.solovyev.android.calculator.history;

import android.support.annotation.Nullable;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;

public class HistoryState {

    @Nonnull
    protected final EditorState editor;
    @Nonnull
    protected final DisplayState display;
    protected long time;
    @Nullable
    protected String comment;

    private HistoryState(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        this.editor = editor;
        this.display = display;
    }

    @Nonnull
    public static Builder newBuilder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        return new Builder(editor, display);
    }

    @Nonnull
    public EditorState getEditor() {
        return editor;
    }

    @Nonnull
    public DisplayState getDisplay() {
        return display;
    }

    public long getTime() {
        return time;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public static final class Builder extends HistoryState {

        private boolean built;

        private Builder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
            super(editor, display);
            setTime(System.currentTimeMillis());
        }

        public void setTime(long time) {
            Check.isTrue(!built);
            this.time = time;
        }

        public void setComment(@Nullable String comment) {
            Check.isTrue(!built);
            this.comment = comment;
        }

        @Nonnull
        public HistoryState build() {
            built = true;
            return this;
        }
    }
}
