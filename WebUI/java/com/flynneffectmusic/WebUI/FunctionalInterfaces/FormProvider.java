package com.flynneffectmusic.WebUI.FunctionalInterfaces;

import j2html.tags.Tag;

import java.util.function.BiFunction;

public interface FormProvider extends BiFunction<String, String, Tag>
{
	@Override
	Tag apply(String name, String defaultValue);
}
