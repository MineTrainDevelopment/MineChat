package de.minetrain.minechat.gui.utils;

import java.util.List;

import com.sun.javafx.collections.ObservableListWrapper;

public class NotifiableObservableListWrapper<E> extends ObservableListWrapper<E> {

	public NotifiableObservableListWrapper(List<E> list) {
		super(list);
	}

	public void notifyAdd(int index) {
		beginChange();
		nextAdd(index, index + 1);
		++modCount;
		endChange();
	}
}
