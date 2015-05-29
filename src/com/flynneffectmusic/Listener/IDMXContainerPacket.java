package com.flynneffectmusic.Listener;

import java.nio.ByteBuffer;

/**
 * Created by higginsonj on 23/04/2015.
 */
public interface IDMXContainerPacket
{
    void parse(ByteBuffer buffer);
    byte[] getDmx();
    short getUniverse();
}
