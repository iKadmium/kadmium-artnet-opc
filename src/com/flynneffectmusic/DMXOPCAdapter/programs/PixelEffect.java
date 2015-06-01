package com.flynneffectmusic.DMXOPCAdapter.programs;

import com.flynneffectmusic.DMXOPCAdapter.programs.PixelFixture;

public abstract class PixelEffect
{
	public abstract void Apply(PixelFixture fixture, float offset);
    public abstract void Set(String attribute, float value);
    public abstract boolean SolvesForAttribute(String attribute);
    public abstract boolean IsActive();
}
