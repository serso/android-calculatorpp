package org.solovyev.android.calculator.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.FragmentTab;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.entities.BaseEntitiesFragment;
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.views.Adjuster;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Tabs {

    @Nonnull
    private final AppCompatActivity activity;
    @Nonnull
    private final TabFragments adapter;
    @Nullable
    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Nullable
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    public Tabs(@Nonnull AppCompatActivity activity) {
        this.activity = activity;
        this.adapter = new TabFragments(activity.getSupportFragmentManager());
    }

    public void onCreate() {
        ButterKnife.bind(this, activity);

        if (tabLayout == null || viewpager == null) {
            return;
        }
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);

        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayout.setupWithViewPager(viewpager);
        } else {
            final ViewTreeObserver treeObserver = Adjuster.getTreeObserver(tabLayout);
            if (treeObserver != null) {
                treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final ViewTreeObserver anotherTreeObserver = Adjuster.getTreeObserver(tabLayout);
                        if(anotherTreeObserver != null) {
                            //noinspection deprecation
                            anotherTreeObserver.removeGlobalOnLayoutListener(this);
                        }
                        tabLayout.setupWithViewPager(viewpager);
                    }
                });
            }
        }
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
