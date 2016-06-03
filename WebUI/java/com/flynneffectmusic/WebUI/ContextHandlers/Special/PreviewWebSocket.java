package com.flynneffectmusic.WebUI.ContextHandlers.Special;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by higginsonj on 3/06/2016.
 */
public class PreviewWebSocket extends WebSocketServer
{
	public PreviewWebSocket(InetSocketAddress address) throws UnknownHostException
	{
		super(address);
		start();
	}

	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
	{

	}

	@Override
	public void onClose(WebSocket webSocket, int i, String s, boolean b)
	{

	}

	@Override
	public void onMessage(WebSocket webSocket, String s)
	{

	}

	@Override
	public void onError(WebSocket webSocket, Exception e)
	{

	}
}
