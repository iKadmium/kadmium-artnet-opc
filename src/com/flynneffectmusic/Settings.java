package com.flynneffectmusic;

import com.flynneffectmusic.DMXOPCAdapter.DMXOPCAdapter;
import com.flynneffectmusic.Listener.DMXListener;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by higginsonj on 1/06/2015.
 */
public class Settings
{
    private short dmxAddress;
	private int pixelsX;
	private int pixelsY;
    private int webServerPort;
	private int startupWait;

	public Settings()
	{
		this.dmxAddress = 1;
		this.pixelsX = 1;
		this.pixelsY = 1;
		this.webServerPort = 8080;
		this.startupWait = 0;
	}

    public void save()
    {
        Element rootElement = new Element("settings");
        Document settingsDoc = new Document(rootElement);

        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.addNamespaceDeclaration(xsi);
        rootElement.setAttribute(
            new Attribute(
                "noNamespaceSchemaLocation",
                "settings.xsd",
                xsi
            )
        );

        rootElement.addContent(new Element("dmxAddress").addContent(getDmxAddress() + ""));
        rootElement.addContent(new Element("httpServerPort").addContent(getWebServerPort() + ""));
        rootElement.addContent(Main.getInstance().getListener().serialize());
        rootElement.addContent(new Element("pixelsX").addContent(getPixelsX() + ""));
		rootElement.addContent(new Element("pixelsY").addContent(getPixelsY() + ""));
        rootElement.addContent(Main.getInstance().getAdapter().serialize());
        rootElement.addContent(Main.getInstance().getTransmitter().serialize());
		rootElement.addContent(new Element("startupWait").addContent(startupWait + ""));

        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try
        {
            xmlOutputter.output(settingsDoc, new FileOutputStream(Main.settingsFile));
        }
        catch (IOException e)
        {
            System.err.println("Unable to write to settings file " + Main.settingsFile);
            e.printStackTrace();
        }

    }

    public void load()
    {
        SAXBuilder builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
        try
        {
            Document settingsDoc = builder.build(Main.settingsFile);
            Element rootElement = settingsDoc.getRootElement();

			startupWait = Integer.parseInt(rootElement.getChild("startupWait").getValue());
			System.out.println("waiting for " + startupWait + "s as specified...");
			Thread.sleep(startupWait * 1000);

			dmxAddress = (Short.parseShort(rootElement.getChild("dmxAddress").getValue()));
            pixelsX = (Integer.parseInt(rootElement.getChild("pixelsX").getValue()));
			pixelsY = (Integer.parseInt(rootElement.getChild("pixelsY").getValue()));
            Main.getInstance().setTransmitter(OPCTransmitter.deserialize(rootElement.getChild("opcTransmitter")));
            Main.getInstance().setAdapter(DMXOPCAdapter.deserialize(rootElement.getChild("adapter")));
            Main.getInstance().setListener(DMXListener.deserialize(rootElement.getChild("listener")));
            webServerPort = (Short.parseShort((rootElement.getChild("httpServerPort").getValue())));
        }
        catch (JDOMException | IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
	}

    public short getDmxAddress()
    {
        return dmxAddress;
    }
    public void setDmxAddress(short dmxAddress)
    {
        this.dmxAddress = dmxAddress;
    }

    public int getPixelCount()
    {
        return pixelsX * pixelsY;
    }

	public int getPixelsX()
	{
		return pixelsX;
	}
	public void setPixelsX(int pixelsX)
	{
		this.pixelsX = pixelsX;
	}

	public int getPixelsY()
	{
		return pixelsY;
	}
	public void setPixelsY(int pixelsY)
	{
		this.pixelsY = pixelsY;
	}

    public int getWebServerPort()
    {
        return webServerPort;
    }
    public void setWebServerPort(int webServerPort)
    {
		this.webServerPort = webServerPort;
    }

	public int getStartupWait()
	{
		return startupWait;
	}

	public void setStartupWait(int startupWait)
	{
		this.startupWait = startupWait;
	}
}
