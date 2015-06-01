package com.flynneffectmusic.DMXOPCAdapter.programs;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;

public class HSBGenerator extends PixelGenerator
{
	public HSBGenerator(float hue, float saturation, float brightness)
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

}
