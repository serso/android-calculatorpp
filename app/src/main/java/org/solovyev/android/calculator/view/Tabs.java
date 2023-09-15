package org.solovyev.android.calculator.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import org.solovyev.android.calculator.AppModule;
import org.solovyev.android.calculator.FragmentTab;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.entities.BaseEntitiesFragment;
import org.solovyev.android.calculator.entities.Category;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.App.cast;

public class Tabs {

    @Nonnull
    private final AppCompatActivity activity;
    @Nonnull
    private final TabFragments adapter;
    @Inject
    @Named(AppModule.PREFS_TABS)
    SharedPreferences preferences;
    @Nullable
    TabLayout tabLayout;
    @Nullable
    ViewPager viewPager;
    private int defaultSelectedTab = -1;

    public Tabs(@Nonnull AppCompatActivity activity) {
        this.activity = activity;
        this.adapter = new TabFragments(activity.getSupportFragmentManager());
    }

    @Nonnull
    private static String makeTabKey(@Nonnull Activity activity) {
        return activity.getClass().getSimpleName();
    }

    public void onCreate() {
        cast(activity.getApplicationContext()).getComponent().inject(this);
        tabLayout = activity.findViewById(R.id.tabs);
        viewPager = activity.findViewById(R.id.viewPager);

        if (tabLayout == null || viewPager == null) {
            return;
        }
        final int tabs = adapter.getCount();
        if (tabs == 0) {
            tabLayout.setVisibility(View.GONE);
            return;
        }
        viewPager.setAdapter(adapter);
        tabLayout.setTabMode(tabs > 3 ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        restoreSelectedTab();
    }

    public void addTab(@Nonnull Category category, @Nonnull FragmentTab tab) {
        addTab(category, tab, activity.getString(category.title()));
    }

    public final void addTab(@Nonnull Category category, @Nonnull FragmentTab tab, @Nonnull CharSequence title) {
        final Bundle arguments = new Bundle(1);
        arguments.putString(BaseEntitiesFragment.ARG_CATEGORY, category.name());
        addTab(tab.type, arguments, title);
    }

    public void addTab(@Nonnull FragmentTab tab) {
        addTab(tab.type, null, activity.getString(tab.title));
    }

    public void addTab(@Nonnull Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentArgs, @Nonnull CharSequence title) {
        adapter.add(new TabFragment(fragmentClass, fragmentArgs, title));
    }

    @Nullable
    public Fragment getCurrentFragment() {
        if (viewPager == null) {
            return null;
        }
        return adapter.getItem(viewPager.getCurrentItem());
    }

    public int getCurrentTab() {
        if (viewPager == null) {
            return -1;
        }
        return viewPager.getCurrentItem();
    }

    public int getTabCount() {
        return adapter.getCount();
    }

    public void selectTab(int index) {
        if (tabLayout == null) {
            return;
        }
        final TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }

    public void setDefaultSelectedTab(int defaultSelectedTab) {
        this.defaultSelectedTab = defaultSelectedTab;
    }

    public void restoreSelectedTab() {
        final int selectedTab = preferences.getInt(makeTabKey(activity), defaultSelectedTab);
        if (selectedTab >= 0 && selectedTab < getTabCount()) {
            selectTab(selectedTab);
        }
    }

    public void onPause() {
        saveSelectedTab();
    }

    private void saveSelectedTab() {
        final int selectedTab = getCurrentTab();
        if (selectedTab >= 0) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(makeTabKey(activity), selectedTab);
            editor.apply();
        }
    }

    private final class TabFragments extends FragmentPagerAdapter {

        @Nonnull
        private final List<TabFragment> list = new ArrayList<>();

        public TabFragments(@Nonnull FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position).makeFragment();
        }

        public void add(@Nonnull TabFragment tabFragment) {
            list.add(tabFragment);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).title;
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    private final class TabFragment {
        @Nonnull
        final Class<? extends Fragment> fragmentClass;
        @Nullable
        final Bundle fragmentArgs;
        @Nonnull
        final CharSequence title;

        public TabFragment(@Nonnull Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentArgs, @Nonnull CharSequence title) {
            this.fragmentClass = fragmentClass;
            this.fragmentArgs = fragmentArgs;
            this.title = title;
        }

        @Nonnull
        public Fragment makeFragment() {
            return Fragment.instantiate(activity, fragmentClass.getName(), fragmentArgs);
        }
    }
}
