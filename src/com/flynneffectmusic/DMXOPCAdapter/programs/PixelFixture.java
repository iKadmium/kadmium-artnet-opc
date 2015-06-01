package com.flynneffectmusic.DMXOPCAdapter.programs;

import javafx.geometry.Point2D;
import org.jdom2.Element;

import java.util.*;
import java.util.stream.Collectors;

public class PixelFixture
{
    HashMap<Point2D, Pixel> pixels;
    int xCount;
    int yCount;

    public PixelFixture(String name, int xCount, int yCount)
    {
        pixels = new HashMap<>();
        this.xCount = xCount;
        this.yCount = yCount;

        for (int y = 0; y < yCount; y++)
        {
            for (int x = 0; x < xCount; x++)
            {
                Point2D point = new Point2D(x, y);
                Pixel pixel = new Pixel();
                pixels.put(point, pixel);
            }
        }
    }

    public PixelFixture(String name, Element groupElement, int xCount, int yCount)
    {
        pixels = new HashMap<>();
        this.xCount = xCount;
        this.yCount = yCount;

        for (int y = 0; y < yCount; y++)
        {
            for (int x = 0; x < xCount; x++)
            {
                Point2D point = new Point2D(x, y);
                Pixel pixel = new Pixel();
                pixels.put(point, pixel);
            }
        }
    }

    public void UpdateDisplay()
    {
        for (Pixel pixel : pixels.values())
        {
            pixel.UpdateDisplay();
        }
    }

    public HashMap<Point2D, Pixel> GetPixelMap()
    {
        return pixels;
    }

    public Pixel Get(int x, int y)
    {
        Point2D point = new Point2D(x, y);
        Pixel returnValue = pixels.get(point);
        if (returnValue == null)
        {
            throw new IllegalArgumentException("No such pixel at " + x + ", " + y + ". Dimensions are " + GetXDimensions() +"x" + GetYDimensions());
        }
        return returnValue;
    }

    public int GetXDimensions()
    {
        return xCount;
    }

    public int GetYDimensions()
    {
        return yCount;
    }

    public ArrayList<Pixel> GetPixels(boolean horizontal, boolean leftToRight, boolean topToBottom)
    {
        int majorMax = horizontal ? GetXDimensions() : GetYDimensions();
        int minorMax = horizontal ? GetYDimensions() : GetXDimensions();

        ArrayList<Pixel> returnValue = new ArrayList<>();

        for (int y = 0; y < minorMax; y++)
        {
            for (int x = 0; x < majorMax; x++)
            {
                int trueX = leftToRight ? x : (majorMax - x - 1);
                int trueY = topToBottom ? y : (minorMax - y - 1);

                if (horizontal)
                {
                    returnValue.add(Get(trueX, trueY));
                }
                else
                {
                    returnValue.add(Get(trueY, trueX));
                }
            }
        }

        return returnValue;
    }

    public ArrayList<Pixel> GetRow(int rowNumber)
    {
        ArrayList<Pixel> row = new ArrayList<>();
        for (int i = 0; i < xCount; i++)
        {
            row.add(Get(i, rowNumber));
        }
        return row;
    }

    public ArrayList<Pixel> GetColumn(int colNumber)
    {
        ArrayList<Pixel> column = new ArrayList<>();
        for (int i = 0; i < yCount; i++)
        {
            column.add(Get(colNumber, i));
        }
        return column;
    }

    public void SetColumn(int colNumber, ArrayList<Pixel> column)
    {
        for (int i = 0; i < yCount; i++)
        {
            Pixel pixel = column.get(i);
            Get(colNumber, i).SetColor(pixel.GetColor());
        }
    }

    public void SetRow(int rowNumber, ArrayList<Pixel> row)
    {
        for (int i = 0; i < xCount; i++)
        {
            Pixel pixel = row.get(i);
            Get(i, rowNumber).SetColor(pixel.GetColor());
        }
    }

    public Point2D GetPosition(Pixel pixel)
    {
        for (int x = 0; x < xCount; x++)
        {
            for (int y = 0; y < yCount; y++)
            {
                Point2D point = new Point2D(x, y);
                if (pixels.get(point) == pixel)
                {
                    return point;
                }
            }
        }
        return null;
    }

    public Collection<? extends Pixel> GetRandom(int count)
    {
        if (count > pixels.size())
        {
            throw new IllegalArgumentException(
                "Not enough pixels in set for random set that size. Asked for "
                + count + ", set contains " + pixels.size()
            );
        }
        ArrayList<Pixel> randomPixels = new ArrayList<>(count);
        randomPixels.addAll(pixels.values());
        Collections.shuffle(randomPixels);
        return randomPixels.stream().limit(count).collect(Collectors.toList());
    }

    public Collection<? extends Byte> GetRGBData()
    {
        ArrayList<Byte> bytes = new ArrayList<>();
        for (Pixel pixel : GetPixels(true, true, true))
        {
            bytes.add((byte) pixel.GetRed());
            bytes.add((byte) pixel.GetGreen());
            bytes.add((byte) pixel.GetBlue());
        }

        return bytes;
    }
}
