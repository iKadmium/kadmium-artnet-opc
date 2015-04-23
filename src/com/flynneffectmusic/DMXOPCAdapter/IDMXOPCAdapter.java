package com.flynneffectmusic.DMXOPCAdapter;

/**
 * Created by higginsonj on 23/04/2015.
 */
public interface IDMXOPCAdapter
{
    byte[] adaptDMX(byte[] dmx);
    static int PIXEL_LENGTH = 3;
}
