package com.flynneffectmusic.Listener;

import java.nio.ByteBuffer;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class SACNPacket implements IDMXContainerPacket
{
    short universe;
    byte[] dmx;


    @Override
    public void parse(ByteBuffer buffer)
    {
        buffer.flip();

        short preamble = buffer.getShort();
        short flags = buffer.getShort();
        byte[] packetIdentifier = new byte[12];
        buffer.get(packetIdentifier);
        String identifier = new String(packetIdentifier);
        short flagsAndLength1 = buffer.getShort();
        int vector1 = buffer.getInt();
        byte[] cid = new byte[16];
        buffer.get(cid);

        short flagsAndLength2 = buffer.getShort();
        int vector2 = buffer.getInt();
        byte[] sourceName = new byte[64];
        buffer.get(sourceName);
        String sourceString = new String(sourceName);

        byte priority = buffer.get();
        short reserved = buffer.getShort();
        byte sequenceNumber = buffer.get();
        byte options = buffer.get();
        universe = buffer.getShort();

        short flagsAndLength3 = buffer.getShort();
        byte vector3 = buffer.get();
        byte addressTypeAndDataType = buffer.get();
        short firstPropertyAddress = buffer.getShort();
        short addressIncrement = buffer.getShort();
        short propertyValueCount = buffer.getShort();

        byte startCode = buffer.get();
        dmx = new byte[propertyValueCount - 1];
        buffer.get(dmx);
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
