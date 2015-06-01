package com.flynneffectmusic.DMXOPCAdapter.programs;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;

public class ImageGenerator extends PixelGenerator
{
	static final String PATH_TO_IMAGES = "data/pixelImages/"; 
	Image image;
	
	public ImageGenerator(String filename)
	{
		this.image = new Image(GetFileURI(filename));
		
	}
	
	@Override
	public void Generate(PixelFixture fixture, float offset)
	{
		for(int x = 0; x < fixture.GetXDimensions(); x++)
		{
			for(int y = 0; y < fixture.GetYDimensions(); y++)
			{
				Color color = image.getPixelReader().getColor(x, y);
				Pixel pixel = fixture.Get(x, y);
				pixel.SetHue(hue);
				pixel.SetSaturation(saturation);
				if(color.getOpacity() != 0)
				{
					pixel.SetBrightness(brightness);
				}
				else
				{
					pixel.SetBrightness(0.0f);
				}
			}
		}
	}
	
	static String GetFileURI(String filename)
	{
		File file = new File(filename);
		return file.toURI().toString();
	}
	
	public static File[] GetImageFiles()
	{
		File file = new File(PATH_TO_IMAGES);
		return file.listFiles();
	}

}
