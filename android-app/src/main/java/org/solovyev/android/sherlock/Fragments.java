package org.solovyev.android.sherlock;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import javax.annotation.Nonnull;

public class Fragments extends org.solovyev.android.Fragments {

	private Fragments() {
	}

	public static void showDialog(@Nonnull DialogFragment dialogFragment,
								  @Nonnull String fragmentTag,
								  @Nonnull FragmentManager fm) {
		showDialog(dialogFragment, fragmentTag, fm, true);
	}

	public static void showDialog(DialogFragment dialogFragment, String fragmentTag, FragmentManager fm, boolean useExisting) {
		Fragment prev = fm.findFragmentByTag(fragmentTag);
		if (prev != null) {
			if (!useExisting) {
				final FragmentTransaction ft = fm.beginTransaction();
				ft.remove(prev);
				ft.addToBackStack(null);

				// Create and show the dialog.
				dialogFragment.show(ft, fragmentTag);
				fm.executePendingTransactions();
			}
		} else {
			final FragmentTransaction ft = fm.beginTransaction();

			ft.addToBackStack(null);

			// Create and show the dialog.
			dialogFragment.show(ft, fragmentTag);
			fm.executePendingTransactions();
		}
	}
}
