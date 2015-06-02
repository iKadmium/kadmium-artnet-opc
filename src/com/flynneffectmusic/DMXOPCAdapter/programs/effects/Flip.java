package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import java.util.ArrayList;
import java.util.HashMap;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelMath;
import org.jdom2.Element;

public class Flip extends PixelEffect
{
	boolean horizontal;
	public Flip(boolean horizontal)
	{
		this.horizontal = horizontal;
	}
	
	@Override
	public void Apply(PixelFixture fixture, float offset)
	{
		int dimensions = horizontal ? fixture.GetXDimensions() : fixture.GetYDimensions();
		
		HashMap<Integer, ArrayList<Pixel>> pixels = new HashMap<>();
		
		for(int i = 0; i < dimensions; i++)
		{
			int newIndex = dimensions - i - 1;
			if(horizontal)
			{
				pixels.put(newIndex, PixelMath.GetColors(fixture.GetColumn(i)));
			}
			else
			{
				pixels.put(newIndex, PixelMath.GetColors(fixture.GetRow(i)));
			}
		}
		
		for(int i = 0; i < dimensions; i++)
		{
			if(horizontal)
			{
				fixture.SetColumn(i, pixels.get(i));
			}
			else
			{
				fixture.SetRow(i, pixels.get(i));
			}
		}
	}

    public static Flip deserialize(Element element)
    {
        boolean horizontal = Boolean.parseBoolean(element.getAttributeValue("horizontal"));
        return new Flip(horizontal);
    }

}
