package com.flynneffectmusic.Listener;

/**
 * Created by higginsonj on 23/04/2015.
 */
public interface IListener
{
    byte[] getDMX(int start, int length);
    public boolean read();
}
