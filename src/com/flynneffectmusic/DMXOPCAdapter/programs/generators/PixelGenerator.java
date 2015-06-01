package com.flynneffectmusic.DMXOPCAdapter.programs.generators;

import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import org.jdom2.Element;

public abstract class PixelGenerator
{
	float hue;
	float saturation;
	float brightness;
	
	public float GetHue()
	{
		return hue;
	}

	public void SetHue(float hue)
	{
		this.hue = hue;
	}

	public float GetSaturation()
	{
		return saturation;
	}

	public void SetSaturation(float saturation)
	{
		this.saturation = saturation;
	}

	public float GetBrightness()
	{
		return brightness;
	}

	public void SetBrightness(float brightness)
	{
		this.brightness = brightness;
	}
	
	public abstract void Generate(PixelFixture fixture, float offset);

    public static PixelGenerator deserialize(Element element)
    {
        switch(element.getName())
        {
            case "hsb":
                return HSBGenerator.deserialize(element);
            case "image":
                return ImageGenerator.deserialize(element);
            default:
                return null;
        }
    }
}
