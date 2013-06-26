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

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.solovyev.common.text.Strings;

/**
 * User: serso
 * Date: 10/13/12
 * Time: 1:11 PM
 */
@RunWith(value = RobolectricTestRunner.class)
public class AndroidCalculatorEditorViewTest {

	@BeforeClass
	public static void staticSetUp() throws Exception {
		/*CalculatorTestUtils.staticSetUp(null);*/
	}

	@Before
	public void setUp() throws Exception {
/*        CalculatorActivity context = new CalculatorActivity();
		CalculatorTestUtils.initViews(context);*/
	}

	@Test
	public void testCreation() throws Exception {
		new AndroidCalculatorEditorView(CalculatorApplication.getInstance());
	}

	@Test
	public void testAsyncWork() throws Exception {
		final int threadNum = 10;
		final int count = 10;
		final int maxTextLength = 100;

		final Random random = new Random(new Date().getTime());
		final CountDownLatch startLatchLatch = new CountDownLatch(threadNum);
		final CountDownLatch finishLatch = new CountDownLatch(threadNum * count);
		final AtomicBoolean error = new AtomicBoolean(false);

		for (int i = 0; i < threadNum; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						startLatchLatch.await();
					} catch (InterruptedException e) {
						System.out.println(e);
						error.set(true);
						for (int j = 0; j < count; j++) {
							finishLatch.countDown();
						}
						return;
					}

					for (int j = 0; j < count; j++) {
						try {
							int textLength = random.nextInt(maxTextLength);
							Locator.getInstance().getEditor().insert(Strings.generateRandomString(textLength), textLength);
						} catch (Throwable e) {
							System.out.println(e);
							error.set(true);
						} finally {
							finishLatch.countDown();
						}
					}
				}
			}).start();
			startLatchLatch.countDown();
		}

		if (finishLatch.await(60, TimeUnit.SECONDS)) {
			Assert.assertFalse(error.get());
		} else {
			Assert.fail("Too long execution!");
		}
	}
}
