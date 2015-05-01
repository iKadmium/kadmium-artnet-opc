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
        Element adapterElement = new Element("adapterMethod");
        adapterElement.setText("straight");
        return adapterElement;
    }

    @Override
    public int getDMXLength(int pixelCount)
    {
        return DMXOPCAdapter.PIXEL_LENGTH * pixelCount;
    }

}
