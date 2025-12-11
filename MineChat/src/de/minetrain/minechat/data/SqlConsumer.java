package de.minetrain.minechat.data;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t the input argument
	 */
	void accept(T t) throws SQLException;
}
