package com.flynneffectmusic.DMXOPCAdapter;

import org.jdom2.Element;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class GradientAdapter extends DMXOPCAdapter
{
    public GradientAdapter()
    {
    }

    @Override
    public byte[] adaptDMX(byte[] dmx, int pixelCount)
    {
        byte[] pixelValues = new byte[pixelCount * PIXEL_LENGTH];
        for(int i = 0; i < pixelCount; i++)
        {
            for(int j = 0; j < PIXEL_LENGTH; j++)
            {
                pixelValues[i * PIXEL_LENGTH + j] = lerp(i, pixelCount, dmx[j], dmx[PIXEL_LENGTH + j]);
            }
        }
        return pixelValues;
    }

    @Override
    public int getDMXLength(int pixelCount)
    {
        return DMXOPCAdapter.PIXEL_LENGTH * 2;
    }

    public byte lerp(int position, int maxPosition, byte startingValue, byte endingValue)
    {
        float percentage = (float)position / (float)maxPosition;
        float total = (1 - percentage) * unsignedByte(startingValue) + percentage * unsignedByte(endingValue);
        return (byte)total;
    }

    private static float unsignedByte(byte source)
    {
        return (float)(source & 0xFF);
    }

    @Override
    public Element serialize()
    {
        return serialize("gradient");
    }

}
