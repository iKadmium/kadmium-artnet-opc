package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import com.flynneffectmusic.DMXOPCAdapter.programs.Pixel;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import com.flynneffectmusic.DMXOPCAdapter.programs.PixelMath;
import org.jdom2.Element;

import java.util.Collection;

/**
 * Created by higginsonj on 3/12/2014.
 */
public class Apeshit
    extends PixelEffect
{
    int fadeTime;
    int countdown;
    Collection<? extends Pixel> currentPixels;
    int coverage;

    public Apeshit(int fadeTime, int coverage)
    {
        this.fadeTime = fadeTime;
        countdown = 1;
        this.coverage = coverage;
    }

    @Override
    public void Apply(PixelFixture fixture, float offset)
    {
        if(offset > 0.0f)
        {
            countdown--;
            if (countdown <= 0)
            {
                countdown = Math.round(PixelMath.Clamp(fadeTime / offset, 0, 10f));
                currentPixels = fixture.GetRandom((fixture.GetXDimensions() * fixture.GetYDimensions()) - coverage);
            }

            for (Pixel pixel : currentPixels)
            {
                pixel.SetBrightness(0);
            }
        }
    }

    public static Apeshit deserialize(Element element)
    {
        int fadeTime = Integer.parseInt(element.getAttributeValue("fadeTime"));
        int coverage = Integer.parseInt(element.getAttributeValue("coverage"));
        return new Apeshit(fadeTime, coverage);
    }
}
