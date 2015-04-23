package com.flynneffectmusic.DMXOPCAdapter;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class CopyAdapter implements IDMXOPCAdapter
{
    private int pixelCount;

    public CopyAdapter(int pixelCount)
    {
        this.pixelCount = pixelCount;
    }

    @Override
    public byte[] adaptDMX(byte[] dmx)
    {
        byte[] pixelValues = new byte[pixelCount * IDMXOPCAdapter.PIXEL_LENGTH];
        for(int i = 0; i < pixelCount; i++)
        {
            System.arraycopy(dmx, 0, pixelValues, i * PIXEL_LENGTH, PIXEL_LENGTH);
        }
        return pixelValues;
    }
}
