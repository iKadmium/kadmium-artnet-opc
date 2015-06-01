package com.flynneffectmusic.DMXOPCAdapter.programs.generators;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import org.jdom2.Element;

import javax.imageio.ImageIO;

public class ImageGenerator extends PixelGenerator
{
	static final String PATH_TO_IMAGES = "pixelImages/";
	BufferedImage image;
	
	private ImageGenerator(String filename)
	{
        try
        {
            image = ImageIO.read(new File(filename));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
		
	}
	
	@Override
	public void Generate(PixelFixture fixture, float offset)
	{
		for(int x = 0; x < fixture.GetXDimensions(); x++)
		{
			for(int y = 0; y < fixture.GetYDimensions(); y++)
			{
                Color color = new Color(image.getRGB(x, y));

				Pixel pixel = fixture.Get(x, y);
				pixel.SetHue(hue);
				pixel.SetSaturation(saturation);
                if(color.getAlpha() != 0)
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

    public static ImageGenerator deserialize(Element element)
    {
        String src = element.getAttributeValue("src");
        return new ImageGenerator(PATH_TO_IMAGES + src);
    }

}
