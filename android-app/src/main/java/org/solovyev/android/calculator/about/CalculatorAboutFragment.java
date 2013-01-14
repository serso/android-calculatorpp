/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.about;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 12/24/11
 * Time: 11:55 PM
 */
public class CalculatorAboutFragment extends CalculatorFragment {

    public CalculatorAboutFragment() {
        super(CalculatorFragmentType.about);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final TextView about = (TextView) root.findViewById(R.id.aboutTextView);
        about.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
