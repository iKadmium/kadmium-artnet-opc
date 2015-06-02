package com.flynneffectmusic.DMXOPCAdapter;

import com.flynneffectmusic.Settings;
import org.jdom2.Element;

/**
 * Created by higginsonj on 23/04/2015.
 */
public abstract class DMXOPCAdapter
{
    public static int PIXEL_LENGTH = 3;

    public abstract Element serialize();

    public byte[] adaptDMX(byte[] dmx, int pixelCount)
    {
        byte[] pixelValues = new byte[pixelCount * PIXEL_LENGTH];
        for(int i = 0; i < pixelCount; i++)
        {
            System.arraycopy(dmx, 0, pixelValues, i * PIXEL_LENGTH, PIXEL_LENGTH);
        }
        return pixelValues;
    }

    protected Element serialize(String adapterType)
    {
        Element adapterElement = new Element("adapter");

        adapterElement.setAttribute("method", adapterType);
        return adapterElement;
    }

    public abstract int getDMXLength(int pixelCount);

    public static DMXOPCAdapter deserialize(Element adapter)
    {
        switch(adapter.getAttributeValue("method"))
        {
            default:
            case "copy":
                return new CopyAdapter();
            case "straight":
                return new StraightAdapter();
            case "gradient":
                return new GradientAdapter();
            case "program":
                int xCount = Integer.parseInt(adapter.getAttributeValue("xCount"));
                int yCount = Integer.parseInt(adapter.getAttributeValue("yCount"));
                if(xCount * yCount != Settings.getPixelCount())
                {
                    throw new IllegalArgumentException("Pixel Count (" + Settings.getPixelCount() + ") does not match adapter x*y (" + xCount + "x" + yCount +")");
                }
                return new ProgramAdapter(xCount, yCount);
        }
    }
}
