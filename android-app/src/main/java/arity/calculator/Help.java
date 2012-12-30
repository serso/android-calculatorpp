// Copyright (C) 2009 Mihai Preda

package arity.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Help extends Activity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        WebView view = new WebView(this);
        setContentView(view);
        view.loadUrl("file:///android_asset/help.html");
    }   
}
