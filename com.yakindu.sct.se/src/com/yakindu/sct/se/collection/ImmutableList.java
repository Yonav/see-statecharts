package com.yakindu.sct.se.collection;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Stores objects in an immutable way. Is mainly used for nodes
 * 
 * @author jwielage
 */
public class ImmutableList<T> {
	private final T value;

	private final ImmutableList<T> next;

	public static final ImmutableList<?> EMPTY = new ImmutableList<Object>(null, null);

	public T getValue() {
		return value;
	}

	public ImmutableList<T> getNext() {
		return next;
	}

	private ImmutableList(T value, ImmutableList<T> next) {
		super();
		this.value = value;
		this.next = next;
	}

	public ImmutableList<T> prepend(T value) {
		if (value == null) {
			return this;
		}
		return new ImmutableList<T>(value, this);
	}

	public ImmutableList<T> prepend(T... values) {
		ImmutableList<T> result = this;
		for (T value : values) {
			result = result.prepend(value);
		}
		return result;
	}

	public ImmutableList<T> prependReverse(T... values) {
		ImmutableList<T> result = this;
		for (int i = values.length - 1; i >= 0; i--) {
			result = result.prepend(values[i]);
		}
		return result;
	}

	public static <T> ImmutableList<T> create(@SuppressWarnings("unchecked") T... values) {
		ImmutableList<T> result = new ImmutableList<T>(null, null);
		if (values != null) {
			for (T value : values) {

				result = result.prepend(value);
			}
		}

		return result;
	}

	public static <T> ImmutableList<T> createReverse(@SuppressWarnings("unchecked") T... values) {
		ImmutableList<T> result = new ImmutableList<T>(null, null);
		if (values != null) {
			for (int i = values.length - 1; i >= 0; i--) {
				result = result.prepend(values[i]);
			}
		}

		return result;
	}

	public boolean isEmpty() {
		return value == null;
	}

	public void consumeUnderlyingList(Consumer<T> action) {
		if (isEmpty()) {
			return;
		}
		action.accept(value);
		next.consumeUnderlyingList(action);
	}

	public T consumeUnderlyingList(Function<T, Boolean> action) {
		if (isEmpty()) {
			return null;
		}
		if (action.apply(value)) {
			return value;
		}
		return next.consumeUnderlyingList(action);
	}

	public void consumeValuesUntilEqual(ImmutableList<T> searchTillFound, Consumer<T> action) {
		if (isEmpty() || this.equals(searchTillFound)) {
			return;
		}
		action.accept(value);
		next.consumeValuesUntilEqual(searchTillFound, action);
	}

	public boolean contains(T pContainedValue) {
		return consumeUnderlyingList(v -> {
			return v.equals(pContainedValue);
		}) != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		consumeUnderlyingList(v -> {
			sb.append(" -> ");
			sb.append(v);
		});
		return sb.toString();
	}
}
