package org.solovyev.android.calculator;

import android.os.Bundle;
import android.webkit.WebView;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 6:26 PM
 */
public final class CalculatorWikiActivity extends CalculatorFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_wiki_page);

		final WebView webView = (WebView) findViewById(R.id.cpp_wiki_webview);
		//webView.loadUrl("file:///android_asset/wiki/functions/sin/index.html");
	}
}
