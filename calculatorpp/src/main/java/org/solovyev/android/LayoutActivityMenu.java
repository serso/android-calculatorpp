package org.solovyev.android;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.menu.AMenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 4/23/12
 * Time: 1:57 PM
 */
public class LayoutActivityMenu implements ActivityMenu {

    public final int menuLayoutId;

    @NotNull
    private List<LayoutMenuItem> menuItems;

    private LayoutActivityMenu(int menuLayoutId) {
        this.menuLayoutId = menuLayoutId;
    }

    public static <E extends Enum & LayoutMenuItem> ActivityMenu newInstance(int menuLayoutId, @NotNull Class<E> enumMenuClass) {
        final LayoutActivityMenu result = new LayoutActivityMenu(menuLayoutId);

        result.menuItems = new ArrayList<LayoutMenuItem>(enumMenuClass.getEnumConstants().length);

        Collections.addAll(result.menuItems, enumMenuClass.getEnumConstants());

        return result;
    }

    public static ActivityMenu newInstance(int menuLayoutId, @NotNull List<LayoutMenuItem> menuItems) {
        final LayoutActivityMenu result = new LayoutActivityMenu(menuLayoutId);

        result.menuItems = menuItems;

        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(@NotNull Activity activity, @NotNull Menu menu) {
        final MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(menuLayoutId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull Activity activity, @NotNull MenuItem item) {
        for (LayoutMenuItem menuItem : menuItems) {
            if (menuItem.getItemId().equals(item.getItemId())) {
                menuItem.onClick(item, activity);
                return true;
            }
        }

        return activity.onOptionsItemSelected(item);
    }

    public static interface LayoutMenuItem extends AMenuItem<MenuItem> {

        @NotNull
        Integer getItemId();
    }
}
