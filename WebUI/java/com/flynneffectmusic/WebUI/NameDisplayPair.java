package com.flynneffectmusic.WebUI;

import com.google.gson.JsonObject;

public class NameDisplayPair
{
	public String GetName()
	{
		return name;
	}

	public void SetName(String name)
	{
		this.name = name;
	}

	public String GetDisplay()
	{
		return display;
	}

	public void SetDisplay(String display)
	{
		this.display = display;
	}

	private String name;
	private String display;

	public NameDisplayPair(String name, String display)
	{
		this.name = name;
		this.display = display;
	}

	public JsonObject SerializeToJSON()
	{
		JsonObject object = new JsonObject();
		object.addProperty("name", GetName());
		object.addProperty("value", GetName());
		return object;
	}


}
