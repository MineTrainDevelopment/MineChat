package de.minetrain.minechat.gui.utils;

import com.sun.javafx.util.Utils;

import javafx.beans.NamedArg;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.LongStringConverter;

public class LongSpinnerValueFactory extends SpinnerValueFactory<Long> {

	public LongSpinnerValueFactory(@NamedArg("min") long min, @NamedArg("max") long max) {
		this(min, max, min);
	}

	public LongSpinnerValueFactory(@NamedArg("min") long min, @NamedArg("max") long max,
			@NamedArg("initialValue") long initialValue) {
		this(min, max, initialValue, 1L);
	}

	public LongSpinnerValueFactory(@NamedArg("min") long min, @NamedArg("max") long max,
			@NamedArg("initialValue") long initialValue, @NamedArg("amountToStepBy") long amountToStepBy) {
		setMin(min);
		setMax(max);
		setAmountToStepBy(amountToStepBy);
		setConverter(new LongStringConverter());

		valueProperty().addListener((o, oldValue, newValue) -> {
			if (newValue == null) {
				return;
			}

			if (newValue < getMin()) {
				setValue(getMin());
			} else if (newValue > getMax()) {
				setValue(getMax());
			}
		});
		setValue(initialValue >= min && initialValue <= max ? initialValue : min);
	}

	private LongProperty min = new SimpleLongProperty(this, "min") {
		@Override
		protected void invalidated() {
			Long currentValue = LongSpinnerValueFactory.this.getValue();
			if (currentValue == null) {
				return;
			}

			long newMin = get();
			if (newMin > getMax()) {
				setMin(getMax());
				return;
			}

			if (currentValue < newMin) {
				LongSpinnerValueFactory.this.setValue(newMin);
			}
		}
	};

	public final void setMin(long value) {
		min.set(value);
	}

	public final long getMin() {
		return min.get();
	}

	public final LongProperty minProperty() {
		return min;
	}

	private LongProperty max = new SimpleLongProperty(this, "max") {
		@Override
		protected void invalidated() {
			Long currentValue = LongSpinnerValueFactory.this.getValue();
			if (currentValue == null) {
				return;
			}

			long newMax = get();
			if (newMax < getMin()) {
				setMax(getMin());
				return;
			}

			if (currentValue > newMax) {
				LongSpinnerValueFactory.this.setValue(newMax);
			}
		}
	};

	public final void setMax(long value) {
		max.set(value);
	}

	public final long getMax() {
		return max.get();
	}

	public final LongProperty maxProperty() {
		return max;
	}

	private LongProperty amountToStepBy = new SimpleLongProperty(this, "amountToStepBy");

	public final void setAmountToStepBy(long value) {
		amountToStepBy.set(value);
	}

	public final long getAmountToStepBy() {
		return amountToStepBy.get();
	}

	public final LongProperty amountToStepByProperty() {
		return amountToStepBy;
	}

	@Override
	public void decrement(int steps) {
		final long min = getMin();
		final long max = getMax();
		final long newIndex = getValue() - steps * getAmountToStepBy();
		setValue(isWrapAround() ? wrapValue(newIndex, min, max) : Utils.clamp(min, newIndex, max));
	}

	@Override
	public void increment(int steps) {
		final long min = getMin();
		final long max = getMax();
		final long currentValue = getValue();
		final long newIndex = currentValue + steps * getAmountToStepBy();
		setValue(isWrapAround() ? wrapValue(newIndex, min, max) : Utils.clamp(min, newIndex, max));
	}

	static long wrapValue(long value, long min, long max) {
		long span = max - min + 1;

		if (value < 0) {
			value = max + value % span + 1;
		}

		return min + (value - min) % span;
	}
}
