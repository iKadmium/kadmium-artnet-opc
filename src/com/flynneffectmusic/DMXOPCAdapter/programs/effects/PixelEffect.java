package com.flynneffectmusic.DMXOPCAdapter.programs.effects;

import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;
import org.jdom2.Element;

public abstract class PixelEffect
{
	public abstract void Apply(PixelFixture fixture, float offset);
    public abstract void Set(String attribute, float value);
    public abstract boolean SolvesForAttribute(String attribute);
    public abstract boolean IsActive();

    public static PixelEffect deserialize(Element effectElement)
    {
        switch(effectElement.getName())
        {
            case "apeshit":
                return Apeshit.deserialize(effectElement);
            case "chase":
                return Chase.deserialize(effectElement);
            case "flip":
                return Flip.deserialize(effectElement);
            case "mirror":
                return Mirror.deserialize(effectElement);
            case "shift":
                return Shift.deserialize(effectElement);
            case "strobe":
                return Strobe.deserialize(effectElement);
            case "translate":
                return Translate.deserialize(effectElement);
            case "wave":
                return Wave.deserialize(effectElement);
            default:
                return null;
        }
    }
}