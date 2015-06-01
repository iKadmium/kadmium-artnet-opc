package com.flynneffectmusic.DMXOPCAdapter.programs;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;

public class Mirror extends PixelEffect
{
	boolean horizontal;
	boolean vertical;

	public Mirror(boolean horizontal, boolean vertical)
	{
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	@Override
	public void Apply(PixelFixture fixture, float offset)
	{
		int xDimensions = horizontal ? fixture.GetXDimensions() / 2: fixture.GetXDimensions();
		int yDimensions = vertical ? fixture.GetYDimensions() / 2: fixture.GetYDimensions();
		
		for(int x = 0; x < xDimensions; x++)
		{
			for(int y = 0; y < yDimensions; y++)
			{
				Pixel sourcePixel = fixture.Get(x, y);
				if(horizontal)
				{
					Pixel targetPixel = fixture.Get(fixture.GetXDimensions() - x - 1, y);
					targetPixel.SetColor(sourcePixel.GetColor());
				}
				if(vertical)
				{
					Pixel targetPixel = fixture.Get(x, fixture.GetYDimensions() - y - 1);
					targetPixel.SetColor(sourcePixel.GetColor());
				}
				if(horizontal && vertical)
				{
					Pixel targetPixel = fixture.Get(fixture.GetXDimensions() - x - 1, fixture.GetYDimensions() - y - 1);
					targetPixel.SetColor(sourcePixel.GetColor());
				}
			}
		}
	}

    @Override
    public void Set(String attribute, float value)
    {

    }

    @Override
    public boolean SolvesForAttribute(String attribute)
    {
        return false;
    }

    @Override
    public boolean IsActive()
    {
        return true;
    }

}
