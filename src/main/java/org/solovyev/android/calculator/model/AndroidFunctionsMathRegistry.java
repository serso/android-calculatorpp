/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Comparison;
import jscl.math.function.Function;
import jscl.math.function.Trigonometric;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.common.math.MathRegistry;
import org.solovyev.common.utils.CollectionsUtils;

import java.util.*;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 11:28 PM
 */
public class AndroidFunctionsMathRegistry extends AndroidMathRegistryImpl<jscl.math.function.Function> {

    public static enum Category {

        trigonometric(R.string.c_fun_category_trig, 100){
            @Override
            boolean isInCategory(@NotNull Function function) {
                return (function instanceof Trigonometric || function instanceof ArcTrigonometric) && !hyperbolic_trigonometric.isInCategory(function);
            }
        },
        
        hyperbolic_trigonometric(R.string.c_fun_category_hyper_trig, 300) {

            private final List<String> names = Arrays.asList("sinh", "cosh", "tanh", "coth", "asinh", "acosh", "atanh", "acoth");
            
            @Override
            boolean isInCategory(@NotNull Function function) {
                return names.contains(function.getName());
            }
        },
        
        comparison(R.string.c_fun_category_comparison, 200) {
            @Override
            boolean isInCategory(@NotNull Function function) {
                return function instanceof Comparison;
            }
        },
        
        common(R.string.c_fun_category_common, 0) {
            @Override
            boolean isInCategory(@NotNull Function function) {
                for (Category category : values()) {
                    if ( category != this ) {
                        if ( category.isInCategory(function) ) {
                            return false;
                        }
                    }
                }
                
                return true;
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
        
        abstract boolean isInCategory(@NotNull Function function);
        
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

	@NotNull
	private static final Map<String, String> substitutes = new HashMap<String, String>();
	static {
		substitutes.put("√", "sqrt");
	}

	@NotNull
	private static final String FUNCTION_DESCRIPTION_PREFIX = "c_fun_description_";

	public AndroidFunctionsMathRegistry(@NotNull MathRegistry<jscl.math.function.Function> functionsRegistry) {
		super(functionsRegistry, FUNCTION_DESCRIPTION_PREFIX);
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

    @Override
    public String getCategory(@NotNull Function function) {
        for (Category category : Category.values()) {
            if ( category.isInCategory(function) ) {
                return category.name();
            }
        }
        
        return null;
    }
}
