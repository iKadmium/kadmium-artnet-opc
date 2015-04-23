package com.flynneffectmusic.Listener;

import java.nio.ByteBuffer;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class ArtNetPacket implements IDMXContainerPacket
{
    byte[] identifier;
    short opCode;
    short version;
    byte sequence;
    byte physical;
    short universe;
    short length;

    byte[] dmx;

    public ArtNetPacket()
    {

    }

    public void parse(ByteBuffer buffer)
    {
        buffer.flip();
        identifier = new byte[8];
        buffer.get(identifier, 0, 8);
        String id = new String(identifier);
        opCode = buffer.getShort();
        version = buffer.getShort();
        sequence = buffer.get();
        physical = buffer.get();
        universe = buffer.getShort();
        length = buffer.getShort();
        dmx = new byte[length];
        buffer.get(dmx, 0, length);
        buffer.flip();
    }

    public byte[] getDmx()
    {
        return dmx;
    }

    public short getUniverse()
    {
        return universe;
    }
}
