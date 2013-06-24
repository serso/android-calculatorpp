package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.drag.DirectionDragButton;
import org.solovyev.android.view.drag.DragButton;
import org.solovyev.android.view.drag.DragDirection;
import org.solovyev.android.view.drag.SimpleOnDragListener;
import org.solovyev.common.math.Point2d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class DragButtonWizardStep extends SherlockFragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final String ACTION = "action";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private DirectionDragButton dragButton;

	@Nullable
	private TextView actionTextView;

	private DragButtonAction action = DragButtonAction.center;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			action = (DragButtonAction) savedInstanceState.getSerializable(ACTION);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_drag_button, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		dragButton = (DirectionDragButton) root.findViewById(R.id.wizard_dragbutton);
		dragButton.setOnClickListener(new DragButtonOnClickListener());
		dragButton.setOnDragListener(new SimpleOnDragListener(new DragButtonProcessor(), SimpleOnDragListener.getDefaultPreferences(getActivity())));
		actionTextView = (TextView) root.findViewById(R.id.wizard_dragbutton_action_textview);

		actionTextView.setText(action.actionTextResId);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(ACTION, action);
	}

	/*
	**********************************************************************
	*
	*                           STATIC/INNER
	*
	**********************************************************************
	*/

	private static enum DragButtonAction {
		center(R.string.cpp_wizard_dragbutton_action_center, null),
		up(R.string.cpp_wizard_dragbutton_action_up, DragDirection.up),
		left(R.string.cpp_wizard_dragbutton_action_left, DragDirection.left),
		down(R.string.cpp_wizard_dragbutton_action_down, DragDirection.down),
		end(R.string.cpp_wizard_dragbutton_action_end, null);

		private final int actionTextResId;

		@Nullable
		private final DragDirection dragDirection;

		DragButtonAction(int actionTextResId, @Nullable DragDirection dragDirection) {
			this.actionTextResId = actionTextResId;
			this.dragDirection = dragDirection;
		}

		@Nullable
		DragButtonAction getNextAction() {
			final DragButtonAction[] values = values();
			final int position = Arrays.binarySearch(values, this);
			if (position < values.length - 1) {
				return values[position + 1];
			} else {
				return values[0];
			}
		}
	}

	private class DragButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if(action == DragButtonAction.center || action == DragButtonAction.end) {
				setNextAction();
			}
		}
	}

	private void setNextAction() {
		setAction(action.getNextAction());
	}

	private class DragButtonProcessor implements SimpleOnDragListener.DragProcessor {
		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection,
										@Nonnull DragButton dragButton,
										@Nonnull Point2d startPoint2d,
										@Nonnull MotionEvent motionEvent) {
			if(action.dragDirection == dragDirection) {
				setNextAction();
				return true;
			}
			return false;
		}
	}

	private void setAction(DragButtonAction action) {
		if (this.action != action) {
			this.action = action;
			if (actionTextView != null) {
				actionTextView.setText(this.action.actionTextResId);
			}
		}
	}
}
