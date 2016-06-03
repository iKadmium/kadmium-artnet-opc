package com.flynneffectmusic.WebUI.ContextHandlers.Settings;

import com.flynneffectmusic.Listener.DMXListener;
import com.flynneffectmusic.Main;
import com.flynneffectmusic.OPCTransmitter;
import com.flynneffectmusic.Settings;
import com.flynneffectmusic.WebUI.ContextHandlers.EditableProperty;
import com.flynneffectmusic.WebUI.ContextHandlers.StandardEditHandler;
import com.flynneffectmusic.WebUI.NameDisplayPair;
import com.flynneffectmusic.WebUI.WebServer;


/**
 * Created by higginsonj on 3/06/2016.
 */
public class SettingsHandler extends StandardEditHandler<Settings>
{
	public SettingsHandler()
	{
		super("Settings",
				(params) ->
				{
					Settings settings = new Settings();
					settings.setDmxAddress(Short.parseShort(params.get("dmxAddress")));
					settings.setPixelsX(Integer.parseInt(params.get("pixelsX")));
					settings.setPixelsY(Integer.parseInt(params.get("pixelsY")));
					settings.setWebServerPort(Integer.parseInt(params.get("webServerPort")));
					return settings;
				},
				(settings) -> false,
				(name) -> Main.getInstance().getSettings(),
				Settings::save,
				new EditableProperty<>(
						"dmxAddress",
						"1",
						(name, defaultValue) -> WebServer.GetNumberInput("DMX Address", name, defaultValue, 1, 511),
						(settings) -> settings.getDmxAddress() + "",
						(settings, value) -> settings.setDmxAddress(Short.parseShort(value))
				),
				new EditableProperty<>(
						"pixelsX",
						"1",
						(name, defaultValue) -> WebServer.GetNumberInput("Pixel X Count:", name, defaultValue, 1, 65536),
						(settings) -> settings.getPixelsX() + "",
						(settings, value) -> settings.setPixelsX(Integer.parseInt(value))
				),
				new EditableProperty<>(
						"pixelsY",
						"1",
						(name, defaultValue) -> WebServer.GetNumberInput("Pixel Y Count:", name, defaultValue, 1, 65536),
						(settings) -> settings.getPixelsY() + "",
						(settings, value) -> settings.setPixelsY(Integer.parseInt(value))
				),
				new EditableProperty<>(
						"listenerType",
						"sacn",
						(name, defaultValue) -> WebServer.GetSelectBox(name, "Listener Type:", defaultValue,
								new NameDisplayPair("sacn", "sACN"),
								new NameDisplayPair("artnet", "ArtNet")
						),
						(settings) -> Main.getInstance().getListener().getTypeString(),
						(settings, value) ->
						{
							String adapterType = Main.getInstance().getListener().getTypeString();
							if(value != adapterType)
							{
								Main.getInstance().getListener().close();
								DMXListener listener = DMXListener.create(value, Main.getInstance().getListener().getUniverse());
								Main.getInstance().setListener(listener);
							}
						}
				),
				new EditableProperty<>(
						"transmitterDestination",
						"localhost",
						(name, defaultValue) -> WebServer.GetTextInput("Transmitter Destination:", name, defaultValue),
						(settings) -> Main.getInstance().getTransmitter().getAddress() ,
						(settings, value) ->
						{
							Main.getInstance().getTransmitter().close();
							OPCTransmitter transmitter = new OPCTransmitter(value, Main.getInstance().getTransmitter().getChannel());
							Main.getInstance().setTransmitter(transmitter);
						}
				),
				new EditableProperty<>(
						"transmitterOPCChannel",
						"0",
						(name, defaultValue) -> WebServer.GetNumberInput("Transmitter Channel:", name, defaultValue, 0, 255),
						(settings) -> Main.getInstance().getTransmitter().getChannel() + "" ,
						(settings, value) -> Main.getInstance().getTransmitter().setChannel(Integer.parseInt(value))
				),
				new EditableProperty<>(
						"startupWait",
						"0",
						(name, defaultValue) -> WebServer.GetNumberInput("Startup Wait Time:", name, defaultValue),
						(settings) -> Main.getInstance().getSettings().getStartupWait() + "" ,
						(settings, value) -> Main.getInstance().getSettings().setStartupWait(Integer.parseInt(value))
				)
		);
	}
}
