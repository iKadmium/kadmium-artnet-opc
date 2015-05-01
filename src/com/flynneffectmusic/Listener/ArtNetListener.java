package com.flynneffectmusic.Listener;


import org.jdom2.Element;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class ArtNetListener extends DMXListener
{
    static final int ARTNET_PORT = 6454;

    public ArtNetListener(short universe)
    {
       super(universe);
        try
        {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(ARTNET_PORT));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean read()
    {
        return getPacket(new ArtNetPacket());
    }

    public Element serialize()
    {
        Element element = new Element("listener");
        element.setAttribute("type", "artnet");
        element.setAttribute("universe", universe + "");
        return element;
    }

}
