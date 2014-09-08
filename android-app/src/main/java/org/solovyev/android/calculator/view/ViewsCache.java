package org.solovyev.android.calculator.view;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;

import javax.annotation.Nonnull;

public abstract class ViewsCache {

	@Nonnull
	private final SparseArray<View> cache = new SparseArray<View>();

	protected ViewsCache() {
	}

	@Nonnull
	public static ViewsCache forActivity(@Nonnull Activity activity) {
		return new ActivityViewsCache(activity);
	}

	@Nonnull
	public static ViewsCache forFragment(@Nonnull Fragment fragment) {
		return new FragmentViewsCache(fragment);
	}

	@Nonnull
	public static ViewsCache forView(@Nonnull View view) {
		return new ViewViewsCache(view);
	}

	public final View findViewById(int id) {
		View view = cache.get(id);
		if (view == null) {
			view = lookupViewById(id);
			if (view != null) {
				cache.append(id, view);
			}
		}
		return view;
	}

	public final void clear() {
		cache.clear();
	}

	protected abstract View lookupViewById(int id);

	private static final class FragmentViewsCache extends ViewsCache {

		@Nonnull
		private final Fragment fragment;

		private FragmentViewsCache(@Nonnull Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		protected View lookupViewById(int id) {
			final View view = fragment.getView();
			return view != null ? view.findViewById(id) : null;
		}
	}

	private static final class ViewViewsCache extends ViewsCache {

		@Nonnull
		private final View view;

		private ViewViewsCache(@Nonnull View view) {
			this.view = view;
		}

		@Override
		protected View lookupViewById(int id) {
			return view.findViewById(id);
		}
	}

	private static final class ActivityViewsCache extends ViewsCache {

		@Nonnull
		private final Activity activity;

		private ActivityViewsCache(@Nonnull Activity activity) {
			this.activity = activity;
		}

		@Override
		protected View lookupViewById(int id) {
			return activity.findViewById(id);
		}
	}
}
