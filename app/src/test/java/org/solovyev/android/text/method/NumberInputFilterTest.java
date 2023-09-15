package org.solovyev.android.text.method;

import static org.junit.Assert.assertEquals;

import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.BuildConfig;

@RunWith(RobolectricTestRunner.class)
public class NumberInputFilterTest {

    private Editable editable;

    @Before
    public void setUp() throws Exception {
        editable = new SpannableStringBuilder();
        editable.setFilters(new InputFilter[]{new NumberInputFilter()});
    }

    @Test
    public void testShouldNotInsertExponentInTheBeginning() throws Exception {
        editable.insert(0, "E");
        assertEquals("", editable.toString());
    }

    @Test
    public void testShouldInsertExponentAtTheEnd() throws Exception {
        editable.insert(0, "1");
        editable.insert(1, "E");
        assertEquals("1E", editable.toString());
    }

    @Test
    public void testShouldNotInsertSecondMinusSign() throws Exception {
        editable.insert(0, "-");
        editable.insert(1, "-");
        assertEquals("-", editable.toString());
    }

    @Test
    public void testShouldNotInsertTwoMinusSigns() throws Exception {
        editable.insert(0, "--");
        assertEquals("-", editable.toString());
    }

    @Test
    public void testShouldInsertSecondMinusSignAfterExponent() throws Exception {
        editable.insert(0, "-");
        editable.insert(1, "E");
        editable.insert(2, "-");
        assertEquals("-E-", editable.toString());
    }

    @Test
    public void testShouldInsertSecondMinusSignAlongWithExponent() throws Exception {
        editable.insert(0, "-");
        editable.insert(1, "E-");
        assertEquals("-E-", editable.toString());
    }

    @Test
    public void testShouldNotInsertMinusSignBeforeExistingMinusSIgn() throws Exception {
        editable.insert(0, "-");
        editable.insert(0, "-");
        assertEquals("-", editable.toString());
    }

    @Test
    public void testShouldNotInsertSecondDecimalPoint() throws Exception {
        editable.insert(0, "0.2");
        editable.insert(3, ".");
        assertEquals("0.2", editable.toString());
    }

    @Test
    public void testShouldNotInsertTwoDecimalPoints() throws Exception {
        editable.insert(0, "..");
        assertEquals(".", editable.toString());
    }

    @Test
    public void testShouldNotInsertDecimalPointAfterExponent() throws Exception {
        editable.insert(0, "2E");
        editable.insert(2, ".");
        assertEquals("2E", editable.toString());

        editable.clear();
        editable.insert(0, "2E.");
        assertEquals("2E", editable.toString());
    }

    @Test
    public void testShouldNotInsertTwoExcponents() throws Exception {
        editable.insert(0, "2EE");
        assertEquals("2E", editable.toString());
    }

    @Test
    public void testShouldNotInsertExponentBeforeDecimalPoint() throws Exception {
        editable.insert(0, "0.2");
        editable.insert(0, "E");
        assertEquals("0.2", editable.toString());
    }
}
