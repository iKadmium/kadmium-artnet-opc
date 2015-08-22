package com.flynneffectmusic.DMXOPCAdapter;


import com.flynneffectmusic.DMXOPCAdapter.programs.*;
import com.flynneffectmusic.DMXOPCAdapter.programs.effects.Apeshit;
import com.flynneffectmusic.DMXOPCAdapter.programs.effects.Strobe;
import com.sun.istack.internal.NotNull;
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
    float strobe = 0.0f;
    float apeshit = 0.0f;
    PixelFixture fixture;

    ArrayList<Program> programs;

    Strobe strobeEffect;
    Apeshit apeshitEffect;
    Program activeProgram;

    private boolean demoMode = false;
    private boolean autoAnimate = false;

    public ProgramAdapter(int xCount, int yCount)
    {
        fixture = new PixelFixture(xCount, yCount);
        programs = LoadPrograms();
        activeProgram = programs.get(0);
        strobeEffect = new Strobe(1);
        apeshitEffect = new Apeshit(1, 1);
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
        strobeEffect.Apply(fixture, strobe);
        apeshitEffect.Apply(fixture, apeshit);
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
            case "Strobe":
                this.strobe = value;
                break;
            case "Apeshit":
                this.apeshit = value;
                break;
            case "Program":
                float activeNumber = value * (programs.size() - 1);
                int programInt = (int)Math.floor(activeNumber);
                activeProgram = programs.get(programInt);
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
        return 7; //red, green, blue, strobe, offset, program
    }

    @Override
    public byte[] adaptDMX(@NotNull byte[] dmx, int pixelCount)
    {
        Color color = Color.rgb(dmx[0] & 0xFF, dmx[1] & 0xFF, dmx[2] & 0xFF);

        Set("Hue", (float)color.getHue() / 360.0f);
        Set("Saturation", (float)color.getSaturation());
        Set("Brightness", (float)color.getBrightness());
        Set("Strobe", getValue(dmx[3]));
        Set("Apeshit", getValue(dmx[4]));
        Set("Offset", getValue(dmx[5]));
        Set("Program", getValue(dmx[6]));

        Solve();
        byte[] pixelData = new byte[pixelCount * 3];
        byte[] sourceData = fixture.GetRGBData();
        System.arraycopy(sourceData, 0, pixelData, 0, pixelCount * 3);
        return pixelData;
    }

    float getValue(byte source)
    {
        return (float)(source & 0xFF) / 255.0f;
    }
}
