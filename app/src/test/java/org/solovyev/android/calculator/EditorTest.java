/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import static org.mockito.Mockito.mock;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class)
@RunWith(value = RobolectricTestRunner.class)
public class EditorTest {

    private Editor editor;

    @Before
    public void setUp() throws Exception {
        editor = new Editor(RuntimeEnvironment.application, mock(SharedPreferences.class), Tests.makeEngine());
        editor.bus = mock(Bus.class);
        // real text processor causes Robolectric to crash: NullPointerException at
        // org.robolectric.res.ThemeStyleSet$OverlayedStyle.equals
        editor.textProcessor = null;
    }

    @Test
    public void testInsert() throws Exception {
        EditorState viewState = editor.getState();

        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = insertAndGet("");

        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = insertAndGet("test");

        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = insertAndGet("test");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = insertAndGet("");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = insertAndGet("1234567890");
        Assert.assertEquals("testtest1234567890", viewState.getTextString());
        Assert.assertEquals(18, viewState.selection);

        editor.moveCursorLeft();
        viewState = insertAndGet("9");
        Assert.assertEquals("testtest12345678990", viewState.getTextString());
        Assert.assertEquals(18, viewState.selection);

        editor.setCursorOnStart();
        viewState = insertAndGet("9");
        Assert.assertEquals("9testtest12345678990", viewState.getTextString());
        Assert.assertEquals(1, viewState.selection);

        editor.erase();
        viewState = insertAndGet("9");
        Assert.assertEquals("9testtest12345678990", viewState.getTextString());
        Assert.assertEquals(1, viewState.selection);

        viewState = insertAndGet("öäü");
        Assert.assertEquals("9öäütesttest12345678990", viewState.getTextString());

        editor.setCursorOnEnd();
        viewState = insertAndGet("öäü");
        Assert.assertEquals("9öäütesttest12345678990öäü", viewState.getTextString());
    }

    @NonNull
    private EditorState insertAndGet(@NonNull String text) {
        editor.insert(text);
        return editor.getState();
    }

    @Test
    public void testErase() throws Exception {
        setTextAndGet("");
        editor.erase();

        Assert.assertEquals("", editor.getState().getTextString());

        setTextAndGet("test");
        editor.erase();
        Assert.assertEquals("tes", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("te", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("t", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("", editor.getState().getTextString());

        setTextAndGet("1234");
        editor.moveCursorLeft();
        editor.erase();
        Assert.assertEquals("124", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("14", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("4", editor.getState().getTextString());

        setTextAndGet("1");
        editor.moveCursorLeft();
        editor.erase();
        Assert.assertEquals("1", editor.getState().getTextString());
    }

    @Test
    public void testMoveSelection() throws Exception {
        setTextAndGet("");

        EditorState viewState = editor.moveSelection(0);
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.moveSelection(2);
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.moveSelection(100);
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.moveSelection(-3);
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.moveSelection(-100);
        Assert.assertEquals(0, viewState.selection);

        setTextAndGet("0123456789");

        viewState = editor.moveSelection(0);
        Assert.assertEquals(10, viewState.selection);

        viewState = editor.moveSelection(1);
        Assert.assertEquals(10, viewState.selection);

        viewState = editor.moveSelection(-2);
        Assert.assertEquals(8, viewState.selection);

        viewState = editor.moveSelection(1);
        Assert.assertEquals(9, viewState.selection);

        viewState = editor.moveSelection(-9);
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.moveSelection(-10);
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.moveSelection(2);
        Assert.assertEquals(2, viewState.selection);

        viewState = editor.moveSelection(2);
        Assert.assertEquals(4, viewState.selection);

        viewState = editor.moveSelection(-6);
        Assert.assertEquals(0, viewState.selection);
    }

    @Test
    public void testSetText() throws Exception {
        EditorState viewState = setTextAndGet("test");

        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = setTextAndGet("testtest");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = setTextAndGet("");
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = setTextAndGet("testtest", 0);
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = setTextAndGet("testtest", 2);
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(2, viewState.selection);

        viewState = setTextAndGet("", 0);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = setTextAndGet("", 3);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = setTextAndGet("", -3);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = setTextAndGet("test");
        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = setTextAndGet("", 2);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);
    }

    @NonNull
    private EditorState setTextAndGet(@NonNull String text, int selection) {
        editor.setText(text, selection);
        return editor.getState();
    }

    @NonNull
    private EditorState setTextAndGet(@NonNull String text) {
        editor.setText(text);
        return editor.getState();
    }
}
