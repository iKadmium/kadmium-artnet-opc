package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import com.flynneffectmusic.DMXOPCAdapter.programs.effects.PixelEffect;
import org.jdom2.Element;

/**
 * Created by higginsonj on 3/12/2014.
 */
public class Strobe extends PixelEffect
{
    int fadeTime = 0;
    int fadeCounter = 1;
    boolean strobeOn = true;


    public Strobe(int fadeTime)
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

    public static Strobe deserialize(Element element)
    {
        int fadeTime = Integer.parseInt(element.getAttributeValue("fadeTime"));
        return new Strobe(fadeTime);
    }
}
