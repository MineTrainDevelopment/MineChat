package de.minetrain.minechat.gui.utils;

import javafx.beans.property.SimpleObjectProperty;

public class NotifiableObjectProperty<T> extends SimpleObjectProperty<T> {

	public NotifiableObjectProperty(Object bean, String name) {
		super(bean, name);
	}

	public void notifyChange() {
		invalidated();
		fireValueChangedEvent();
	}
}
