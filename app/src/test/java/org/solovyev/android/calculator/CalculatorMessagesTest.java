package org.solovyev.android.calculator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.common.msg.MessageType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

@Config(constants = BuildConfig.class)
@RunWith(value = RobolectricTestRunner.class)
public class CalculatorMessagesTest {

    private static final Locale[] LOCALES = new Locale[]{
            Locale.ENGLISH, new Locale("ar"), new Locale("cs"), new Locale("de"), new Locale("es"), new Locale("es", "ES"), new Locale("fi"), new Locale("fr"), new Locale("it"), new Locale("ja"), new Locale("nl"), new Locale("pl"),
            new Locale("pt", "BR"), new Locale("pt", "PT"),
            new Locale("ru"), new Locale("uk"), new Locale("vi"), new Locale("zh"), new Locale("zh", "CN"), new Locale("zh", "TW")
    };

    private static final String[] MESSAGES = new String[]{CalculatorMessages.msg_001,
            CalculatorMessages.msg_002,
            CalculatorMessages.msg_003,
            CalculatorMessages.msg_004,
            CalculatorMessages.msg_005,
            CalculatorMessages.msg_006,
            CalculatorMessages.msg_007,
            CalculatorMessages.syntax_error,
            CalculatorMessages.result_copied,
            CalculatorMessages.text_copied,
            CalculatorMessages.ans_description};

    @Test
    public void testAllMessages() throws Exception {
        for (String id : MESSAGES) {
            final List<String> arguments = makeMessageArguments(id);
            final CalculatorMessage message = new CalculatorMessage(id, MessageType.info, arguments);
            for (Locale locale : LOCALES) {
                final String text = message.getLocalizedMessage(locale);
                assertFalse(text.isEmpty());
                final String errorMessage = "Message=" + id + ", locale=" + locale;
                if (arguments.size() == 1) {
                    assertTrue(errorMessage, text.contains("param0"));
                } else if (arguments.size() == 2) {
                    assertTrue(errorMessage, text.contains("param1"));
                    assertTrue(errorMessage, text.contains("param2"));
                }
            }

        }

    }

    @Nonnull
    private List<String> makeMessageArguments(@Nonnull String id) {
        switch (id) {
            case CalculatorMessages.msg_001:
            case CalculatorMessages.msg_005:
            case CalculatorMessages.msg_007:
                return Arrays.asList("param0");
        }
        return Collections.emptyList();
    }
}
