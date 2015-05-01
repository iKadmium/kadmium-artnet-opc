package com.flynneffectmusic.Listener;

import org.jdom2.Element;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class SACNListener extends DMXListener
{
    static final int SACN_PORT = 5568;
    static final byte MULTICAST_BYTE_1 = (byte)239;
    static final byte MULTICAST_BYTE_2 = (byte)255;

    MembershipKey multicastKey;

    public SACNListener(short universe, String listenAddress)
    {
        super(universe);
        this.listenAddress = listenAddress;
        try
        {
            channel = DatagramChannel
                .open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(SACN_PORT));

            multicastKey = joinMulticastGroup(universe, listenAddress);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private MembershipKey joinMulticastGroup(short universe, String listenAddress) throws IOException
    {
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(listenAddress));
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        return channel.join(getMulticastAddress(universe), networkInterface);

    }

    public boolean read()
    {
        return getPacket(new SACNPacket());
    }

    private InetAddress getMulticastAddress(short universe) throws UnknownHostException
    {
        ByteBuffer bytes = ByteBuffer.allocate(4);
        bytes.put(MULTICAST_BYTE_1);
        bytes.put(MULTICAST_BYTE_2);
        bytes.putShort(universe);
        return InetAddress.getByAddress(bytes.array());
    }

    public Element serialize()
    {
        Element element = new Element("listener");
        element.setAttribute("type", "sacn");
        element.setAttribute("universe", universe + "");
        element.setAttribute("listenAddress", listenAddress);
        return element;
    }

    private void rejoinMulticastGroup(short universe, String listenAddress)
    {
        if(multicastKey != null)
        {
            multicastKey.drop();
        }
        try
        {
            multicastKey = joinMulticastGroup(universe, listenAddress);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setListenAddress(String listenAddress)
    {
        super.setListenAddress(listenAddress);
        rejoinMulticastGroup(universe, listenAddress);
    }

    @Override
    public void setUniverse(short universe)
    {
        super.setUniverse(universe);
        rejoinMulticastGroup(universe, listenAddress);
    }

}
