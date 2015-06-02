package com.flynneffectmusic.DMXOPCAdapter.programs;

import com.flynneffectmusic.DMXOPCAdapter.programs.effects.PixelEffect;
import com.flynneffectmusic.DMXOPCAdapter.programs.generators.HSBGenerator;
import com.flynneffectmusic.DMXOPCAdapter.programs.generators.PixelGenerator;
import org.jdom2.Element;

import java.util.ArrayList;

/**
 * Created by higginsonj on 1/06/2015.
 */
public class Program
{
    PixelGenerator generator;
    ArrayList<PixelEffect> effects;

    PixelFixture fixture;

    private float hue;
    private float saturation;
    private float brightness;

    private Program(PixelFixture fixture, PixelGenerator generator, ArrayList<PixelEffect> effects)
    {
        this.fixture = fixture;
        this.effects = effects;
        this.generator = generator;
    }

    public void Solve(float offset)
    {
        generator.Generate(fixture, offset);
        if (generator != null)
        {
            generator.Generate(fixture, offset);
        }

        effects.stream().forEach(effect -> effect.Apply(fixture, offset));
    }

    public float getHue()
    {
        return hue;
    }

    public void setHue(float hue)
    {
        this.hue = hue;
        generator.SetHue(hue);
    }

    public float getSaturation()
    {
        return saturation;
    }

    public void setSaturation(float saturation)
    {
        this.saturation = saturation;
        generator.SetSaturation(saturation);
    }

    public float getBrightness()
    {
        return brightness;
    }

    public void setBrightness(float brightness)
    {
        this.brightness = brightness;
        generator.SetBrightness(brightness);
    }

    public static Program deserialize(PixelFixture fixture, Element programElement)
    {
        PixelGenerator generator = PixelGenerator.deserialize(programElement.getChildren().get(0));
        Element effectsElement = programElement.getChild("effects");
        ArrayList<PixelEffect> effects = new ArrayList<>();
        for(Element effectElement : effectsElement.getChildren())
        {
            PixelEffect effect = PixelEffect.deserialize(effectElement);
            effects.add(effect);
        }

        return new Program(fixture, generator, effects);
    }
}
