package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:43 PM
 */
public interface MathEntityDao<T extends MathPersistenceEntity> {

	void save(@NotNull MathEntityPersistenceContainer<T> container);

	@Nullable
	MathEntityPersistenceContainer<T> load();

	@Nullable
	String getDescription(@NotNull String descriptionId);
}
