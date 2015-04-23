package com.flynneffectmusic;

import java.net.URI;
import java.net.URISyntaxException;

public class OPCTransmitter
{
	final static boolean SET_LENGTH_BYTES = false;
	
	int channel;
	URI address;
	OPCClient client;

	public OPCTransmitter(String host, int channel)
	{
		this.channel = channel;
		String uri = "ws://" + host + ":7890";
		try
		{
			address = new URI(uri);
			client = new OPCClient(address, this);
		}
		catch (URISyntaxException e)
		{
            e.printStackTrace();
		}
	}
	
	public void close()
	{
		client.close();
	}

	public String GetAddress()
	{
		return address.toString();
	}

	public String GetType()
	{
		return "opc";
	}

	public void SendPixels(byte[] data)
	{
        if(!client.IsConnecting() && (client == null || !client.isOpen()))
        {
            client = new OPCClient(address, this);
        }
        else
        {
            byte command = 0;
            byte lengthHi = (byte) (data.length & 255);
            byte lengthLo = (byte) (data.length | 255);

            byte[] newData = new byte[data.length + 4];
            newData[0] = (byte) (channel & 255);
            newData[1] = command;
            newData[2] = SET_LENGTH_BYTES ? lengthHi : 0;
            newData[3] = SET_LENGTH_BYTES ? lengthLo : 0;

            System.arraycopy(data, 0, newData, 4, data.length);
            if (client.isOpen())
            {
                if (!client.hasBufferedData())
                {
                    client.send(newData);
                }
            }
        }
	}
	
	public int GetChannel()
	{
		return channel;
	}

    @Override
    public String toString()
    {
        return "OPC Transmitter - " + client.getURI().getHost();
    }
}
