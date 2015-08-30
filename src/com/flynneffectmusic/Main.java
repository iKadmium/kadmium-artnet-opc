package com.flynneffectmusic;

import com.flynneffectmusic.DMXOPCAdapter.DMXOPCAdapter;
import com.flynneffectmusic.Listener.DMXListener;

import java.util.*;

public class Main
{
    static int FRAME_RATE = 60;
    static int RENDER_DELAY_MS = 1000 / FRAME_RATE;
    static int SECONDS_UNTIL_RELOAD = 15;

    static String settingsFile = "settings.xml";
    static int updatesPerSecond = 0;
    static int secondsSinceLastUpdate = 0;


    static Timer outputTimer;
    static Timer renderTimer;

    static DMXOPCAdapter adapter;
    static DMXListener listener;
    static OPCTransmitter transmitter;

    static WebServer webServer;

    static byte[] dmx;
    static byte[] pixelData;

    public static void main(String[] args)
    {
        Settings.load();

        webServer = new WebServer(Settings.getWebServerPort());

        setupTimer();
        dmx = new byte[512];

        while (true)
        {
            if (listener.read())
            {
                updatesPerSecond++;
				secondsSinceLastUpdate = 0;
                dmx = listener.getDMX(Settings.getDmxAddress() - 1, adapter.getDMXLength(Settings.getPixelCount()));
            }
        }
    }

    public static void setupTimer()
    {
        TimerTask outputTask = new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("Updates per second: " + updatesPerSecond);
                updatesPerSecond = 0;
                secondsSinceLastUpdate++;
                if(secondsSinceLastUpdate >= SECONDS_UNTIL_RELOAD)
                {
					listener.reload();
					secondsSinceLastUpdate = 0;
                }
            }
        };

        outputTimer = new Timer("Update output timer", true);
        outputTimer.scheduleAtFixedRate(outputTask, 1000, 1000);

        TimerTask renderTask = new TimerTask()
        {
            @Override
            public void run()
            {
                pixelData = adapter.adaptDMX(dmx, Settings.getPixelCount());
                transmitter.SendPixels(pixelData);
            }
        };

        renderTimer = new Timer("Render", true);
        renderTimer.scheduleAtFixedRate(renderTask, RENDER_DELAY_MS, RENDER_DELAY_MS);
    }

}
