/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListFragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.AMenuBuilder;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.android.menu.MenuImpl;
import org.solovyev.common.equals.EqualsTool;
import org.solovyev.common.filter.Filter;
import org.solovyev.common.filter.FilterRule;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.text.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 9:24 PM
 */
public abstract class AbstractMathEntityListFragment<T extends MathEntity> extends SherlockListFragment {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    public static final String MATH_ENTITY_CATEGORY_EXTRA_STRING = "org.solovyev.android.calculator.CalculatorVarsActivity_math_entity_category";

	protected final static List<Character> acceptableChars = Arrays.asList(StringUtils.toObject("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_".toCharArray()));


    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @Nullable
    private MathEntityArrayAdapter<T> adapter;
    
    @Nullable
    private String category;

    @NotNull
    private CalculatorFragmentHelper fragmentHelper;


    protected int getLayoutId() {
        return R.layout.math_entities_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        if ( bundle != null ) {
            category = bundle.getString(MATH_ENTITY_CATEGORY_EXTRA_STRING);
        }

        fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(getLayoutId(), getTitleResId());
        fragmentHelper.onCreate(this);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        fragmentHelper.onViewCreated(this, root);

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent,
                                    final View view,
                                    final int position,
                                    final long id) {

                CalculatorLocatorImpl.getInstance().getKeyboard().digitButtonPressed(((MathEntity) parent.getItemAtPosition(position)).getName());
                getActivity().finish();
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final T item = (T) parent.getItemAtPosition(position);

                final List<LabeledMenuItem<T>> menuItems = getMenuItemsOnLongClick(item);

                if (!menuItems.isEmpty()) {
                    final AMenuBuilder<LabeledMenuItem<T>, T> menuBuilder = AMenuBuilder.newInstance(AbstractMathEntityListFragment.this.getActivity(), MenuImpl.newInstance(menuItems));
                    menuBuilder.create(item).show();
                }

                return true;
            }
        });
    }

    protected abstract int getTitleResId();

    @Override
    public void onDestroy() {
        fragmentHelper.onDestroy(this);

        super.onDestroy();
	}

	@NotNull
	protected abstract List<LabeledMenuItem<T>> getMenuItemsOnLongClick(@NotNull T item);

    @Override
    public void onPause() {
        this.fragmentHelper.onPause(this);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.fragmentHelper.onResume(this);

        adapter = new MathEntityArrayAdapter<T>(getDescriptionGetter(), this.getActivity(), R.layout.math_entity, R.id.math_entity_text, getMathEntitiesByCategory());
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
        return t != null && (category == null || EqualsTool.areEqual(getMathEntityCategory(t), category));
    }

    @NotNull
    protected abstract MathEntityDescriptionGetter getDescriptionGetter();

    @NotNull
    protected abstract List<T> getMathEntities();
    
    @Nullable
    abstract String getMathEntityCategory(@NotNull T t);

    protected void sort() {
		final MathEntityArrayAdapter<T> localAdapter = adapter;
		if (localAdapter != null) {
			localAdapter.sort(new Comparator<T>() {
				@Override
				public int compare(T function1, T function2) {
					return function1.getName().compareTo(function2.getName());
				}
			});

			localAdapter.notifyDataSetChanged();
		}
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
                    final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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
        private final CalculatorMathRegistry<?> mathRegistry;

        public MathEntityDescriptionGetterImpl(@NotNull CalculatorMathRegistry<?> mathRegistry) {
            this.mathRegistry = mathRegistry;
        }

        @Override
        public String getDescription(@NotNull Context context, @NotNull String mathEntityName) {
            return this.mathRegistry.getDescription(mathEntityName);
        }
    }

    protected static interface MathEntityDescriptionGetter {

        @Nullable
        String getDescription(@NotNull Context context, @NotNull String mathEntityName);
    }

	public void addToAdapter(@NotNull T mathEntity) {
		if (this.adapter != null) {
			this.adapter.add(mathEntity);
		}
	}

	public void removeFromAdapter(@NotNull T mathEntity) {
		if (this.adapter != null) {
			this.adapter.remove(mathEntity);
		}
	}

	public void notifyAdapter() {
		if (this.adapter != null) {
			this.adapter.notifyDataSetChanged();
		}
	}

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

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

    @NotNull
    public static Bundle createBundleFor(@NotNull String categoryId) {
        final Bundle result = new Bundle(1);
        putCategory(result, categoryId);
        return result;
    }

    static void putCategory(@NotNull Bundle bundle, @NotNull String categoryId) {
        bundle.putString(MATH_ENTITY_CATEGORY_EXTRA_STRING, categoryId);
    }
}
