package com.flynneffectmusic.WebUI.FunctionalInterfaces;

/**
 * Created by higginsonj on 5/05/2016.
 */
@FunctionalInterface
public interface SetterProvider<T>
{
    void accept(T item, String property) throws Exception;
}
