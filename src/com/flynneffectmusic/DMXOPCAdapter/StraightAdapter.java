package com.flynneffectmusic.DMXOPCAdapter;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class StraightAdapter implements IDMXOPCAdapter
{
    private int pixelCount;

    public StraightAdapter(int pixelCount)
    {
        this.pixelCount = pixelCount;
    }

    @Override
    public byte[] adaptDMX(byte[] dmx)
    {
        byte[] pixelValues = new byte[pixelCount * IDMXOPCAdapter.PIXEL_LENGTH];
        System.arraycopy(dmx, 0, pixelValues, 0, dmx.length);
        return pixelValues;
    }
}
