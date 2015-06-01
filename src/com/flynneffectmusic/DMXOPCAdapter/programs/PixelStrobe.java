package com.flynneffectmusic.DMXOPCAdapter.programs;

import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;

/**
 * Created by higginsonj on 3/12/2014.
 */
public class PixelStrobe extends PixelEffect
{
    int fadeTime = 0;
    int fadeCounter = 1;
    boolean strobeOn = true;

    float strobe = 0.0f;

    public PixelStrobe(int fadeTime)
    {
        this.fadeTime = fadeTime;
    }

    @Override
    public void Apply(PixelFixture fixture, float offset)
    {
        fadeCounter--;
        if(fadeCounter == 0)
        {
            strobeOn = !strobeOn;
            fadeCounter = fadeTime;
        }

        if(!strobeOn)
        {
            fixture.GetPixelMap().values().forEach(pixel -> pixel.SetBrightness(0));
        }
    }

    @Override
    public void Set(String attribute, float value)
    {
        if(attribute.equalsIgnoreCase("Strobe"))
        {
            strobe = value;
        }
    }

    @Override
    public boolean SolvesForAttribute(String attribute)
    {
        return attribute.equalsIgnoreCase("Strobe");
    }

    @Override
    public boolean IsActive()
    {
        return strobe > 0.0f;
    }
}
