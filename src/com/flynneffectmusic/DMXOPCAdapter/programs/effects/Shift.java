package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import java.util.ArrayList;

import com.flynneffectmusic.DMXOPCAdapter.programs.*;
import org.jdom2.Element;

public class Shift extends PixelEffect
{
	float pixelShiftDelta;
	AnimatablePropertyType propertyType;
	boolean horizontal;
	float cutoffThresholdMin;
	float cutoffThresholdMax;
	boolean cutoff = false;
	boolean leftToRight; 
	boolean topToBottom;

    float chase;
	
	public Shift(AnimatablePropertyType propertyType, float pixelShiftDelta, boolean horizontal, boolean leftToRight, boolean topToBottom)
	{
		this.pixelShiftDelta = pixelShiftDelta;
		this.propertyType = propertyType;
		this.horizontal = horizontal;
		this.leftToRight = leftToRight;
		this.topToBottom = topToBottom;
	}
	
	public Shift(AnimatablePropertyType propertyType, float pixelShiftDelta, boolean horizontal, boolean leftToRight, boolean topToBottom,
	        float cutoffThresholdMin, float cutoffThresholdMax)
    {
        this(propertyType, pixelShiftDelta, horizontal, leftToRight, topToBottom);
        this.cutoff = true;
        this.cutoffThresholdMin = cutoffThresholdMin;
        this.cutoffThresholdMax = cutoffThresholdMax;
    }
	
	@Override
	public void Apply(PixelFixture fixture, float offset)
	{
		offset *= Math.PI;
		float currentValue = offset;
		ArrayList<Pixel> pixels = fixture.GetPixels(horizontal, leftToRight, topToBottom);
		
		for(Pixel pixel : pixels)
		{
    		float value = Math.abs((float)Math.sin(currentValue));
    		if(cutoff)
    		{
    		    value = PixelMath.Cutoff(value, cutoffThresholdMin, cutoffThresholdMax);
    			value = (value > 0.0f) ? PixelMath.Rescale(value, cutoffThresholdMin, cutoffThresholdMax) : value;
    		}
    		pixel.GetProperty(propertyType).Set(value);
    		currentValue += pixelShiftDelta;
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

    public static Shift deserialize(Element element)
    {
        AnimatablePropertyType propertyType = AnimatablePropertyType.valueOf(element.getAttributeValue("property").toUpperCase());
        float pixelShiftDelta = Float.parseFloat(element.getAttributeValue("pixelShiftDelta"));
        boolean horizontal = Boolean.parseBoolean(element.getAttributeValue("horizontal"));
        boolean leftToRight = Boolean.parseBoolean(element.getAttributeValue("leftToRight"));
        boolean topToBottom = Boolean.parseBoolean(element.getAttributeValue("topToBottom"));

        return new Shift(propertyType, pixelShiftDelta, horizontal, leftToRight, topToBottom);
    }

}
