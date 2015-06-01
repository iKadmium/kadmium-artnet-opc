package com.flynneffectmusic.DMXOPCAdapter;

import org.jdom2.Element;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class StraightAdapter extends DMXOPCAdapter
{
    public StraightAdapter()
    {

    }

    @Override
    public Element serialize()
    {
        return serialize("straight");
    }

    @Override
    public int getDMXLength(int pixelCount)
    {
        return DMXOPCAdapter.PIXEL_LENGTH * pixelCount;
    }

}
