package jscl.text.msg;

import org.junit.Test;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.ENGLISH;
import static jscl.text.msg.Messages.msg_1;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.solovyev.common.msg.MessageType.error;

/**
 * User: serso
 * Date: 11/30/11
 * Time: 9:53 PM
 */
public class JsclMessageTest {

    private static final Locale[] LOCALES = new Locale[]{
            Locale.ENGLISH, new Locale("ar"), new Locale("cs"), new Locale("de"), new Locale("es", "ES"), new Locale("fi"), new Locale("fr"), new Locale("it"), new Locale("ja"), new Locale("nl"), new Locale("pl"), new Locale("pt", "BR"), new Locale("pt", "PT"),
            new Locale("ru"), new Locale("uk"), new Locale("vi"), new Locale("zh", "CN"), new Locale("zh", "TW")
    };

    @Test
    public void testTranslation() throws Exception {
        String localizedMessage = new JsclMessage(msg_1, error).getLocalizedMessage(ENGLISH);
        assertTrue(localizedMessage.startsWith("Parsing error "));
    }

    @Test
    public void testShouldContainPolishStrings() throws Exception {
        String localizedMessage = new JsclMessage(msg_1, error).getLocalizedMessage(new Locale("pl", "PL"));
        assertTrue(localizedMessage.startsWith("Wystąpił błąd "));
    }

    @Test
    public void testAllMessages() throws Exception {
        for (int i = 0; i < Messages.COUNT; i++) {
            final String id = "msg_" + i;
            final List<String> arguments = makeMessageArguments(i);
            final JsclMessage message = new JsclMessage(id, MessageType.info, arguments);
            for (Locale locale : LOCALES) {
                final String text = message.getLocalizedMessage(locale);
                assertFalse(text.isEmpty());
                if (arguments.size() == 1) {
                    assertTrue(text.contains("param0"));
                } else if (arguments.size() == 2) {
                    assertTrue(text.contains("param1"));
                    assertTrue(text.contains("param2"));
                }
            }

        }

    }

    @Nonnull
    private List<String> makeMessageArguments(int i) {
        switch (i) {
            case 0:
            case 10:
            case 19:
                return Arrays.asList("param1", "param2");
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 8:
            case 11:
            case 12:
            case 13:
            case 14:
            case 17:
            case 20:
            case 21:
                return Arrays.asList("param0");
        }
        return Collections.emptyList();
    }
}
