package com.flynneffectmusic.DMXOPCAdapter;

import org.jdom2.Element;

/**
 * Created by higginsonj on 23/04/2015.
 */
public class CopyAdapter extends DMXOPCAdapter
{
    public CopyAdapter()
    {

    }


    @Override
    public Element serialize()
    {
        return serialize("copy");
    }

    @Override
    public int getDMXLength(int pixelCount)
    {
        return PIXEL_LENGTH;
    }

}
