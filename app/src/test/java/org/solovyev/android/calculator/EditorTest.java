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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 12:44
 */
public class EditorTest extends AbstractCalculatorTest {

    @Nonnull
    private Editor editor;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.editor = new Editor(Locator.getInstance().getCalculator(), null);
    }

    @Test
    public void testInsert() throws Exception {
        EditorState viewState = this.editor.getState();

        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.insert("");

        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.insert("test");

        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = this.editor.insert("test");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = this.editor.insert("");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = this.editor.insert("1234567890");
        Assert.assertEquals("testtest1234567890", viewState.getTextString());
        Assert.assertEquals(18, viewState.selection);

        viewState = this.editor.moveCursorLeft();
        viewState = this.editor.insert("9");
        Assert.assertEquals("testtest12345678990", viewState.getTextString());
        Assert.assertEquals(18, viewState.selection);

        viewState = this.editor.setCursorOnStart();
        viewState = this.editor.insert("9");
        Assert.assertEquals("9testtest12345678990", viewState.getTextString());
        Assert.assertEquals(1, viewState.selection);

        viewState = this.editor.erase();
        viewState = this.editor.insert("9");
        Assert.assertEquals("9testtest12345678990", viewState.getTextString());
        Assert.assertEquals(1, viewState.selection);

        viewState = this.editor.insert("öäü");
        Assert.assertEquals("9öäütesttest12345678990", viewState.getTextString());

        this.editor.setCursorOnEnd();
        viewState = this.editor.insert("öäü");
        Assert.assertEquals("9öäütesttest12345678990öäü", viewState.getTextString());
    }

    @Test
    public void testErase() throws Exception {
        this.editor.setText("");
        this.editor.erase();

        Assert.assertEquals("", this.editor.getState().getTextString());

        this.editor.setText("test");
        this.editor.erase();
        Assert.assertEquals("tes", this.editor.getState().getTextString());

        this.editor.erase();
        Assert.assertEquals("te", this.editor.getState().getTextString());

        this.editor.erase();
        Assert.assertEquals("t", this.editor.getState().getTextString());

        this.editor.erase();
        Assert.assertEquals("", this.editor.getState().getTextString());

        this.editor.erase();
        Assert.assertEquals("", this.editor.getState().getTextString());

        this.editor.setText("1234");
        this.editor.moveCursorLeft();
        this.editor.erase();
        Assert.assertEquals("124", this.editor.getState().getTextString());

        this.editor.erase();
        Assert.assertEquals("14", this.editor.getState().getTextString());

        this.editor.erase();
        Assert.assertEquals("4", this.editor.getState().getTextString());

        this.editor.setText("1");
        this.editor.moveCursorLeft();
        this.editor.erase();
        Assert.assertEquals("1", this.editor.getState().getTextString());
    }

    @Test
    public void testMoveSelection() throws Exception {
        this.editor.setText("");

        EditorState viewState = this.editor.moveSelection(0);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.moveSelection(2);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.moveSelection(100);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.moveSelection(-3);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.moveSelection(-100);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.setText("0123456789");

        viewState = this.editor.moveSelection(0);
        Assert.assertEquals(10, viewState.selection);

        viewState = this.editor.moveSelection(1);
        Assert.assertEquals(10, viewState.selection);

        viewState = this.editor.moveSelection(-2);
        Assert.assertEquals(8, viewState.selection);

        viewState = this.editor.moveSelection(1);
        Assert.assertEquals(9, viewState.selection);

        viewState = this.editor.moveSelection(-9);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.moveSelection(-10);
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.moveSelection(2);
        Assert.assertEquals(2, viewState.selection);

        viewState = this.editor.moveSelection(2);
        Assert.assertEquals(4, viewState.selection);

        viewState = this.editor.moveSelection(-6);
        Assert.assertEquals(0, viewState.selection);
    }

    @Test
    public void testSetText() throws Exception {
        EditorState viewState = this.editor.setText("test");

        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = this.editor.setText("testtest");
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(8, viewState.selection);

        viewState = this.editor.setText("");
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.setText("testtest", 0);
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.setText("testtest", 2);
        Assert.assertEquals("testtest", viewState.getTextString());
        Assert.assertEquals(2, viewState.selection);

        viewState = this.editor.setText("", 0);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.setText("", 3);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.setText("", -3);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);

        viewState = this.editor.setText("test");
        Assert.assertEquals("test", viewState.getTextString());
        Assert.assertEquals(4, viewState.selection);

        viewState = this.editor.setText("", 2);
        Assert.assertEquals("", viewState.getTextString());
        Assert.assertEquals(0, viewState.selection);
    }
}
