package com.flynneffectmusic;

import com.flynneffectmusic.DMXOPCAdapter.CopyAdapter;
import com.flynneffectmusic.DMXOPCAdapter.GradientAdapter;
import com.flynneffectmusic.DMXOPCAdapter.IDMXOPCAdapter;
import com.flynneffectmusic.DMXOPCAdapter.StraightAdapter;
import com.flynneffectmusic.Listener.ArtNetListener;
import com.flynneffectmusic.Listener.IListener;
import com.flynneffectmusic.Listener.SACNListener;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main
{
    static String settingsFile = "settings.xml";
    static int updatesPerSecond = 0;

    static Timer timer;

    static int dmxAddress = 1;

    static short universeID = (short)0;

    static String destination = "localhost";
    static int channel = 0;
    static int pixelCount = 64;

    static int dmxLength = 3;

    static IDMXOPCAdapter adapter;
    static IListener listener;

    public static void main(String[] args)
    {
        readXML();

        OPCTransmitter transmitter = new OPCTransmitter(destination, channel);

        byte[] dmx = null;
        byte[] pixelData;

        setupTimer();

        while(true)
        {
            if(listener.read())
            {
                updatesPerSecond++;
                dmx = listener.getDMX(dmxAddress, dmxLength);
                pixelData = adapter.adaptDMX(dmx);

                transmitter.SendPixels(pixelData);
            }
        }
    }

    public static void readXML()
    {
        SAXBuilder builder = new SAXBuilder();
        try
        {
            Document settingsDoc = builder.build(settingsFile);
            Element rootElement = settingsDoc.getRootElement();
            dmxAddress = Integer.parseInt(rootElement.getChild("dmxAddress").getValue()) - 1;
            destination = rootElement.getChild("opcDestination").getValue();
            channel = Integer.parseInt(rootElement.getChild("opcChannel").getValue());
            pixelCount = Integer.parseInt(rootElement.getChild("opcPixelCount").getValue());

            switch(rootElement.getChild("adapterMethod").getValue())
            {
                default:
                case "copy":
                    adapter = new CopyAdapter(pixelCount);
                    dmxLength = IDMXOPCAdapter.PIXEL_LENGTH;
                    break;
                case "straight":
                    adapter = new StraightAdapter(pixelCount);
                    dmxLength = IDMXOPCAdapter.PIXEL_LENGTH * pixelCount;
                    break;
                case "gradient":
                    adapter = new GradientAdapter(pixelCount);
                    dmxLength = IDMXOPCAdapter.PIXEL_LENGTH * 2;
                    break;
            }

            switch(rootElement.getChild("listener").getAttributeValue("type"))
            {
                case "artnet":
                    listener = new ArtNetListener(Short.parseShort(rootElement.getChild("dmxAddress").getValue()));
                    break;
                case "sacn":
                    String listenAddress = rootElement.getChild("listener").getAttributeValue("listenAddress");
                    listener = new SACNListener(Short.parseShort(rootElement.getChild("dmxAddress").getValue()), listenAddress);
                    break;
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void setupTimer()
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("Updates per second: " + updatesPerSecond);
                updatesPerSecond = 0;
            }
        };

        timer = new Timer("Update output timer", true);
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

}
