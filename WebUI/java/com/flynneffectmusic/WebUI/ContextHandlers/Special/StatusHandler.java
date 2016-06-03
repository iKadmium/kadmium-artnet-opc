package com.flynneffectmusic.WebUI.ContextHandlers.Special;

import com.flynneffectmusic.Main;
import com.flynneffectmusic.Timestamped;
import com.flynneffectmusic.WebUI.ContextHandlers.StandardContextHandler;
import com.google.common.primitives.UnsignedLongs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import net.freeutils.httpserver.HTTPServer;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

/**
 * Created by higginsonj on 3/06/2016.
 */
public class StatusHandler extends StandardContextHandler
{
	public StatusHandler()
	{
		super("Status");
	}

	@Override
	protected List<Tag> GetPostBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = super.GetPostBodyContent(request);
		tags.add(script().withSrc("/assets/js/Chart.js"));
		tags.add(script().withType("text/javascript").with(
			unsafeHtml(
					"var ctx = document.getElementById(\"myChart\");"
			),
			unsafeHtml(
					"var chartObj = new Chart(ctx, chartData);"
			)
		));
		return tags;
	}

	@Override
	public List<Tag> GetBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = new ArrayList<>();
		ContainerTag row = div().withClass("col-md-6 col-md-offset-3").with(
				canvas().withId("myChart").attr("width", "400").attr("height","400")
		);
		ContainerTag scriptTag = script().withType("text/javascript").with(
				unsafeHtml("var chartData = " + getHistoryObject().toString() + ";")
		);

		tags.add(row);
		tags.add(scriptTag);
		return tags;
	}

	private JsonObject getHistoryObject()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "line");

		JsonObject data = new JsonObject();

		JsonArray labels = new JsonArray();

		JsonArray datasets = new JsonArray();
		JsonObject dataset = new JsonObject();

		dataset.addProperty("label", "Updates per second");
		//dataset.addProperty("xAxisID", "Time");
		//dataset.addProperty("yAxisID", "Updates");

		JsonArray datasetData = new JsonArray();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
		for(Timestamped<Integer> dataPoint : Main.getInstance().getUpdateHistory())
		{

			labels.add(formatter.format(dataPoint.getTimestamp()));
			datasetData.add(dataPoint.getValue());
		}

		dataset.add("data", datasetData);
		datasets.add(dataset);
		data.add("datasets", datasets);
		data.add("labels", labels);

		obj.add("data", data);
		return obj;
	}

	public JsonObject getHistoryOptions()
	{
		JsonObject options = new JsonObject();

		return options;
	}
}
