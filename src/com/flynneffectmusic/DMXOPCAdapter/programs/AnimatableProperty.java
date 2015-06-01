package com.flynneffectmusic.DMXOPCAdapter.programs;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by higginsonj on 24/10/2014.
 */
public class AnimatableProperty
{
    Consumer<Float> setter;
    Supplier<Float> getter;

    public AnimatableProperty(Consumer<Float> setter, Supplier<Float> getter)
    {
        this.getter = getter;
        this.setter = setter;
    }

    public void Set(float value)
    {
        setter.accept(value);
    }

    public float Get()
    {
        return getter.get();
    }
}
