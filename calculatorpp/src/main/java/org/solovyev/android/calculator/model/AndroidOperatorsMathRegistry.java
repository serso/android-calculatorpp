/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.app.Application;
import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Comparison;
import jscl.math.function.Trigonometric;
import jscl.math.operator.*;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.common.JBuilder;
import org.solovyev.common.collections.CollectionsUtils;
import org.solovyev.common.math.MathRegistry;

import java.util.*;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 11:29 PM
 */
public class AndroidOperatorsMathRegistry extends AbstractAndroidMathRegistry<Operator, MathPersistenceEntity> {

	@NotNull
	private static final Map<String, String> substitutes = new HashMap<String, String>();
	static {
		substitutes.put("Σ", "sum");
		substitutes.put("∏", "product");
		substitutes.put("∂", "derivative");
		substitutes.put("∫ab", "integral_ab");
		substitutes.put("∫", "integral");
		substitutes.put("Σ", "sum");
	}

	@NotNull
	private static final String OPERATOR_DESCRIPTION_PREFIX = "c_op_description_";

	protected AndroidOperatorsMathRegistry(@NotNull MathRegistry<Operator> functionsRegistry,
                                           @NotNull Application application) {
		super(functionsRegistry, OPERATOR_DESCRIPTION_PREFIX, application);
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

    @Override
    public String getCategory(@NotNull Operator operator) {
        for (Category category : Category.values()) {
            if ( category.isInCategory(operator) ) {
                return category.name();
            }
        }
        return null;
    }

	@Override
	public void load() {
		// not supported yet
	}

	@NotNull
	@Override
	protected JBuilder<? extends Operator> createBuilder(@NotNull MathPersistenceEntity entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	protected Class<? extends MathEntityPersistenceContainer<MathPersistenceEntity>> getPersistenceContainerClass() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected Integer getPreferenceStringId() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void save() {
		// not supported yet
	}

	@Override
	protected MathPersistenceEntity transform(@NotNull Operator entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	protected MathEntityPersistenceContainer<MathPersistenceEntity> createPersistenceContainer() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    public static enum Category {

        derivatives(R.string.derivatives, 100){
            @Override
            boolean isInCategory(@NotNull Operator operator) {
                return operator instanceof Derivative || operator instanceof Integral || operator instanceof IndefiniteIntegral;
            }
        },

        other(R.string.other, 200) {
            @Override
            boolean isInCategory(@NotNull Operator operator) {
                return operator instanceof Sum || operator instanceof Product;
            }
        },

        my(R.string.c_fun_category_my, 0) {
            @Override
            boolean isInCategory(@NotNull Operator operator) {
                return !operator.isSystem();
            }
        },

        common(R.string.c_fun_category_common, 50) {
            @Override
            boolean isInCategory(@NotNull Operator operator) {
                for (Category category : values()) {
                    if ( category != this ) {
                        if ( category.isInCategory(operator) ) {
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

        abstract boolean isInCategory(@NotNull Operator operator);

        @NotNull
        public static List<Category> getCategoriesByTabOrder() {
            final List<Category> result = CollectionsUtils.asList(Category.values());

            Collections.sort(result, new Comparator<Category>() {
                @Override
                public int compare(Category category, Category category1) {
                    return category.tabOrder - category1.tabOrder;
                }
            });

            // todo serso: current solution (as creating operators is not implemented yet)
            result.remove(my);

            return result;
        }
    }
}
