package com.flynneffectmusic.WebUI.FunctionalInterfaces;

import java.io.IOException;

/**
 * Created by higginsonj on 5/05/2016.
 */
@FunctionalInterface
public interface SaveProvider<T>
{
    void accept(T t) throws IOException;
}
