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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListFragment;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.ContextMenuBuilder;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.android.menu.ListContextMenu;
import org.solovyev.common.JPredicate;
import org.solovyev.common.Objects;
import org.solovyev.common.filter.Filter;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.text.Strings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 9:24 PM
 */
public abstract class AbstractMathEntityListFragment<T extends MathEntity> extends SherlockListFragment implements CalculatorEventListener {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	public static final String MATH_ENTITY_CATEGORY_EXTRA_STRING = "org.solovyev.android.calculator.CalculatorVarsActivity_math_entity_category";

	protected final static List<Character> acceptableChars = Arrays.asList(Strings.toObjects("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_".toCharArray()));


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

	@Nonnull
	private final CalculatorFragmentHelper fragmentHelper;

	@Nonnull
	private final Handler uiHandler = new Handler();

	protected AbstractMathEntityListFragment(@Nonnull CalculatorFragmentType fragmentType) {
		fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(fragmentType.getDefaultLayoutId(), fragmentType.getDefaultTitleResId());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			category = bundle.getString(MATH_ENTITY_CATEGORY_EXTRA_STRING);
		}

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
				final AMenuItem<T> onClick = getOnClickAction();
				if (onClick != null) {
					onClick.onClick(((T) parent.getItemAtPosition(position)), getActivity());
				}
			}
		});

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final T item = (T) parent.getItemAtPosition(position);

				final List<LabeledMenuItem<T>> menuItems = getMenuItemsOnLongClick(item);

				if (!menuItems.isEmpty()) {
					final ContextMenuBuilder<LabeledMenuItem<T>, T> menuBuilder = ContextMenuBuilder.newInstance(AbstractMathEntityListFragment.this.getActivity(), "math-entity-menu", ListContextMenu.newInstance(menuItems));
					menuBuilder.build(item).show();
				}

				return true;
			}
		});
	}

	@Nullable
	protected abstract AMenuItem<T> getOnClickAction();

	@Override
	public void onDestroy() {
		fragmentHelper.onDestroy(this);

		super.onDestroy();
	}

	@Nonnull
	protected abstract List<LabeledMenuItem<T>> getMenuItemsOnLongClick(@Nonnull T item);

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

	@Nonnull
	private List<T> getMathEntitiesByCategory() {
		final List<T> result = getMathEntities();

		new Filter<T>(new JPredicate<T>() {
			@Override
			public boolean apply(T t) {
				return !isInCategory(t);
			}
		}).filter(result.iterator());

		return result;
	}

	protected boolean isInCategory(@Nullable T t) {
		return t != null && (category == null || Objects.areEqual(getMathEntityCategory(t), category));
	}

	@Nonnull
	protected abstract MathEntityDescriptionGetter getDescriptionGetter();

	@Nonnull
	protected abstract List<T> getMathEntities();

	@Nullable
	abstract String getMathEntityCategory(@Nonnull T t);

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

		@Nonnull
		private final MathEntityDescriptionGetter descriptionGetter;

		private MathEntityArrayAdapter(@Nonnull MathEntityDescriptionGetter descriptionGetter,
									   @Nonnull Context context,
									   int resource,
									   int textViewResourceId,
									   @Nonnull List<T> objects) {

			super(context, resource, textViewResourceId, objects);
			this.descriptionGetter = descriptionGetter;
		}

		@Override
		public View getView(int position, @Nullable View convertView, ViewGroup parent) {
			final ViewGroup result;

			if (convertView == null) {
				result = (ViewGroup) super.getView(position, convertView, parent);
				fillView(position, result);
			} else {
				result = (ViewGroup) convertView;
				fillView(position, result);
			}


			return result;
		}

		private void fillView(int position, @Nonnull ViewGroup result) {
			final T mathEntity = getItem(position);

			final TextView text = (TextView) result.findViewById(R.id.math_entity_text);
			text.setText(String.valueOf(mathEntity));

			final String mathEntityDescription = descriptionGetter.getDescription(getContext(), mathEntity.getName());

			final TextView description = (TextView) result.findViewById(R.id.math_entity_description);
			if (!Strings.isEmpty(mathEntityDescription)) {
				description.setVisibility(View.VISIBLE);
				description.setText(mathEntityDescription);
			} else {
				description.setVisibility(View.GONE);
			}
		}
	}

	protected static class MathEntityDescriptionGetterImpl implements MathEntityDescriptionGetter {

		@Nonnull
		private final CalculatorMathRegistry<?> mathRegistry;

		public MathEntityDescriptionGetterImpl(@Nonnull CalculatorMathRegistry<?> mathRegistry) {
			this.mathRegistry = mathRegistry;
		}

		@Override
		public String getDescription(@Nonnull Context context, @Nonnull String mathEntityName) {
			return this.mathRegistry.getDescription(mathEntityName);
		}
	}

	protected static interface MathEntityDescriptionGetter {

		@Nullable
		String getDescription(@Nonnull Context context, @Nonnull String mathEntityName);
	}

	public void addToAdapter(@Nonnull T mathEntity) {
		if (this.adapter != null) {
			this.adapter.add(mathEntity);
		}
	}

	public void removeFromAdapter(@Nonnull T mathEntity) {
		if (this.adapter != null) {
			this.adapter.remove(mathEntity);
		}
	}

	public void notifyAdapter() {
		if (this.adapter != null) {
			this.adapter.notifyDataSetChanged();
		}
	}

	@Nullable
	protected MathEntityArrayAdapter<T> getAdapter() {
		return adapter;
	}

	@Nonnull
	protected Handler getUiHandler() {
		return uiHandler;
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	static void createTab(@Nonnull Context context,
						  @Nonnull TabHost tabHost,
						  @Nonnull String tabId,
						  @Nonnull String categoryId,
						  int tabCaptionId,
						  @Nonnull Class<? extends Activity> activityClass,
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

	@Nonnull
	public static Bundle createBundleFor(@Nonnull String categoryId) {
		final Bundle result = new Bundle(1);
		putCategory(result, categoryId);
		return result;
	}

	static void putCategory(@Nonnull Bundle bundle, @Nonnull String categoryId) {
		bundle.putString(MATH_ENTITY_CATEGORY_EXTRA_STRING, categoryId);
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
	}
}
