// Copyright (C) 2009 Mihai Preda
  
package arity.calculator;

import android.text.Editable;
import android.text.SpannableStringBuilder;
  
class CalculatorEditable extends SpannableStringBuilder {
    static class Factory extends Editable.Factory {
        public Editable newEditable(CharSequence source) {
            return new CalculatorEditable(source);
        }
    }

    static final char MINUS = '\u2212', TIMES = '\u00d7', DIV = '\u00f7';
    private boolean isRec;

    public CalculatorEditable(CharSequence source) {
	super(source);
    }

    public SpannableStringBuilder replace(int start, int end, CharSequence buf, int bufStart, int bufEnd) {
	if (isRec || bufEnd - bufStart != 1) {
	    return super.replace(start, end, buf, bufStart, bufEnd);
	} else {
	    isRec = true;                
	    try {
		char c = buf.charAt(bufStart);
		return internalReplace(start, end, c);
	    } finally {
		isRec = false;
	    }
	}
    }

    private boolean isOperator(char c) {
	return "\u2212\u00d7\u00f7+-/*=^,".indexOf(c) != -1;
    }

    private SpannableStringBuilder internalReplace(int start, int end, char c) {
	switch (c) {
	case '-': c = MINUS; break;
	case '*': c = TIMES; break;
	case '/': c = DIV;   break;
	}
	if (c == '.') {
	    int p = start - 1;
	    while (p >= 0 && Character.isDigit(charAt(p))) {
		--p;
	    }
	    if (p >= 0 && charAt(p) == '.') {
		return super.replace(start, end, "");
	    }
	}
	
	char prevChar = start > 0 ? charAt(start-1) : '\0';
        
	if (c == MINUS && prevChar == MINUS) {
	    return super.replace(start, end, "");
	}
        
	if (isOperator(c)) {
	    while (isOperator(prevChar) && 
		   (c != MINUS || prevChar == '+')) {
		--start;
		prevChar = start > 0 ? charAt(start-1) : '\0';
	    }
	}
        
	//don't allow leading operator + * /
	if (start == 0 && isOperator(c)) { // && c != MINUS
	    return super.replace(start, end, "ans" + c);
	}

        //allow at most one '='
        if (c == '=') {
            for (int pos = 0; pos < start; ++pos) {
                if (charAt(pos) == '=') {
                    return super.replace(start, end, "");
                }
            }
        }
	return super.replace(start, end, "" + c);
    }
}
