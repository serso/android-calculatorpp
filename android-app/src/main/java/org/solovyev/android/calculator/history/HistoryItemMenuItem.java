/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.text.Strings;

/**
* User: serso
* Date: 12/18/11
* Time: 3:09 PM
*/
public enum HistoryItemMenuItem implements LabeledMenuItem<HistoryItemMenuData> {

	use(R.string.c_use) {
		@Override
		public void onClick(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			AbstractCalculatorHistoryFragment.useHistoryItem(data.getHistoryState());
		}
	},

	copy_expression(R.string.c_copy_expression) {
		@Override
		public void onClick(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			final CalculatorHistoryState calculatorHistoryState = data.getHistoryState();
			final String text = calculatorHistoryState.getEditorState().getText();
			if (!Strings.isEmpty(text)) {
				final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
				clipboard.setText(text);
				Toast.makeText(context, context.getText(R.string.c_expression_copied), Toast.LENGTH_SHORT).show();
			}
		}
	},

	copy_result(R.string.c_copy_result) {
		@Override
		public void onClick(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			final CalculatorHistoryState calculatorHistoryState = data.getHistoryState();
			final String text = calculatorHistoryState.getDisplayState().getEditorState().getText();
			if (!Strings.isEmpty(text)) {
				final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
				clipboard.setText(text);
				Toast.makeText(context, context.getText(R.string.c_result_copied), Toast.LENGTH_SHORT).show();
			}
		}
	},

	save(R.string.c_save) {
		@Override
		public void onClick(@NotNull final HistoryItemMenuData data, @NotNull final Context context) {
			final CalculatorHistoryState historyState = data.getHistoryState();
			if (!historyState.isSaved()) {
				createEditHistoryDialog(data, context, true);
			} else {
				Toast.makeText(context, context.getText(R.string.c_history_already_saved), Toast.LENGTH_LONG).show();
			}
		}
	},

	edit(R.string.c_edit) {
		@Override
		public void onClick(@NotNull final HistoryItemMenuData data, @NotNull final Context context) {
			final CalculatorHistoryState historyState = data.getHistoryState();
			if (historyState.isSaved()) {
				createEditHistoryDialog(data, context, false);
			} else {
				Toast.makeText(context, context.getText(R.string.c_history_must_be_saved), Toast.LENGTH_LONG).show();
			}
		}
	},

	remove(R.string.c_remove) {
		@Override
		public void onClick(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			final CalculatorHistoryState historyState = data.getHistoryState();
			if (historyState.isSaved()) {
				data.getAdapter().remove(historyState);
				Locator.getInstance().getHistory().removeSavedHistory(historyState);
				Toast.makeText(context, context.getText(R.string.c_history_was_removed), Toast.LENGTH_LONG).show();
				data.getAdapter().notifyDataSetChanged();
			}
		}
	};

	private static void createEditHistoryDialog(@NotNull final HistoryItemMenuData data, @NotNull final Context context, final boolean save) {
		final CalculatorHistoryState historyState = data.getHistoryState();

		final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View editView = layoutInflater.inflate(R.layout.history_edit, null);
		final TextView historyExpression = (TextView)editView.findViewById(R.id.history_edit_expression);
		historyExpression.setText(AbstractCalculatorHistoryFragment.getHistoryText(historyState));

		final EditText comment = (EditText)editView.findViewById(R.id.history_edit_comment);
		comment.setText(historyState.getComment());

		final AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setTitle(save ? R.string.c_save_history : R.string.c_edit_history)
				.setCancelable(true)
				.setNegativeButton(R.string.c_cancel, null)
				.setPositiveButton(R.string.c_save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (save) {
							final CalculatorHistoryState savedHistoryItem = Locator.getInstance().getHistory().addSavedState(historyState);
							savedHistoryItem.setComment(comment.getText().toString());
                            Locator.getInstance().getHistory().save();
							// we don't need to add element to the adapter as adapter of another activity must be updated and not this
							//data.getAdapter().add(savedHistoryItem);
						} else {
							historyState.setComment(comment.getText().toString());
                            Locator.getInstance().getHistory().save();
						}
						data.getAdapter().notifyDataSetChanged();
						Toast.makeText(context, context.getText(R.string.c_history_saved), Toast.LENGTH_LONG).show();
					}
				})
				.setView(editView);

		builder.create().show();
	}

	private final int captionId;

	private HistoryItemMenuItem(int captionId) {
		this.captionId = captionId;
	}

	@NotNull
	@Override
	public String getCaption(@NotNull Context context) {
		return context.getString(captionId);
	}
}
