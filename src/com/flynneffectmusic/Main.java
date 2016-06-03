package com.flynneffectmusic;

import com.flynneffectmusic.DMXOPCAdapter.DMXOPCAdapter;
import com.flynneffectmusic.Listener.DMXListener;
import com.flynneffectmusic.WebUI.WebServer;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main
{
	private static final int MAX_UPDATE_HISTORY_SIZE = 20;
	private static int FRAME_RATE = 60;
	private static int RENDER_DELAY_MS = 1000 / FRAME_RATE;
	private static int SECONDS_UNTIL_RELOAD = 15;

    static String settingsFile = "settings.xml";
    private Queue<Timestamped<Integer>> updateHistory;
	private int updatesPerSecond = 0;
	private int secondsSinceLastUpdate = 0;

	private ScheduledExecutorService executorService;

    private DMXOPCAdapter adapter;
	private DMXListener listener;
	private OPCTransmitter transmitter;
	private List<PixelRenderTarget> renderTargets;

	private WebServer webServer;

	private byte[] dmx;
	private byte[] pixelData;
	private Settings settings;

	private static Main instance;

	public static void main(String[] args)
    {
		instance = new Main();
        instance.loadSettings();
		instance.loop();
    }

	public void loop()
	{
		while (true)
		{
			if (listener.read())
			{
				updatesPerSecond++;
				secondsSinceLastUpdate = 0;
				dmx = listener.getDMX(settings.getDmxAddress(), adapter.getDMXLength(settings.getPixelCount()));
			}
		}
	}

	public void loadSettings()
	{
		settings = new Settings();
		settings.load();
		webServer = new WebServer(settings.getWebServerPort());
		setupTimer();
	}

	public Main()
	{
		dmx = new byte[512];
		updateHistory = new LinkedList<>();
		executorService = Executors.newScheduledThreadPool(2);
		renderTargets = new ArrayList<>();
	}

    public void setupTimer()
    {
		Runnable outputTask = () -> {
			updateHistory.add(new Timestamped<>(updatesPerSecond));
			while(updateHistory.size() > MAX_UPDATE_HISTORY_SIZE)
			{
				updateHistory.remove();
			}
			updatesPerSecond = 0;
			secondsSinceLastUpdate++;
			if(secondsSinceLastUpdate >= SECONDS_UNTIL_RELOAD)
			{
				listener.reload();
				secondsSinceLastUpdate = 0;
			}
		};

		executorService.scheduleAtFixedRate(outputTask, 1000, 1000, TimeUnit.MILLISECONDS);

		Runnable renderTask = () -> {
			pixelData = adapter.adaptDMX(dmx, settings.getPixelCount());
			for(PixelRenderTarget target : renderTargets)
			{
				target.render(pixelData);
			}
		};

        executorService.scheduleAtFixedRate(renderTask, RENDER_DELAY_MS, RENDER_DELAY_MS, TimeUnit.MILLISECONDS);
    }

	public Settings getSettings()
	{
		return settings;
	}
	public static Main getInstance()
	{
		return instance;
	}

	public DMXListener getListener()
	{
		return listener;
	}
	public void setListener(DMXListener listener)
	{
		this.listener = listener;
	}

	public DMXOPCAdapter getAdapter()
	{
		return adapter;
	}
	public void setAdapter(DMXOPCAdapter adapter)
	{
		this.adapter = adapter;
	}

	public OPCTransmitter getTransmitter()
	{
		return transmitter;
	}
	public void setTransmitter(OPCTransmitter transmitter)
	{
		this.transmitter = transmitter;
	}

	public Collection<Timestamped<Integer>> getUpdateHistory()
	{
		return updateHistory;
	}

	public List<PixelRenderTarget> getRenderTargets()
	{
		return renderTargets;
	}
}
