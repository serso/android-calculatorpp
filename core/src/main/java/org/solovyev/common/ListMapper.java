package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.CollectionTransformations;
import org.solovyev.common.text.Mapper;

import java.util.List;

public class ListMapper<T> implements Mapper<List<T>> {

	@NotNull
	private final Mapper<T> nestedMapper;

	private ListMapper(@NotNull Mapper<T> nestedMapper) {
		this.nestedMapper = nestedMapper;
	}

	@NotNull
	private static <T> Mapper<List<T>> newInstance(@NotNull Mapper<T> nestedMapper) {
		return new ListMapper<T>(nestedMapper);
	}

	@Nullable
	@Override
	public String formatValue(@Nullable List<T> value) throws IllegalArgumentException {
		return CollectionTransformations.formatValue(value, ";", nestedMapper);
	}

	@Nullable
	@Override
	public List<T> parseValue(@Nullable String value) throws IllegalArgumentException {
		return CollectionTransformations.split(value, ";", nestedMapper);
	}
}
