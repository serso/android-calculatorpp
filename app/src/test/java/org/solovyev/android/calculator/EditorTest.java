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

import android.content.SharedPreferences;
import android.os.Build;
import com.squareup.otto.Bus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(value = RobolectricGradleTestRunner.class)
public class EditorTest {

    private Editor editor;

    @Before
    public void setUp() throws Exception {
        editor = new Editor(mock(SharedPreferences.class), Tests.makeEngine());
        editor.bus = mock(Bus.class);
    }

    @Test
    public void testInsert() throws Exception {
        EditorState viewState = editor.getState();

        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.insert("");

        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.insert("test");

        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = editor.insert("test");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = editor.insert("");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = editor.insert("1234567890");
        Assert.assertEquals("testtest1234567890", viewState.getTextString());
        Assert.assertEquals(18, viewState.selection);

        editor.moveCursorLeft();
        viewState = editor.insert("9");
        Assert.assertEquals("testtest12345678990", viewState.getTextString());
        Assert.assertEquals(18, viewState.selection);

        editor.setCursorOnStart();
        viewState = editor.insert("9");
        Assert.assertEquals("9testtest12345678990", viewState.getTextString());
        Assert.assertEquals(1, viewState.selection);

        editor.erase();
        viewState = editor.insert("9");
        Assert.assertEquals("9testtest12345678990", viewState.getTextString());
        Assert.assertEquals(1, viewState.selection);

        viewState = editor.insert("öäü");
        Assert.assertEquals("9öäütesttest12345678990", viewState.getTextString());

        editor.setCursorOnEnd();
        viewState = editor.insert("öäü");
        Assert.assertEquals("9öäütesttest12345678990öäü", viewState.getTextString());
    }

    @Test
    public void testErase() throws Exception {
        editor.setText("");
        editor.erase();

        Assert.assertEquals("", editor.getState().getTextString());

        editor.setText("test");
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

        editor.setText("1234");
        editor.moveCursorLeft();
        editor.erase();
        Assert.assertEquals("124", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("14", editor.getState().getTextString());

        editor.erase();
        Assert.assertEquals("4", editor.getState().getTextString());

        editor.setText("1");
        editor.moveCursorLeft();
        editor.erase();
        Assert.assertEquals("1", editor.getState().getTextString());
    }

    @Test
    public void testMoveSelection() throws Exception {
        editor.setText("");

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

        editor.setText("0123456789");

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
        EditorState viewState = editor.setText("test");

        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = editor.setText("testtest");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = editor.setText("");
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.setText("testtest", 0);
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.setText("testtest", 2);
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(2, viewState.selection);

        viewState = editor.setText("", 0);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.setText("", 3);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.setText("", -3);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = editor.setText("test");
        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = editor.setText("", 2);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);
    }
}
