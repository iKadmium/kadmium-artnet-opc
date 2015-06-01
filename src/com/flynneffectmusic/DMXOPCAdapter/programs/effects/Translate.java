package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import java.util.ArrayList;
import java.util.HashMap;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelMath;
import org.jdom2.Element;

public class Translate extends PixelEffect
{
	boolean horizontal;
	boolean positiveTranslate;

    float chase = 0.0f;
	
	public Translate(boolean horizontal, boolean positiveTranslate)
	{
		this.horizontal = horizontal;
		this.positiveTranslate = positiveTranslate;
	}
	
	@Override
	public void Apply(PixelFixture fixture, float offset)
	{
		int dimensions = horizontal ? fixture.GetXDimensions() : fixture.GetYDimensions();
		offset *= dimensions;
		if(!positiveTranslate)
		{
			offset *= -1;
		}
		offset = PixelMath.Wrap(offset, 0, (float) dimensions); //always shifting to the right
		
		int trueOffset = Math.round(offset);
		
		HashMap<Integer, ArrayList<Pixel>> pixels = new HashMap<>();
		
		for(int i = 0; i < dimensions; i++)
		{
			int newIndex = i + trueOffset;
			if(newIndex >= dimensions)
			{
				newIndex -= dimensions;
			}
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

    @Override
    public void Set(String attribute, float value)
    {
        if(attribute.equalsIgnoreCase("Chase"))
        {
            chase = value;
        }
    }

    @Override
    public boolean SolvesForAttribute(String attribute)
    {
        return attribute.equalsIgnoreCase("Chase");
    }

    @Override
    public boolean IsActive()
    {
        return chase > 0.0f;
    }

    public static Translate deserialize(Element element)
    {
        boolean horizontal = Boolean.parseBoolean(element.getAttributeValue("horizontal"));
        boolean positive = Boolean.parseBoolean(element.getAttributeValue("positive"));

        return new Translate(horizontal, positive);
    }

}
