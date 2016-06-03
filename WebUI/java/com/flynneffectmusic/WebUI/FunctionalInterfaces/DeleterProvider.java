package com.flynneffectmusic.WebUI.FunctionalInterfaces;

import java.io.IOException;

/**
 * Created by higginsonj on 5/05/2016.
 */
@FunctionalInterface
public interface DeleterProvider<T>
{
	/**
	 * @param t The item to delete
	 * @return Whether the save function should be called after deletion
	 * @throws IOException
	 */
    boolean accept(T t) throws IOException;
}
