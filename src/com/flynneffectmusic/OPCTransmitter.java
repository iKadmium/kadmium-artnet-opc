package com.flynneffectmusic;

import org.jdom2.Element;

import java.net.URI;
import java.net.URISyntaxException;

public class OPCTransmitter implements PixelRenderTarget
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

	public String getAddress()
	{
		return address.getHost();
	}

	public void render(byte[] data)
	{
        if(!client.IsConnecting() && (client == null || !client.getConnection().isOpen()))
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
            if (client.getConnection().isOpen())
            {
                if (!client.getConnection().hasBufferedData())
                {
                    client.send(newData);
                }
            }
        }
	}
	
	public int getChannel()
	{
		return channel;
	}

    @Override
    public String toString()
    {
        return "OPC Transmitter - " + client.getURI().getHost();
    }

    public void setChannel(int channel)
    {
        this.channel = channel;
    }

    public Element serialize()
    {
        Element element = new Element("opcTransmitter");
        element.setAttribute("destination", getAddress());
        element.setAttribute("channel", getChannel() + "");
        return element;
    }

    public static OPCTransmitter deserialize(Element opcTransmitter)
    {
        int channel = Integer.parseInt(opcTransmitter.getAttributeValue("channel"));
        String destination = opcTransmitter.getAttributeValue("destination");
        return new OPCTransmitter(destination, channel);
    }
}
