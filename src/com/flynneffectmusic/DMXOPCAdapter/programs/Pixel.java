package com.flynneffectmusic.DMXOPCAdapter.programs;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

public class Pixel
{
	float hue;
	float saturation;
	float brightness;
	
	Rectangle rectangle;

    HashMap<AnimatablePropertyType, AnimatableProperty> properties;
	
	public Pixel()
	{
		hue = 0.0f;
		saturation = 0.0f;
		brightness = 0.0f;

        properties = new HashMap<>();

        properties.put(AnimatablePropertyType.HUE, new AnimatableProperty(this::SetHue, this::GetHue));
        properties.put(AnimatablePropertyType.SATURATION, new AnimatableProperty(this::SetSaturation, this::GetSaturation));
        properties.put(AnimatablePropertyType.BRIGHTNESS, new AnimatableProperty(this::SetBrightness, this::GetBrightness));

	}
	
	public void SetRectangle(Rectangle rectangle)
	{
		this.rectangle = rectangle;
	}
	
	public void SetHue(float hue)
	{
		if(hue >= 0.0f && hue <= 1.0f)
		{
			this.hue = hue;
		}
		else
		{
			throw new IllegalArgumentException("hue should be between 0.0 and 1.0");
		}
	}
	
	public void SetSaturation(float saturation)
	{
		if(saturation >= 0.0f && saturation <= 1.0f)
		{
			this.saturation = saturation;
		}
		else
		{
			throw new IllegalArgumentException("saturation should be between 0.0 and 1.0");
		}
	}
	
	public void SetBrightness(float brightness)
	{
		if(brightness >= 0.0f && brightness <= 1.0f)
		{
			this.brightness = brightness;
		}
		else
		{
			throw new IllegalArgumentException("brightness should be between 0.0 and 1.0");
		}
	}
	
	public void UpdateDisplay()
	{
		Color color = GetColor();
		if(rectangle != null)
		{
			Platform.runLater(() -> rectangle.setFill(color));
		}
	}

	public float GetHue()
	{
		return hue;
	}
	
	public float GetSaturation()
	{
		return saturation;
	}
	
	public float GetBrightness()
	{
		return brightness;
	}

	public void SetColor(Color color)
	{
		hue = (float)color.getHue() / 360.0f;
		saturation = (float)color.getSaturation();
		brightness = (float)color.getBrightness();
	}

	public Color GetColor()
	{
		return Color.hsb(hue * 360, saturation, brightness);
	}	
	
	public int GetRed()
	{
		return (int) Math.round((GetColor().getRed() * 255));
	}
	
	public int GetGreen()
	{
		return (int) Math.round((GetColor().getGreen() * 255));
	}
	
	public int GetBlue()
	{
		return (int) Math.round((GetColor().getBlue() * 255));
	}
	
	@Override
	public String toString()
	{
		return GetRed() + " " + GetBlue() + " " + GetGreen();
	}

    public AnimatableProperty GetProperty(AnimatablePropertyType propertyType)
    {
        return properties.get(propertyType);
    }
}
