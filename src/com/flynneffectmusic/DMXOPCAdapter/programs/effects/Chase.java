package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import com.flynneffectmusic.DMXOPCAdapter.programs.*;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Created by higginsonj on 3/12/2014.
 */
public class Chase extends PixelEffect
{
    int nodes;
    float coverage;
    Wave wave;
    Translate translate;

    float chase = 0.0f;

    private Chase(int nodes, float coverage)
    {
        this.nodes = nodes;
        this.coverage = coverage;

        float pixelShiftDelta = (float) (Math.PI);
        wave = new Wave(AnimatablePropertyType.BRIGHTNESS, 0.25f, true, false, 1);
        translate = new Translate(true, true);
    }

    @Override
    public void Apply(PixelFixture fixture, float offset)
    {
        int centres = fixture.GetXDimensions() / (nodes);

        ArrayList<Integer> unaffected = new ArrayList<>(fixture.GetXDimensions());
        IntStream.range(0, fixture.GetXDimensions()).forEach(xValue -> unaffected.add(xValue));

        int maxDistance = (int)Math.floor((fixture.GetXDimensions() * coverage) / (nodes * 2));

        for(int node = 1; node <= nodes; node++)
        {
            int nodeCentreX = (node * centres);
            for(int xOffset = -maxDistance; xOffset <= maxDistance; xOffset++)
            {
                int distance = Math.abs(xOffset);
                float brightnessDelta = 1 - ((float)distance / (float)(maxDistance + 1));
                int x = PixelMath.Wrap(nodeCentreX + xOffset, 1, fixture.GetXDimensions());

                ArrayList<Pixel> column = fixture.GetColumn(x - 1);
                column.forEach(pixel -> pixel.SetBrightness(pixel.GetBrightness() * brightnessDelta));
                unaffected.remove((Integer)(x - 1));
            }
        }

        for(int xValue : unaffected)
        {
            fixture.GetColumn(xValue).forEach(pixel -> pixel.SetBrightness(0));
        }

        translate.Apply(fixture, chase);
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

    public static Chase deserialize(Element element)
    {
        int nodes = Integer.parseInt(element.getAttributeValue("nodes"));
        float coverage = Float.parseFloat(element.getAttributeValue("coverage"));
        return new Chase(nodes, coverage);
    }
}
