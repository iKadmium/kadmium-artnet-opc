package com.flynneffectmusic.WebUI.ContextHandlers;

import com.flynneffectmusic.WebUI.FunctionalInterfaces.FormProvider;
import com.flynneffectmusic.WebUI.FunctionalInterfaces.SetterProvider;

import java.util.Map;
import java.util.function.*;

/**
 * Created by kadmi on 1/05/2016.
 */
public class EditableProperty<T>
{
	public String GetName()
	{
		return name;
	}


	public Function<T, String> GetGetter()
	{
		return getter;
	}

	public SetterProvider<T> GetSetter()
	{
		return setter;
	}

	public FormProvider GetFormProvider()
	{
		return formProvider;
	}

	public String GetDefaultValue()
	{
		return defaultValue;
	}

	public Function<Map<String, String>, Boolean> GetRelevanceGetter()
	{
		return relevanceGetter;
	}

	private String name;
	private String defaultValue;
	private FormProvider formProvider;
	private Function<T, String> getter;
	private SetterProvider<T> setter;
	private Function<Map<String, String>, Boolean> relevanceGetter;

	public EditableProperty(String name, String defaultValue, FormProvider formProvider , Function<T, String> getter, SetterProvider<T> setter)
	{
		this(name, defaultValue, formProvider, getter, setter, params -> true);
	}

	public EditableProperty(String name, String defaultValue, FormProvider formProvider , Function<T, String> getter, SetterProvider<T> setter, Function<Map<String, String>, Boolean> relevanceGetter)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		this.formProvider = formProvider;
		this.getter = getter;
		this.setter = setter;
		this.relevanceGetter = relevanceGetter;
	}
}

