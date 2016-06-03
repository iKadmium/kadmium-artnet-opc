package com.flynneffectmusic.WebUI.ContextHandlers;

import com.flynneffectmusic.WebUI.FunctionalInterfaces.DeleterProvider;
import com.flynneffectmusic.WebUI.FunctionalInterfaces.SaveProvider;
import com.flynneffectmusic.WebUI.WebServer;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import net.freeutils.httpserver.HTTPServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static j2html.TagCreator.script;

/**
 * Created by kadmi on 1/05/2016.
 */
public abstract class StandardEditHandler<T> extends StandardContextHandler
{
	private EditableProperty<T>[] properties;
	private Function<String, T> supplier;
    private DeleterProvider<T> deleter;
	private SaveProvider<T> saver;
	private Function<Map<String, String>, T> constructor;

    @SafeVarargs
    protected StandardEditHandler(String title, Function<Map<String, String>, T> constructor, DeleterProvider<T> deleter, Function<String, T> supplier, SaveProvider<T> saver, EditableProperty<T>... properties)
	{
		super(title);
		this.constructor = constructor;
		this.supplier = supplier;
        this.deleter = deleter;
		this.saver = saver;
		this.properties = properties;
	}

	@Override
	public List<Tag> GetBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = new ArrayList<>();
		List<Tag> submitTags = new ArrayList<>();
		List<Tag> errorsTags = new ArrayList<>();
        List<Tag> messageTags = new ArrayList<>();

		String[] parts = request.getPath().split("/");
        String area = parts[1];
        String method = parts[2];
        String itemName = "";

		T item = null;

		switch(method)
		{
            case "delete":
                itemName = parts[parts.length - 1];
                item = supplier.apply(itemName);
                break;
            case "applyedit":
			case "edit":
				itemName = parts[parts.length - 1];
                submitTags.add(WebServer.GetSubmitButton());
				item = supplier.apply(itemName);
				break;
            case "add":
                submitTags.add(WebServer.GetSubmitButton());
                break;
		}

        Map<String, String> params = null;
        try
        {
             params = request.getParams();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        switch (method)
        {
            case "delete":
                try
                {
                    deleter.accept(item);
                    messageTags.add(WebServer.GetSuccess("Success", "The item was successfully deleted"));
                }
                catch (Exception e)
                {
                    errorsTags.add(WebServer.GetFailure("Failure", e.getMessage(), e.getStackTrace()));
                }

                break;
            case "applyedit":
                for(EditableProperty<T> property : properties)
                {
                    String oldValue = property.GetGetter().apply(item);
                    String newValue = "";
                    newValue = params.get(property.GetName());

                    if(newValue == null)
                    {
                        newValue = "off";
                    }

                    if(property.GetRelevanceGetter().apply(params) && !newValue.equals(oldValue))
                    {
                        try
                        {
                            property.GetSetter().accept(item, newValue);
                            messageTags.add(WebServer.GetSuccess("Success", property.GetName() + " was successfully changed"));
                        }
                        catch (Exception e)
                        {
                            errorsTags.add(WebServer.GetFailure("Failure", e.getMessage(), e.getStackTrace()));
                        }
                    }
                }
                break;
            case "edit":
                ContainerTag form = WebServer.GetEditForm(GetNewPath(area, "applyedit", itemName));
                for (EditableProperty<T> property : properties)
                {
                    form.with(
                            property.GetFormProvider().apply(property.GetName(), property.GetGetter().apply(item))
                    );
                }
                form.with(WebServer.GetSubmitContainer(
                        submitTags
                ));
                tags.add(form);
                break;
            case "add":
                form = WebServer.GetEditForm(GetNewPath(area, "applyadd"));
                for (EditableProperty<T> property : properties)
                {
                    form.with(
                            property.GetFormProvider().apply(property.GetName(), property.GetDefaultValue())
                    );
                }
                form.with(WebServer.GetSubmitContainer(
                        submitTags
                ));
                tags.add(form);
                break;
            case "applyadd":
                try
                {
                    item = constructor.apply(request.getParams());
                    messageTags.add(WebServer.GetSuccess("Success", "The item was successfully added"));
                }
                catch (Exception e)
                {
                    errorsTags.add(WebServer.GetFailure("Failure", e.getMessage(), e.getStackTrace()));
                }
                break;
        }

		switch(method)
		{
			case "applyedit":
				if(errorsTags.size() > 0)
				{
					messageTags.add(WebServer.GetInfo("Saving cancelled", "File was not saved because of errors"));
				}
				else if(messageTags.size() == 0)
				{
					messageTags.add(WebServer.GetInfo("Nothing changed", "None of the properties has been changed"));
				}
				else
				{
					try
					{
						saver.accept(item);
						messageTags.add(WebServer.GetSuccess("Saving succeeded", "File was successfully saved"));
					}
					catch (Exception e)
					{
						errorsTags.add(WebServer.GetFailure("Saving failed", e.getMessage(), e.getStackTrace()));
					}
				}
				break;
			case "applyadd":
				if(errorsTags.size() > 0)
				{
					messageTags.add(WebServer.GetInfo("Saving cancelled", "File was not saved because of errors"));
				}
				else
				{
					try
					{
						saver.accept(item);
						messageTags.add(WebServer.GetSuccess("Saving succeeded", "File was successfully saved"));
					}
					catch (Exception e)
					{
						errorsTags.add(WebServer.GetFailure("Saving failed", e.getMessage(), e.getStackTrace()));
					}
				}

				break;
		}

        tags.addAll(errorsTags);
		tags.addAll(messageTags);

		return tags;
	}

	@Override
	protected List<Tag> GetPostBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = super.GetPostBodyContent(request);
		String method = request.getPath().split("/")[2];
		switch(method)
		{
			case "edit":
			case "add":
				if(GetPageJS() != null)
				{
					tags.add(script().withSrc(GetPageJS()));
				}

				break;
		}
		return tags;
	}

	protected String GetPageJS()
	{
		return null;
	}

    private static String GetNewPath(String area, String method, String itemName)
    {
        return "/" + area + "/" + method + "/" + itemName;
    }

	private static String GetNewPath(String area, String method)
	{
		return "/" + area + "/" + method;
	}

}
