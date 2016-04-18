package org.solovyev.android.calculator.buttons;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CppSpecialButtonTest {

    @Test
    public void testShouldReturnButtonByGlyph() throws Exception {
        assertEquals(CppSpecialButton.copy, CppSpecialButton.getByGlyph(CppSpecialButton.copy.glyph));
        assertEquals(CppSpecialButton.paste, CppSpecialButton.getByGlyph(CppSpecialButton.paste.glyph));
    }

    @Test
    public void testShouldReturnNullForButtonWithoutGlyph() throws Exception {
        assertNull(CppSpecialButton.getByGlyph(CppSpecialButton.brackets_wrap.glyph));
    }
}