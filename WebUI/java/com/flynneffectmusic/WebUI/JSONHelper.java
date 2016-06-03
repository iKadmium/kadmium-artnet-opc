package com.flynneffectmusic.WebUI;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by higginsonj on 12/05/2016.
 */
public class JSONHelper
{
	public static <T> Collection<T> ParseArray(Function<JsonObject, T> parser, String arrayJson)
	{
		List<T> collection = new ArrayList<>();
		JsonArray itemArray = new JsonParser().parse(arrayJson).getAsJsonArray();

		for(JsonElement itemElement : itemArray)
		{
			JsonObject itemObject = (JsonObject)itemElement;
			T item = parser.apply(itemObject);
			collection.add(item);
		}

		return collection;
	}

	public static <T> JsonArray SerializeArray(Function<T, JsonObject> serializer, Collection<T> items)
	{
		JsonArray jsonArray = new JsonArray();

		for(T item : items)
		{
			JsonObject jsonObject = serializer.apply(item);
			jsonArray.add(jsonObject);
		}

		return jsonArray;
	}

	public static JsonArray SerializeStringArray(Collection<String> items)
	{
		JsonArray jsonArray = new JsonArray();
		items.forEach(jsonArray::add);
		return jsonArray;
	}

	public static <T> Collection<String> ParseStringArray(String arrayJson)
	{
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(arrayJson);
		JsonArray jsonArray = (JsonArray)element;

		List<String> items = new ArrayList<>();
		for(int i = 0; i < jsonArray.size(); i++)
		{
			String item = jsonArray.get(i).getAsString();
			items.add(item);
		}

		return items;
	}
}
