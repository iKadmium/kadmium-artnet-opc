package com.flynneffectmusic;

import com.flynneffectmusic.DMXOPCAdapter.DMXOPCAdapter;
import com.flynneffectmusic.Listener.DMXListener;

import java.util.*;

public class Main
{
    static String settingsFile = "settings.xml";
    static int updatesPerSecond = 0;

    static Timer timer;

    static DMXOPCAdapter adapter;
    static DMXListener listener;
    static OPCTransmitter transmitter;

    static WebServer webServer;

    public static void main(String[] args)
    {
        Settings.load();

        webServer = new WebServer(Settings.getWebServerPort());

        byte[] dmx;
        byte[] pixelData;

        setupTimer();

        while (true)
        {
            if (listener.read())
            {
                updatesPerSecond++;
                dmx = listener.getDMX(Settings.getDmxAddress() - 1, adapter.getDMXLength(Settings.getPixelCount()));
                pixelData = adapter.adaptDMX(dmx, Settings.getPixelCount());

                transmitter.SendPixels(pixelData);
            }
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
