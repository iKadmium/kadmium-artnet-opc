package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import com.flynneffectmusic.DMXOPCAdapter.programs.*;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by higginsonj on 2/09/2015.
 */
public class ProgressBar extends PixelEffect
{
    int sections;
    AnimatablePropertyType propertyType;

    public ProgressBar(AnimatablePropertyType propertyType, int sections)
    {
        this.propertyType = propertyType;
        this.sections = sections;
    }

    @Override
    public void Apply(PixelFixture fixture, float offset)
    {
        int totalSections;
        if(sections != 0)
        {
            totalSections = sections;
        }
        else
        {
            totalSections = fixture.GetXDimensions();
        }

        float sectionPercentage = 1f / (float)totalSections;
        int sectionPixels = fixture.GetXDimensions() / totalSections;

        for(int sectionNumber = 0; sectionNumber < totalSections; sectionNumber++)
        {
            float sectionFraction = sectionNumber * sectionPercentage;
            float amount = (offset - sectionFraction) / sectionPercentage; // if the section isn't lit up yet, will be negative. If it's fully lit, will be greater than 1

            amount = PixelMath.Clamp(amount);

            for(int pixelInSection = 0; pixelInSection < sectionPixels; pixelInSection++)
            {
                int columnNumber = sectionPixels * sectionNumber + pixelInSection;
                ArrayList<Pixel> column = fixture.GetColumn(columnNumber);

                for(Pixel pixel : column)
                {
                    AnimatableProperty property = pixel.GetProperty(propertyType);
                    property.Set(amount);
                }
            }
        }

        int remainingPixels = fixture.GetXDimensions() % totalSections;
        int startPoint = fixture.GetXDimensions() - remainingPixels;
        float sectionFraction = (totalSections - 1) * sectionPercentage;
        float amount = (offset - sectionFraction) / sectionPercentage; // if the section isn't lit up yet, will be negative. If it's fully lit, will be greater than 1
        amount = PixelMath.Clamp(amount);

        for(int columnNumber = startPoint; columnNumber < fixture.GetXDimensions(); columnNumber++)
        {
            ArrayList<Pixel> column = fixture.GetColumn(columnNumber);

            for(Pixel pixel : column)
            {
                AnimatableProperty property = pixel.GetProperty(propertyType);
                property.Set(amount);
            }
        }

    }

    public static ProgressBar deserialize(Element element)
    {
        AnimatablePropertyType propertyType = AnimatablePropertyType.valueOf(element.getAttributeValue("property").toUpperCase());
        int sections = Integer.parseInt(element.getAttributeValue("sections"));

        return new ProgressBar(propertyType, sections);
    }
}
