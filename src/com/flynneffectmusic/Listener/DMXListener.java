package com.flynneffectmusic.Listener;

import org.jdom2.Element;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by higginsonj on 23/04/2015.
 */
public abstract class DMXListener
{
    final int BUFFER_SIZE = 65536;
    final int DMX_UNIVERSE_SIZE = 512;

    protected short universe;
    protected DatagramChannel channel;

    public String getListenAdapter()
    {
        return listenAdapter;
    }

    public void setListenAdapter(String listenAddress)
    {
        this.listenAdapter = listenAddress;
    }

    String listenAdapter;

    protected ByteBuffer buffer;

    protected byte[] dmxSettings;

    public abstract boolean read();

    public DMXListener(short universe)
    {
        this.universe = universe;

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

        System.arraycopy(dmxSettings, start, returnVal, 0, length);
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
            //e.printStackTrace();
        }
        return false;
    }

    public void setUniverse(short universe)
    {
        this.universe = universe;
    }

    public short getUniverse()
    {
        return universe;
    }

    public static DMXListener deserialize(Element element)
    {
        String adapter = element.getAttributeValue("listenAdapter");
        short universe = Short.parseShort(element.getAttributeValue("universe") );
        switch(element.getAttributeValue("type"))
        {
            default:
            case "sacn":
                return new SACNListener(universe, adapter);
            case "artnet":
                return new ArtNetListener(universe);
        }
    }

    public abstract Element serialize();

    public abstract void reload();

	public abstract void close();

	public abstract String getTypeString();

	public static DMXListener create(String type, short universe)
	{
		switch(type)
		{
			default:
			case "sacn":
				return new SACNListener(universe, "auto");
			case "artnet":
				return new ArtNetListener(universe);
		}
	}
}
