package com.flynneffectmusic.DMXOPCAdapter.programs;

import java.util.ArrayList;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;

public abstract class PixelMath
{
	public static float Clamp(float value)
	{
		return Clamp(value, 0.0f, 1.0f);
	}
	
	public static float Wrap(float value)
	{
		return Wrap(value, 0, 1);
	}
	
	public static float Mirror(float value)
	{
		return Mirror(value, 0, 1);
	}
	
	public static int Wrap(int value, int min, int max)
	{
		while(value > max)
		{
			value -= max;
		}
		while(value < min)
		{
			value += max;
		}
		return value;
	}
	
	public static float Wrap(float value, float min, float max)
	{
		while(value > max)
		{
			value -= max;
		}
		while(value < min)
		{
			value += max;
		}
		return value;
	}
	
	public static float Clamp(float value, float min, float max)
	{
		if(value > max)
		{
			return max;
		}
		else if(value < min)
		{
			return min;
		}
		return value;
	}
	
	public static float Mirror(float value, float min, float max)
	{
		if(value > max)
		{
			float difference = value - max;
			value = max - difference;
		}
		if(value < min)
		{
			float difference = value - min;
			value = min - difference;
		}
		return value;
	}
	
	public static float Cutoff(float value, float min, float max)
	{
		if(value < min)
	    {
	        return min;
	    }
        else if(value > max)
        {
            return max;
        }
	    else
	    {
	        return value;
	    }
	}
	
    public static float Rescale(float value, float min, float max)
    {
        float range = max - min;
        return (value - min) / range;
    }
    
    public static ArrayList<Pixel> GetColors(ArrayList<Pixel> source)
	{
		ArrayList<Pixel> destination = new ArrayList<>();
		for(Pixel pixel : source)
		{
			Pixel newPixel = new Pixel();
			newPixel.SetColor(pixel.GetColor());
			destination.add(newPixel);
		}
		return destination;
	}
}
