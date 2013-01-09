package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.view.TextViewBuilder;
import org.solovyev.android.view.UpdatableViewBuilder;

public class ParcelablePlotInputListItem implements ListItem {

	@NotNull
	private ParcelablePlotInput plotInput;

	@NotNull
	private UpdatableViewBuilder<TextView> viewBuilder;

	public ParcelablePlotInputListItem(@NotNull ParcelablePlotInput plotInput) {
		this.plotInput = plotInput;
		// todo serso: use correct tag
		this.viewBuilder = TextViewBuilder.newInstance(R.layout.plot_functions_fragment_list_item, null);
	}

	@Nullable
	@Override
	public OnClickAction getOnClickAction() {
		return null;
	}

	@Nullable
	@Override
	public OnClickAction getOnLongClickAction() {
		return null;
	}

	@NotNull
	@Override
	public View updateView(@NotNull Context context, @NotNull View view) {
		// todo serso: optimize
		return build(context);
	}

	@NotNull
	@Override
	public View build(@NotNull Context context) {
		TextView textView = viewBuilder.build(context);
		fill(textView);
		return textView;
	}

	private void fill(@NotNull TextView textView) {
		textView.setText(plotInput.getExpression());
	}
}
