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
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.AMenuItem;
import org.solovyev.android.view.prefs.ResourceCache;
import org.solovyev.common.utils.StringUtils;

/**
* User: serso
* Date: 12/18/11
* Time: 3:09 PM
*/
public enum HistoryItemMenuItem implements AMenuItem<HistoryItemMenuData> {

	use("c_use_expression") {
		@Override
		public void doAction(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			if (context instanceof AbstractHistoryActivity) {
				AbstractHistoryActivity.useHistoryItem(data.getHistoryState(), (AbstractHistoryActivity) context);
			} else {
				Log.e(HistoryItemMenuItem.class.getName(), CalculatorHistoryActivity.class + " must be passed as context!");
			}
		}
	},

	copy_expression("c_copy_expression") {
		@Override
		public void doAction(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			final CalculatorHistoryState calculatorHistoryState = data.getHistoryState();
			final String text = calculatorHistoryState.getEditorState().getText();
			if (!StringUtils.isEmpty(text)) {
				final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
				clipboard.setText(text);
				Toast.makeText(context, context.getText(R.string.c_expression_copied), Toast.LENGTH_SHORT).show();
			}
		}
	},

	copy_result("c_copy_result") {
		@Override
		public void doAction(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			final CalculatorHistoryState calculatorHistoryState = data.getHistoryState();
			final String text = calculatorHistoryState.getDisplayState().getEditorState().getText();
			if (!StringUtils.isEmpty(text)) {
				final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
				clipboard.setText(text);
				Toast.makeText(context, context.getText(R.string.c_result_copied), Toast.LENGTH_SHORT).show();
			}
		}
	},

	save("c_save") {
		@Override
		public void doAction(@NotNull final HistoryItemMenuData data, @NotNull final Context context) {
			final CalculatorHistoryState historyState = data.getHistoryState();
			if (!historyState.isSaved()) {
				createEditHistoryDialog(data, context, true);
			} else {
				Toast.makeText(context, "History item was already saved!", Toast.LENGTH_LONG).show();
			}
		}
	},

	edit("c_edit") {
		@Override
		public void doAction(@NotNull final HistoryItemMenuData data, @NotNull final Context context) {
			final CalculatorHistoryState historyState = data.getHistoryState();
			if (historyState.isSaved()) {
				createEditHistoryDialog(data, context, false);
			} else {
				Toast.makeText(context, "History item must be saved before editing!", Toast.LENGTH_LONG).show();
			}
		}
	},

	remove("c_remove") {
		@Override
		public void doAction(@NotNull HistoryItemMenuData data, @NotNull Context context) {
			final CalculatorHistoryState historyState = data.getHistoryState();
			if (historyState.isSaved()) {
				data.getAdapter().remove(historyState);
				CalculatorHistory.instance.removeSavedHistory(historyState, context, PreferenceManager.getDefaultSharedPreferences(context));
				Toast.makeText(context, "History item was removed!", Toast.LENGTH_LONG).show();
				data.getAdapter().notifyDataSetChanged();
			}
		}
	};

	private static void createEditHistoryDialog(@NotNull final HistoryItemMenuData data, @NotNull final Context context, final boolean save) {
		final CalculatorHistoryState historyState = data.getHistoryState();

		final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View editView = layoutInflater.inflate(R.layout.history_edit, null);
		final TextView historyExpression = (TextView)editView.findViewById(R.id.history_edit_expression);
		historyExpression.setText(AbstractHistoryActivity.getHistoryText(historyState));

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
							final CalculatorHistoryState savedHistoryItem = CalculatorHistory.instance.addSavedState(historyState);
							savedHistoryItem.setComment(comment.getText().toString());
							CalculatorHistory.instance.save(context);
							// we don't need to add element to the adapter as adapter of another activity must be updated and not this
							//data.getAdapter().add(savedHistoryItem);
						} else {
							historyState.setComment(comment.getText().toString());
							CalculatorHistory.instance.save(context);
						}
						data.getAdapter().notifyDataSetChanged();
						Toast.makeText(context, "History item was successfully saved!", Toast.LENGTH_LONG).show();
					}
				})
				.setView(editView);

		builder.create().show();
	}

	@NotNull
	private final String captionId;

	private HistoryItemMenuItem(@NotNull String captionId) {
		this.captionId = captionId;
	}

	@NotNull
	@Override
	public String getCaption() {
		final String caption = ResourceCache.instance.getCaption(getCaptionId());
		return caption == null ? this.name() : caption;
	}

	@NotNull
	public String getCaptionId() {
		return captionId;
	}
}
