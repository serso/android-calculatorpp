/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.common.utils.CollectionsUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:31 PM
 */
public interface AndroidVarsRegistry extends AndroidMathRegistry<Var>{

    public static enum Category {

        system(R.string.c_var_system, 100){
            @Override
            boolean isInCategory(@NotNull Var var) {
                return var.isSystem();
            }
        },

        my(R.string.c_var_my, 0) {
            @Override
            boolean isInCategory(@NotNull Var var) {
                return !var.isSystem();
            }
        };

        private final int captionId;

        private final int tabOrder;

        Category(int captionId, int tabOrder) {
            this.captionId = captionId;
            this.tabOrder = tabOrder;
        }

        public int getCaptionId() {
            return captionId;
        }

        abstract boolean isInCategory(@NotNull Var var);

        @NotNull
        public static List<Category> getCategoriesByTabOrder() {
            final List<Category> result = CollectionsUtils.asList(Category.values());

            Collections.sort(result, new Comparator<Category>() {
                @Override
                public int compare(Category category, Category category1) {
                    return category.tabOrder - category1.tabOrder;
                }
            });

            return result;
        }
    }

	void load(@Nullable Context context, @Nullable SharedPreferences preferences);

	void save(@NotNull Context context);
}
