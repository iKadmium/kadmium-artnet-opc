package com.flynneffectmusic.Listener;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class SACNListener extends GenericListener implements IListener
{
    static final int SACN_PORT = 5568;
    static final byte MULTICAST_BYTE_1 = (byte)239;
    static final byte MULTICAST_BYTE_2 = (byte)255;

    MembershipKey multicastKey;

    public SACNListener(short universe, String listenAddress)
    {
        super(universe);
        try
        {
            channel = DatagramChannel
                        .open(StandardProtocolFamily.INET)
                        .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                        .bind(new InetSocketAddress(SACN_PORT));

            if(listenAddress != null)
            {

                for(NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces()) )
                {
                    System.out.println(networkInterface.getName() + "(" + networkInterface.getDisplayName() + "):");
                    for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses()))
                    {
                        System.out.println("\t" + inetAddress.getHostAddress());
                    }
                }

                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(listenAddress));
                channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
                channel.join(getMulticastAddress(), networkInterface);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean read()
    {
        return getPacket(new SACNPacket());
    }

    private InetAddress getMulticastAddress() throws UnknownHostException
    {
        ByteBuffer bytes = ByteBuffer.allocate(4);
        bytes.put(MULTICAST_BYTE_1);
        bytes.put(MULTICAST_BYTE_2);
        bytes.putShort(universe);
        return InetAddress.getByAddress(bytes.array());
    }
}
