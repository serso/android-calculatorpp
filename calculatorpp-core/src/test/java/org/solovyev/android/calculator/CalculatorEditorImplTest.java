package org.solovyev.android.calculator;

import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 12:44
 */
public class CalculatorEditorImplTest {

    @NotNull
    private CalculatorEditor calculatorEditor;

    @Before
    public void setUp() throws Exception {
        this.calculatorEditor = new CalculatorEditorImpl(new CalculatorImpl());
    }

    @Test
    public void testInsert() throws Exception {
        CalculatorEditorViewState viewState = this.calculatorEditor.getViewState();

        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.insert("");

        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.insert("test");

        Assert.assertEquals("test", viewState.getText());
        Assert.assertEquals(4, viewState.getSelection());

        viewState = this.calculatorEditor.insert("test");
        Assert.assertEquals("testtest", viewState.getText());
        Assert.assertEquals(8, viewState.getSelection());

        viewState = this.calculatorEditor.insert("");
        Assert.assertEquals("testtest", viewState.getText());
        Assert.assertEquals(8, viewState.getSelection());

        viewState = this.calculatorEditor.insert("1234567890");
        Assert.assertEquals("testtest1234567890", viewState.getText());
        Assert.assertEquals(18, viewState.getSelection());

        viewState = this.calculatorEditor.moveCursorLeft();
        viewState = this.calculatorEditor.insert("9");
        Assert.assertEquals("testtest12345678990", viewState.getText());
        Assert.assertEquals(18, viewState.getSelection());

        viewState = this.calculatorEditor.setCursorOnStart();
        viewState = this.calculatorEditor.insert("9");
        Assert.assertEquals("9testtest12345678990", viewState.getText());
        Assert.assertEquals(1, viewState.getSelection());

        viewState = this.calculatorEditor.erase();
        viewState = this.calculatorEditor.insert("9");
        Assert.assertEquals("9testtest12345678990", viewState.getText());
        Assert.assertEquals(1, viewState.getSelection());
    }

    @Test
    public void testErase() throws Exception {
        this.calculatorEditor.setText("");
        this.calculatorEditor.erase();

        Assert.assertEquals("", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.setText("test");
        this.calculatorEditor.erase();
        Assert.assertEquals("tes", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.erase();
        Assert.assertEquals("te", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.erase();
        Assert.assertEquals("t", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.erase();
        Assert.assertEquals("", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.erase();
        Assert.assertEquals("", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.setText("1234");
        this.calculatorEditor.moveCursorLeft();
        this.calculatorEditor.erase();
        Assert.assertEquals("124", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.erase();
        Assert.assertEquals("14", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.erase();
        Assert.assertEquals("4", this.calculatorEditor.getViewState().getText());

        this.calculatorEditor.setText("1");
        this.calculatorEditor.moveCursorLeft();
        this.calculatorEditor.erase();
        Assert.assertEquals("1", this.calculatorEditor.getViewState().getText());
    }

    @Test
    public void testMoveSelection() throws Exception {
        this.calculatorEditor.setText("");

        CalculatorEditorViewState viewState = this.calculatorEditor.moveSelection(0);
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(2);
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(100);
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(-3);
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(-100);
        Assert.assertEquals(0, viewState.getSelection());

        viewState =  this.calculatorEditor.setText("0123456789");

        viewState = this.calculatorEditor.moveSelection(0);
        Assert.assertEquals(10, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(1);
        Assert.assertEquals(10, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(-2);
        Assert.assertEquals(8, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(1);
        Assert.assertEquals(9, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(-9);
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(-10);
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(2);
        Assert.assertEquals(2, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(2);
        Assert.assertEquals(4, viewState.getSelection());

        viewState = this.calculatorEditor.moveSelection(-6);
        Assert.assertEquals(0, viewState.getSelection());
    }

    @Test
    public void testSetText() throws Exception {
        CalculatorEditorViewState viewState = this.calculatorEditor.setText("test");

        Assert.assertEquals("test", viewState.getText());
        Assert.assertEquals(4, viewState.getSelection());

        viewState = this.calculatorEditor.setText("testtest");
        Assert.assertEquals("testtest", viewState.getText());
        Assert.assertEquals(8, viewState.getSelection());

        viewState = this.calculatorEditor.setText("");
        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.setText("testtest", 0);
        Assert.assertEquals("testtest", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.setText("testtest", 2);
        Assert.assertEquals("testtest", viewState.getText());
        Assert.assertEquals(2, viewState.getSelection());

        viewState = this.calculatorEditor.setText("", 0);
        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.setText("", 3);
        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.setText("", -3);
        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());

        viewState = this.calculatorEditor.setText("test");
        Assert.assertEquals("test", viewState.getText());
        Assert.assertEquals(4, viewState.getSelection());

        viewState = this.calculatorEditor.setText("", 2);
        Assert.assertEquals("", viewState.getText());
        Assert.assertEquals(0, viewState.getSelection());
    }
}
