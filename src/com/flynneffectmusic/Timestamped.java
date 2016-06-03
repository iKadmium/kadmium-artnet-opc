package com.flynneffectmusic;

import java.time.Instant;

/**
 * Created by higginsonj on 3/06/2016.
 */
public class Timestamped<T>
{
	private Instant timestamp;
	private T value;

	public Timestamped(T value)
	{
		this.value = value;
		timestamp = Instant.now();
	}

	public Instant getTimestamp()
	{
		return timestamp;
	}

	public T getValue()
	{
		return value;
	}
}
