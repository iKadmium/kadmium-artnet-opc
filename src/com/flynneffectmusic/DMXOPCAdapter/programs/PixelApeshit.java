package com.flynneffectmusic.DMXOPCAdapter.programs;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;

import java.util.Collection;

/**
 * Created by higginsonj on 3/12/2014.
 */
public class PixelApeshit extends PixelEffect
{
    int fadeTime;
    int countdown;
    Collection<? extends Pixel> currentPixels;
    float coverage;

    float apeshit = 0.0f;

    public PixelApeshit(int fadeTime, float coverage)
    {
        this.fadeTime = fadeTime;
        countdown = 1;
        this.coverage = coverage;
    }

    @Override
    public void Apply(PixelFixture fixture, float offset)
    {
        countdown--;
        if(countdown <= 0)
        {
            int trueCountdown = Math.round(PixelMath.Clamp(fadeTime / apeshit, 0, 10f));
            countdown = trueCountdown;
            int pixelCount = (int)Math.floor((double)((1 - coverage) * fixture.GetPixelMap().size()));
            currentPixels = fixture.GetRandom(pixelCount);
        }

        for(Pixel pixel : currentPixels)
        {
            pixel.SetBrightness(0);
        }
    }

    @Override
    public void Set(String attribute, float value)
    {
        switch(attribute)
        {
            case "Apeshit":
                apeshit = value;
                break;
        }
    }

    @Override
    public boolean SolvesForAttribute(String attribute)
    {
        return attribute.equalsIgnoreCase("Apeshit");
    }

    @Override
    public boolean IsActive()
    {
        return apeshit > 0.0f;
    }
}
