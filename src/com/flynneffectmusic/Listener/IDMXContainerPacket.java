package com.flynneffectmusic.Listener;

import java.nio.ByteBuffer;

/**
 * Created by higginsonj on 23/04/2015.
 */
public interface IDMXContainerPacket
{
    public void parse(ByteBuffer buffer);
    public byte[] getDmx();
    public short getUniverse();
}
