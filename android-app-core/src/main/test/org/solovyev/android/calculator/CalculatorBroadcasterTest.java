package org.solovyev.android.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import javax.annotation.Nonnull;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Robolectric.application;
import static org.solovyev.android.calculator.CalculatorBroadcaster.ACTION_DISPLAY_STATE_CHANGED;
import static org.solovyev.android.calculator.CalculatorBroadcaster.ACTION_EDITOR_STATE_CHANGED;
import static org.solovyev.android.calculator.CalculatorEventType.display_state_changed;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed_light;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class CalculatorBroadcasterTest {

	@Nonnull
	private CalculatorBroadcaster broadcaster;

	@Before
	public void setUp() throws Exception {
		broadcaster = new CalculatorBroadcaster(application);
	}

	@Test
	public void testShouldSendEditorStateChangedIntent() throws Exception {
		assertIntentSent(editor_state_changed, ACTION_EDITOR_STATE_CHANGED);
	}

	@Test
	public void testShouldSendEditorStateChangedLiteIntent() throws Exception {
		assertIntentSent(editor_state_changed_light, ACTION_EDITOR_STATE_CHANGED);
	}

	@Test
	public void testShouldSendDisplayStateChangedIntent() throws Exception {
		assertIntentSent(display_state_changed, ACTION_DISPLAY_STATE_CHANGED);
	}

	private void assertIntentSent(@Nonnull CalculatorEventType eventType, final String expectedAction) {
		final BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
		application.registerReceiver(receiver, new IntentFilter(expectedAction));
		broadcaster.onCalculatorEvent(CalculatorEventDataImpl.newInstance(1L, 1L), eventType, null);
		verify(receiver, times(1)).onReceive(Mockito.<Context>any(), argThat(new BaseMatcher<Intent>() {
			@Override
			public boolean matches(Object o) {
				return ((Intent) o).getAction().equals(expectedAction);
			}

			@Override
			public void describeTo(Description description) {
			}
		}));
	}
}
