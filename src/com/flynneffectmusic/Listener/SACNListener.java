package com.flynneffectmusic.Listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class SACNListener extends GenericListener implements IListener
{
    static final int SACN_PORT = 5568;

    public SACNListener(short universe)
    {
        super(universe, SACN_PORT);
    }

    @Override
    public boolean read()
    {
        return getPacket(new SACNPacket());
    }
}
