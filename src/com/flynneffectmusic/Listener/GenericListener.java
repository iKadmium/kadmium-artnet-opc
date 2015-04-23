package com.flynneffectmusic.Listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by higginsonj on 23/04/2015.
 */
public abstract class GenericListener
{
    final int BUFFER_SIZE = 65536;
    final int DMX_UNIVERSE_SIZE = 512;

    protected short universe;
    protected DatagramChannel channel;

    protected ByteBuffer buffer;

    protected byte[] dmxSettings;


    public GenericListener(short universe, int port)
    {
        this.universe = universe;
        try
        {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(port));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        dmxSettings = new byte[DMX_UNIVERSE_SIZE];
        for(int i = 0; i < DMX_UNIVERSE_SIZE; i++)
        {
            dmxSettings[i] = 0;
        }
    }

    public byte[] getDMX(int start, int length)
    {
        byte[] returnVal = new byte[length];
        for(int i = 0; i < length; i++)
        {
            returnVal[i] = dmxSettings[i + start];
        }
        return returnVal;
    }

    protected boolean getPacket(IDMXContainerPacket packet)
    {
        try
        {
            buffer.clear();
            SocketAddress address = channel.receive(buffer);
            packet.parse(buffer);
            if(packet.getUniverse() == universe)
            {
                System.arraycopy(packet.getDmx(), 0, dmxSettings, 0, packet.getDmx().length);
            }
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
