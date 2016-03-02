package org.solovyev.common.msg;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

class Utf8Control extends ResourceBundle.Control {

	@Nonnull
	private static final ResourceBundle.Control instance = new Utf8Control();

	@Nonnull
	public static ResourceBundle.Control getInstance() {
		return instance;
	}

	private Utf8Control() {
	}

	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
		final ResourceBundle result;

		// The below is a copy of the default implementation.
		final String bundleName = toBundleName(baseName, locale);
		final String resourceName = toResourceName(bundleName, "properties");

		InputStream stream = null;
		if (reload) {
			final URL url = loader.getResource(resourceName);
			if (url != null) {
				final URLConnection connection = url.openConnection();
				if (connection != null) {
					connection.setUseCaches(false);
					stream = connection.getInputStream();
				}
			}
		} else {
			stream = loader.getResourceAsStream(resourceName);
		}

		if (stream != null) {
			try {
				// Only this line is changed to make it to read properties files as UTF-8.
				result = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
			} finally {
				stream.close();
			}
		} else {
			result = null;
		}

		return result;
	}
}
