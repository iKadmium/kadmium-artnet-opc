package com.flynneffectmusic;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class OPCClient
    extends WebSocketClient
{
    boolean connecting = false;
    OPCTransmitter transmitter;

    public OPCClient(URI address, OPCTransmitter transmitter)
    {
        super(address);
        this.transmitter = transmitter;
        connect();
        connecting = true;
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2)
    {
        connecting = false;
    }

    @Override
    public void onError(Exception e)
    {
        connecting = false;
    }

    @Override
    public void onMessage(String message)
    {
        System.out.println(message);
    }

    @Override
    public void onOpen(ServerHandshake shake)
    {
        connecting = false;
    }

    public boolean IsConnecting()
    {
        return connecting;
    }
}
