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
    private static short dmxAddress = 1;
    private static int pixelCount = 0;
    private static int webServerPort;

    public static void save()
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

        rootElement.getChildren().add(WebServer.createElement("dmxAddress", getDmxAddress() + ""));
        rootElement.getChildren().add(WebServer.createElement("httpServerPort", getWebServerPort() + ""));
        rootElement.getChildren().add(Main.listener.serialize());
        rootElement.getChildren().add(WebServer.createElement("pixelCount", getPixelCount() + ""));
        rootElement.getChildren().add(Main.adapter.serialize());
        rootElement.getChildren().add(Main.transmitter.serialize());


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

    public static void load()
    {
        SAXBuilder builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
        try
        {
            Document settingsDoc = builder.build(Main.settingsFile);
            Element rootElement = settingsDoc.getRootElement();

            setDmxAddress(Short.parseShort(rootElement.getChild("dmxAddress").getValue()));
            setPixelCount(Integer.parseInt(rootElement.getChild("pixelCount").getValue()));
            Main.transmitter = OPCTransmitter.deserialize(rootElement.getChild("opcTransmitter"));
            Main.adapter = DMXOPCAdapter.deserialize(rootElement.getChild("adapter"));
            Main.listener = DMXListener.deserialize(rootElement.getChild("listener"));
            setWebServerPort(Short.parseShort((rootElement.getChild("httpServerPort").getValue())));

        }
        catch (JDOMException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static short getDmxAddress()
    {
        return dmxAddress;
    }

    public static void setDmxAddress(short dmxAddress)
    {
        Settings.dmxAddress = dmxAddress;
    }

    public static int getPixelCount()
    {
        return pixelCount;
    }

    public static void setPixelCount(int pixelCount)
    {
        Settings.pixelCount = pixelCount;
    }

    public static int getWebServerPort()
    {
        return webServerPort;
    }

    public static void setWebServerPort(int webServerPort)
    {
        Settings.webServerPort = webServerPort;
    }
}
