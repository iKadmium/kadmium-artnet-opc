package com.flynneffectmusic.DMXOPCAdapter;


import com.flynneffectmusic.DMXOPCAdapter.programs.*;
import com.flynneffectmusic.Main;
import javafx.scene.paint.Color;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import java.io.IOException;
import java.util.ArrayList;

public class ProgramAdapter extends DMXOPCAdapter
{
    float hue = 0.0f;
    float saturation = 0.0f;
    float brightness = 0.0f;
    float offset = 0.0f;
    float animationDelta = 0.02f;
    int demoCounter = 160;
    int demoEffectsProgram = 0;
    int demoGeneratorProgram = 0;
    PixelFixture fixture;

    ArrayList<Program> programs;

    Program activeProgram;

    private boolean demoMode = false;
    private boolean autoAnimate = false;

    public ProgramAdapter(int xCount, int yCount)
    {
        fixture = new PixelFixture(xCount, yCount);
        programs = LoadPrograms();
        activeProgram = programs.get(0);

    }

    private ArrayList<Program> LoadPrograms()
    {
        ArrayList<Program> programList = new ArrayList<>();
        SAXBuilder builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
        try
        {
            Document programsDoc = builder.build("programs.xml");
            Element rootElement = programsDoc.getRootElement();

            for(Element programElement : rootElement.getChildren("program"))
            {
                Program program = Program.deserialize(fixture, programElement);
                programList.add(program);
            }
        }
        catch (JDOMException | IOException e)
        {
            e.printStackTrace();
        }

        return programList;
    }

    public void Solve()
    {
        activeProgram.setHue(hue);
        activeProgram.setSaturation(saturation);
        activeProgram.setBrightness(brightness);

        if(autoAnimate)
        {
            offset += animationDelta;
            offset = PixelMath.Wrap(offset, 0.0f, 1.0f);
        }

        activeProgram.Solve(offset);
    }

    public void Set(String attributeName, float value)
    {

        switch (attributeName)
        {
            case "Hue":
                this.hue = value;
                break;
            case "Saturation":
                this.saturation = value;
                break;
            case "Brightness":
                this.brightness = value;
                break;
            case "Offset":
                this.offset = value;
                break;
        }
    }

    @Override
    public Element serialize()
    {
        Element adapterElement = new Element("adapter");

        adapterElement.setAttribute("method", "program");
        adapterElement.setAttribute("xCount", fixture.GetXDimensions() + "");
        adapterElement.setAttribute("yCount", fixture.GetYDimensions() + "");
        return adapterElement;
    }

    @Override
    public int getDMXLength(int pixelCount)
    {
        return 5; //red, green, blue, offset, program
    }

    @Override
    public byte[] adaptDMX(byte[] dmx, int pixelCount)
    {
        Color color = Color.rgb(dmx[0] & 0xFF, dmx[1] & 0xFF, dmx[2] & 0xFF);
        Set("Hue", (float)color.getHue() / 360.0f);
        Set("Saturation", (float)color.getSaturation());
        Set("Brightness", (float)color.getBrightness());
        Set("Offset", (float)dmx[3] / 256.0f);

        Solve();
        byte[] pixelData = new byte[pixelCount * 3];
        byte[] sourceData = fixture.GetRGBData();
        System.arraycopy(sourceData, 0, pixelData, 0, pixelCount * 3);
        return pixelData;
    }
}
