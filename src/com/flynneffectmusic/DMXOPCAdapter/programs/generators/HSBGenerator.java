package com.flynneffectmusic.DMXOPCAdapter.programs.generators;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import org.jdom2.Element;

public class HSBGenerator extends PixelGenerator
{
	private HSBGenerator(float hue, float saturation, float brightness)
	{
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
	}
	
	@Override
	public void Generate(PixelFixture fixture, float offset)
	{
		for(Pixel pixel : fixture.GetPixels(true, true, true))
		{
			pixel.SetHue(hue);
			pixel.SetSaturation(saturation);
			pixel.SetBrightness(brightness);
		}
	}

    public static HSBGenerator deserialize(Element element)
    {
        float hue = Float.parseFloat(element.getAttributeValue("hue"));
        float saturation = Float.parseFloat(element.getAttributeValue("saturation"));
        float brightness = Float.parseFloat(element.getAttributeValue("brightness"));
        return new HSBGenerator(hue, saturation, brightness);
    }

}
