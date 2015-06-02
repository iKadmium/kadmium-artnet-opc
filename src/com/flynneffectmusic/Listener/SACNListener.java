package com.flynneffectmusic.Listener;

import org.jdom2.Element;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class SACNListener extends DMXListener
{
    static final int SACN_PORT = 5568;
    static final byte MULTICAST_BYTE_1 = (byte)239;
    static final byte MULTICAST_BYTE_2 = (byte)255;

    MembershipKey multicastKey;

    public SACNListener(short universe, String listenAdapter)
    {
        super(universe);
        this.listenAdapter = listenAdapter;
        try
        {
            InetSocketAddress address = new InetSocketAddress(SACN_PORT);
            channel = DatagramChannel
                .open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(address);

            multicastKey = joinMulticastGroup(universe, listenAdapter);
        }
        catch (IOException|IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    private MembershipKey joinMulticastGroup(short universe, String listenAdapter) throws IOException
    {
        NetworkInterface networkInterface = null;
        if(listenAdapter.equals("auto"))
        {
            networkInterface = GetExternalNetworkInterface();
        }
        else
        {
            networkInterface = NetworkInterface.getByName(listenAdapter);
        }

        System.out.println("Using network interface " + networkInterface);
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
        element.setAttribute("listenAdapter", listenAdapter);
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
    public void setListenAdapter(String listenAdapter)
    {
        super.setListenAdapter(listenAdapter);
        rejoinMulticastGroup(universe, listenAdapter);
    }

    @Override
    public void setUniverse(short universe)
    {
        super.setUniverse(universe);
        rejoinMulticastGroup(universe, listenAdapter);
    }

    private NetworkInterface GetExternalNetworkInterface()
    {
        try
        {
            // iterate over the network interfaces known to java
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            OUTER:
            for (NetworkInterface interface_ : Collections.list(interfaces))
            {
                // we shouldn't care about loopback addresses
                if (interface_.isLoopback())
                    continue;

                // if you don't expect the interface to be up you can skip this
                // though it would question the usability of the rest of the code
                if (!interface_.isUp())
                    continue;

                // iterate over the addresses associated with the interface
                Enumeration<InetAddress> addresses = interface_.getInetAddresses();
                for (InetAddress address : Collections.list(addresses))
                {
                    // look only for ipv4 addresses
                    if (address instanceof Inet6Address)
                        continue;

                    // use a timeout big enough for your needs
                    if (!address.isReachable(3000))
                        continue;

                    return interface_;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
