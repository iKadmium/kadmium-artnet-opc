package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import java.util.ArrayList;

import com.flynneffectmusic.DMXOPCAdapter.programs.AnimatablePropertyType;
import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelMath;
import com.flynneffectmusic.DMXOPCAdapter.programs.effects.PixelEffect;
import javafx.geometry.Point2D;
import org.jdom2.Element;


public class Wave extends PixelEffect
{
	float pixelShiftDelta;
	AnimatablePropertyType propertyType;
	boolean horizontal;
	boolean vertical; 
	float cutoffThresholdMin;
	float cutoffThresholdMax;
	boolean cutoff = false;
	boolean topToBottom;
	float speedMultiplier;

    float chase = 0.0f;
	
	public  Wave(AnimatablePropertyType propertyType, float pixelShiftDelta, boolean horizontal, boolean vertical, float speedMultiplier)
	{
		this.pixelShiftDelta = pixelShiftDelta;
		this.propertyType = propertyType;
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.speedMultiplier = speedMultiplier;
	}

    private Wave(AnimatablePropertyType propertyType, float pixelShiftDelta, boolean horizontal, boolean vertical,
	        float speedMultiplier, float cutoffThresholdMin, float cutoffThresholdMax)
    {
        this(propertyType, pixelShiftDelta, horizontal, vertical, speedMultiplier);
        this.cutoff = true;
        this.cutoffThresholdMin = cutoffThresholdMin;
        this.cutoffThresholdMax = cutoffThresholdMax;
    }
	
	@Override
	public void Apply(PixelFixture fixture, float offset)
	{
		ArrayList<Pixel> pixels = fixture.GetPixels(true, true, true);
		Point2D centre = new Point2D( ((double)fixture.GetXDimensions()) / 2.0, ((double)fixture.GetYDimensions()) / 2.0);
		
		offset *= Math.PI;
		
		for(Pixel pixel : pixels)
		{
			Point2D point = fixture.GetPosition(pixel);
			double usedX = point.getX();
			double usedY = point.getY();
			if(!horizontal)
			{
				usedX = centre.getX();
			}
			if(!vertical)
			{
				usedY = centre.getY();
			}
			Point2D usedPoint = new Point2D(usedX, usedY);
			float distance = (float)usedPoint.distance(centre);
			float addition = (pixelShiftDelta* distance) + (offset * speedMultiplier);
    		float value = Math.abs((float)Math.sin(addition));
    		if(cutoff)
    		{
    		    value = PixelMath.Cutoff(value, cutoffThresholdMin, cutoffThresholdMax);
    			value = (value > 0.0f) ? PixelMath.Rescale(value, cutoffThresholdMin, cutoffThresholdMax) : value;
    		}
            pixel.GetProperty(propertyType).Set(value);
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
        return false;
    }

    @Override
    public boolean IsActive()
    {
        return chase > 0.0f;
    }

    public static Wave deserialize(Element element)
    {
        AnimatablePropertyType propertyType = AnimatablePropertyType.valueOf(element.getAttributeValue("property").toUpperCase());
        float pixelShiftDelta = Float.parseFloat(element.getAttributeValue("pixelShiftDelta"));
        boolean horizontal = Boolean.parseBoolean(element.getAttributeValue("horizontal"));
        boolean vertical = Boolean.parseBoolean(element.getAttributeValue("vertical"));
        float speedMultiplier = Float.parseFloat(element.getAttributeValue("speedMultiplier"));

        if(element.getAttribute("cutoffMin") != null)
        {
            float cutoffMin = Float.parseFloat(element.getAttributeValue("cutoffMin"));
            float cutoffMax = Float.parseFloat(element.getAttributeValue("cutoffMax"));
            return new Wave(propertyType, pixelShiftDelta, horizontal, vertical, speedMultiplier, cutoffMin, cutoffMax);
        }
        else
        {
            return new Wave(propertyType, pixelShiftDelta, horizontal, vertical, speedMultiplier);
        }


    }

}
