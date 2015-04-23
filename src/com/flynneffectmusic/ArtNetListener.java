package com.flynneffectmusic;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class ArtNetListener
{
    DatagramChannel channel;

    final int DMX_UNIVERSE_SIZE = 512;

    final int ARTNET_PORT = 6454;
    final int ARTNET_PACKET_SIZE = DMX_UNIVERSE_SIZE + 18;
    final int BUFFER_SIZE = 65536;

    ByteBuffer buffer;

    byte[] dmxSettings;

    short universe;

    public ArtNetListener(short universe)
    {
        this.universe = universe;
        try
        {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(ARTNET_PORT));
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

    public boolean read()
    {
        try
        {
            buffer.clear();
            SocketAddress address = channel.receive(buffer);
            ArtNetPacket packet = new ArtNetPacket(buffer);
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
