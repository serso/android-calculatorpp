package org.solovyev.android.calculator.keyboard;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.R;
import org.solovyev.android.views.dragbutton.DirectionDragButton;

public abstract class BaseFloatingKeyboard implements FloatingKeyboard {

    @NonNull
    protected final User user;
    @ColorInt
    private final int textColor;
    @ColorInt
    private final int textColorSecondary;
    private final int sidePadding;
    @DrawableRes
    private final int buttonBackground;

    @SuppressWarnings("deprecation")
    protected BaseFloatingKeyboard(@NonNull User user) {
        this.user = user;
        final Resources resources = user.getResources();
        textColor = resources.getColor(R.color.cpp_button_text);
        textColorSecondary = resources.getColor(R.color.cpp_button_text);
        sidePadding = resources.getDimensionPixelSize(R.dimen.cpp_button_padding);
        buttonBackground = App.getTheme().light ? R.drawable.material_button_light : R.drawable.material_button_dark;
    }

    @NonNull
    @Override
    public User getUser() {
        return user;
    }

    @NonNull
    protected final LinearLayout makeRow() {
        final LinearLayout row = new LinearLayout(user.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.weight = 1f;
        user.getKeyboard().addView(row, lp);
        return row;
    }

    @NonNull
    protected DirectionDragButton makeButton(@IdRes int id, @NonNull String text) {
        final DirectionDragButton button = new DirectionDragButton(user.getContext());
        fillButton(button, id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            button.setAllCaps(false);
        }
        button.setText(text);
        button.setTextColor(textColor);
        button.setDirectionTextColor(textColorSecondary);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        button.setVibrateOnDrag(user.isVibrateOnKeypress());
        if (TextUtils.isEmpty(text)) {
            button.setEnabled(false);
        }
        return button;
    }

    protected void fillButton(@NonNull View button, @IdRes int id) {
        BaseActivity.setFont(button, user.getTypeface());
        button.setId(id);
        button.setBackgroundResource(buttonBackground);
        button.setPadding(sidePadding, 1, sidePadding, 1);
        button.setHapticFeedbackEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setStateListAnimator(null);
        }
    }

    @NonNull
    protected final View makeImageButton(@IdRes int id, @DrawableRes int icon) {
        final ImageButton button = new ImageButton(user.getContext());
        fillButton(button, id);
        button.setImageResource(icon);
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return button;
    }

    @NonNull
    protected final View addImageButton(@NonNull LinearLayout row, @IdRes int id, @DrawableRes int icon) {
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1f;
        final View view = makeImageButton(id, icon);
        row.addView(view, lp);
        return view;
    }

    @NonNull
    protected final DirectionDragButton addOperationButton(@NonNull LinearLayout row, @IdRes int id, @NonNull String text) {
        final DirectionDragButton button = addButton(row, id, text);
        button.setBackgroundResource(R.drawable.material_button_light_primary);
        button.setTextColor(Color.WHITE);
        button.setDirectionTextAlpha(0.7f);
        return button;
    }

    @NonNull
    protected final DirectionDragButton addButton(@NonNull LinearLayout row, @IdRes int id, @NonNull String text) {
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1f;
        final DirectionDragButton view = makeButton(id, text);
        row.addView(view, lp);
        return view;
    }
}
