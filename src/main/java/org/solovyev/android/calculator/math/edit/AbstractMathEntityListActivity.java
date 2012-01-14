/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.ads.AdView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.CalculatorModel;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.AndroidMathRegistry;
import org.solovyev.android.view.AMenuBuilder;
import org.solovyev.android.view.AMenuItem;
import org.solovyev.android.view.MenuImpl;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.utils.EqualsTool;
import org.solovyev.common.utils.Filter;
import org.solovyev.common.utils.FilterRule;
import org.solovyev.common.utils.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 9:24 PM
 */
public abstract class AbstractMathEntityListActivity<T extends MathEntity> extends ListActivity {

    public static final String MATH_ENTITY_CATEGORY_EXTRA_STRING = "org.solovyev.android.calculator.CalculatorVarsActivity_math_entity_category";

	protected final static List<Character> acceptableChars = Arrays.asList(StringUtils.toObject("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_".toCharArray()));

    @NotNull
    private MathEntityArrayAdapter<T> adapter;
    
    @Nullable
    private String category;

	@Nullable
	private AdView adView;

	static void createTab(@NotNull Context context,
                          @NotNull TabHost tabHost,
                          @NotNull String tabId,
                          @NotNull String categoryId,
                          int tabCaptionId,
                          @NotNull Class<? extends Activity> activityClass,
                          @Nullable Intent parentIntent) {

        TabHost.TabSpec spec;

        final Intent intent;
        if (parentIntent != null) {
            intent = new Intent(parentIntent);
        } else {
            intent = new Intent();
        }
        intent.setClass(context, activityClass);
        intent.putExtra(MATH_ENTITY_CATEGORY_EXTRA_STRING, categoryId);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec(tabId).setIndicator(context.getString(tabCaptionId)).setContent(intent);

        tabHost.addTab(spec);
    }

    protected int getLayoutId() {
        return R.layout.math_entities;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

		adView = AdsController.getInstance().inflateAd(this);

		final Intent intent = getIntent();
        if ( intent != null ) {
            category = intent.getStringExtra(MATH_ENTITY_CATEGORY_EXTRA_STRING);
        }

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent,
                                    final View view,
                                    final int position,
                                    final long id) {

                CalculatorModel.instance.processDigitButtonAction(((MathEntity) parent.getItemAtPosition(position)).getName(), false);

                AbstractMathEntityListActivity.this.finish();
            }
        });

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final T item = (T) parent.getItemAtPosition(position);

				final List<AMenuItem<T>> menuItems = getMenuItemsOnLongClick(item);

				if (!menuItems.isEmpty()) {
					final AMenuBuilder<AMenuItem<T>, T> menuBuilder = AMenuBuilder.newInstance(AbstractMathEntityListActivity.this, MenuImpl.newInstance(menuItems));
					menuBuilder.create(item).show();
				}

				return true;
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (this.adView != null) {
			this.adView.destroy();
		}
		super.onDestroy();
	}

	@NotNull
	protected abstract List<AMenuItem<T>> getMenuItemsOnLongClick(@NotNull T item);

	@Override
    protected void onResume() {
        super.onResume();

        adapter = new MathEntityArrayAdapter<T>(getDescriptionGetter(), this, R.layout.math_entity, R.id.math_entity_text, getMathEntitiesByCategory());
        setListAdapter(adapter);

        sort();
    }

    @NotNull
    private List<T> getMathEntitiesByCategory() {
        final List<T> result = getMathEntities();

        new Filter<T>(new FilterRule<T>() {
            @Override
            public boolean isFiltered(T t) {
                return !isInCategory(t);
            }
        }).filter(result.iterator());
        
        return result; 
    }

    protected boolean isInCategory(@Nullable T t) {
        return t != null && EqualsTool.areEqual(getMathEntityCategory(t), category);
    }

    @NotNull
    protected MathEntityArrayAdapter<T> getAdapter() {
        return adapter;
    }

    @NotNull
    protected abstract MathEntityDescriptionGetter getDescriptionGetter();

    @NotNull
    protected abstract List<T> getMathEntities();
    
    @Nullable
    abstract String getMathEntityCategory(@NotNull T t);

    protected void sort() {
        AbstractMathEntityListActivity.this.adapter.sort(new Comparator<T>() {
            @Override
            public int compare(T function1, T function2) {
                return function1.getName().compareTo(function2.getName());
            }
        });

        AbstractMathEntityListActivity.this.adapter.notifyDataSetChanged();
    }

	protected static class MathEntityArrayAdapter<T extends MathEntity> extends ArrayAdapter<T> {

        @NotNull
        private final MathEntityDescriptionGetter descriptionGetter;

        private MathEntityArrayAdapter(@NotNull MathEntityDescriptionGetter descriptionGetter,
									   @NotNull Context context,
									   int resource,
									   int textViewResourceId,
									   @NotNull List<T> objects) {

            super(context, resource, textViewResourceId, objects);
            this.descriptionGetter = descriptionGetter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

            final T mathEntity = getItem(position);

            final String mathEntityDescription = descriptionGetter.getDescription(getContext(), mathEntity.getName());
            if (!StringUtils.isEmpty(mathEntityDescription)) {
                TextView description = (TextView) result.findViewById(R.id.math_entity_description);
                if (description == null) {
                    final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final ViewGroup itemView = (ViewGroup) layoutInflater.inflate(R.layout.math_entity, null);
                    description = (TextView) itemView.findViewById(R.id.math_entity_description);
                    itemView.removeView(description);
                    result.addView(description);
                }
                description.setText(mathEntityDescription);
            } else {
                TextView description = (TextView) result.findViewById(R.id.math_entity_description);
                if (description != null) {
                    result.removeView(description);
                }
            }


            return result;
        }
    }

    protected static class MathEntityDescriptionGetterImpl implements MathEntityDescriptionGetter {

        @NotNull
        private final AndroidMathRegistry<?> mathRegistry;

        public MathEntityDescriptionGetterImpl(@NotNull AndroidMathRegistry<?> mathRegistry) {
            this.mathRegistry = mathRegistry;
        }

        @Override
        public String getDescription(@NotNull Context context, @NotNull String mathEntityName) {
            return this.mathRegistry.getDescription(context, mathEntityName);
        }
    }

    protected static interface MathEntityDescriptionGetter {

        @Nullable
        String getDescription(@NotNull Context context, @NotNull String mathEntityName);
    }
}
