package com.flynneffectmusic.DMXOPCAdapter.programs;


import java.util.ArrayList;
import java.util.Arrays;

public class PixelSolver
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
    PixelGenerator activeGenerator;
    ArrayList<PixelEffect> activeEffects;

    ArrayList<ArrayList<PixelEffect>> demoEffects;
    ArrayList<PixelGenerator> demoGenerators;
    private boolean demoMode = false;

    public PixelSolver(PixelFixture fixture)
    {
        this.fixture = fixture;
        activeEffects = new ArrayList<>();

        demoEffects = GetDemoEffects();
        demoGenerators = new ArrayList<>();

		/*for(File file : com.flynneffectmusic.DMXOPCAdapter.programs.ImageGenerator.GetImageFiles())
		{
			demoGenerators.add(new com.flynneffectmusic.DMXOPCAdapter.programs.ImageGenerator(file.toString()));
		}*/

        demoGenerators.add(new HSBGenerator(0.0f, 1.0f, 1.0f));

        if (demoMode)
        {
            activeGenerator = demoGenerators.get(0);
            activeEffects = demoEffects.get(0);
        }
        else
        {
            activeGenerator = new HSBGenerator(0, 0, 0);
            activeEffects = new ArrayList<>();
            activeEffects.add(new PixelApeshit(2, 0.1f));
            activeEffects.add(new PixelStrobe(2));
            activeEffects.add(new PixelChase(2, 0.5f));
        }
    }

    public void SolveInternal()
    {
        activeGenerator.SetHue(hue);
        activeGenerator.SetSaturation(saturation);
        activeGenerator.SetBrightness(brightness);

        if (activeGenerator != null)
        {
            activeGenerator.Generate(fixture, offset);
        }

        activeEffects.stream().filter(effect -> effect.IsActive()).forEach(effect -> effect.Apply(fixture, offset));

        offset += animationDelta;
        offset = PixelMath.Wrap(offset, 0.0f, 1.0f);

        if (demoMode)
        {
            DemoIncrement();
        }
    }

    private void DemoIncrement()
    {
        demoCounter--;
        if (demoCounter == 0)
        {
            demoCounter = 160;
            demoEffectsProgram++;
            if (demoEffectsProgram == demoEffects.size())
            {
                demoEffectsProgram = 0;
                demoGeneratorProgram++;
                if (demoGeneratorProgram == demoGenerators.size())
                {
                    demoGeneratorProgram = 0;
                }
                activeGenerator = demoGenerators.get(demoGeneratorProgram);
            }
            activeEffects = demoEffects.get(demoEffectsProgram);
        }
    }

    private ArrayList<ArrayList<PixelEffect>> GetDemoEffects()
    {
        ArrayList<ArrayList<PixelEffect>> effects = new ArrayList<>();

        effects.add(
            new ArrayList<>(
                Arrays.asList(
                    new Mirror(true, true)
                )
            )
        );

        effects.add(
            new ArrayList<>(
                Arrays.asList(
                    new Wave(AnimatablePropertyType.BRIGHTNESS, 0.5f, true, true, 5f)
                )
            )
        );

        effects.add(
            new ArrayList<>(
                Arrays.asList(
                    new Translate(true, false),
                    new Flip(true)
                )
            )
        );

        effects.add(
            new ArrayList<>(
                Arrays.asList(
                    new Shift(AnimatablePropertyType.HUE, (8.0f / 360f), true, true, true),
                    new Shift(AnimatablePropertyType.BRIGHTNESS, 0.15f, false, false, true)
                )
            )
        );

        effects.add(
            new ArrayList<>(
                Arrays.asList(
                    new Shift(AnimatablePropertyType.SATURATION, 0.05f, false, true, true),
                    new Shift(AnimatablePropertyType.BRIGHTNESS, 0.15f, false, false, true, 0.95f, 1.0f)
                )
            )
        );
        return effects;
    }


    public String GetType()
    {
        return "pixel";
    }

    public void Set(String attributeName, float value)
    {
        for (PixelEffect effect : activeEffects)
        {
            if (effect.SolvesForAttribute(attributeName))
            {
                effect.Set(attributeName, value);
                return;
            }
        }

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
        }
    }

    public boolean SolvesAttribute(String attribute)
    {
        for(PixelEffect effect : activeEffects)
        {
            if(effect.SolvesForAttribute(attribute))
            {
                return true;
            }
        }
        switch (attribute)
        {
            case "Hue":
            case "Saturation":
            case "Brightness":
                return true;
        }
        return false;
    }

    public float GetAttribute(String attribute)
    {
        /*for(com.flynneffectmusic.DMXOPCAdapter.programs.PixelEffect effect : activeEffects)
        {
            if(effect.SolvesForAttribute(attribute))
            {
                return effect.Get(attribute);
            }
        }*/
        switch (attribute)
        {
            case "Hue":
                return hue;
            case "Saturation":
                return saturation;
            case "Brightness":
                return brightness;
            default:
                return 0.0f;
        }
    }

}
