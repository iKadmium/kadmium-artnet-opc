package com.flynneffectmusic.WebUI.ContextHandlers.Special;

import com.flynneffectmusic.Main;
import com.flynneffectmusic.PixelRenderTarget;
import com.flynneffectmusic.WebUI.ContextHandlers.StandardContextHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import static j2html.TagCreator.*;
import net.freeutils.httpserver.HTTPServer;
import org.java_websocket.WebSocket;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by higginsonj on 3/06/2016.
 */
public class PreviewHandler extends StandardContextHandler implements PixelRenderTarget
{
	PreviewWebSocket socketServer;

	public PreviewHandler()
	{
		super("Preview");
		try
		{
			socketServer = new PreviewWebSocket(new InetSocketAddress(InetAddress.getLocalHost(), 7234));
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		Main.getInstance().getRenderTargets().add(this);
	}

	@Override
	public List<Tag> GetBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = new ArrayList<>();

		int xCount = Main.getInstance().getSettings().getPixelsX();
		int yCount = Main.getInstance().getSettings().getPixelsY();

		ContainerTag pixelDiv = div().withClass("col-md-6 col-md-offset-3").with(
				canvas().withId("pixelCanvas").attr("width", xCount + "" ).attr("height", yCount + "")
		);
		ContainerTag zoomedDiv = div().withClass("col-md-6").with(
				input().withId("zoomSlider").withType("range").attr("min", "0.1").attr("max", "100").withValue("1.0"),
				canvas().withId("zoomedCanvas").attr("width", xCount + "" ).attr("height", yCount + "")
		);
		tags.add(pixelDiv);
		tags.add(zoomedDiv);

		String serverAddress = socketServer.getAddress().getHostName() + ":" + socketServer.getPort();

		tags.add(script().withType("text/javascript").with(
				unsafeHtml("var websocketAddress = \"ws://" + serverAddress + "\";"),
				unsafeHtml("var xCount = " + Main.getInstance().getSettings().getPixelsX() + ";"),
				unsafeHtml("var yCount = " + Main.getInstance().getSettings().getPixelsY() + ";")
		));
		return tags;
	}

	@Override
	protected List<Tag> GetPostBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags =super.GetPostBodyContent(request);
		tags.add(script().withSrc("/assets/js/preview.js"));
		return tags;
	}

	@Override
	public void render(byte[] data)
	{
		for(WebSocket socket : socketServer.connections())
		{
			String str = new Gson().toJson(data);
			socket.send(str);
		}
	}
}
