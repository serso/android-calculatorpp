package org.solovyev.common.msg;

import javax.annotation.Nonnull;

/**
 * See {@link MessageType} as default implementation of this class
 */
public interface MessageLevel {

    public static final int INFO_LEVEL = 100;
    public static final int WARNING_LEVEL = 500;
    public static final int ERROR_LEVEL = 1000;

    /**
     * Position of current message level in some message level hierarchy.
     * By default, one can use {@link MessageType} implementation which uses next levels:
     * 100         500           1000          level
     * --------|-----------|--------------|------------->
     * Info       Warning         Error
     *
     * @return int message level
     */
    int getMessageLevel();

    /**
     * Some string id for level (might be used in logs)
     *
     * @return string level identifier
     */
    @Nonnull
    String getName();
}
