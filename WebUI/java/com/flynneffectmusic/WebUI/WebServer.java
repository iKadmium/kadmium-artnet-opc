package com.flynneffectmusic.WebUI;

import com.flynneffectmusic.WebUI.ContextHandlers.Settings.SettingsHandler;
import com.flynneffectmusic.WebUI.ContextHandlers.Special.PreviewHandler;
import com.flynneffectmusic.WebUI.ContextHandlers.Special.StatusHandler;
import com.google.gson.JsonArray;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import net.freeutils.httpserver.HTTPServer;

import static j2html.TagCreator.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by higginsonj on 1/06/2015.
 */
public class WebServer
{
    private HTTPServer httpServer;

    public WebServer(int port)
    {
        httpServer = new HTTPServer(port);
        HTTPServer.VirtualHost host = httpServer.getVirtualHost(null);
        try
        {
            host.addContext("/assets", new HTTPServer.FileContextHandler(new File("assets"), "/assets"));

			host.addContext("/settings/edit", new SettingsHandler());
			host.addContext("/settings/applyedit", new SettingsHandler(), "POST");
	        host.addContext("/status", new StatusHandler());
			host.addContext("/", new PreviewHandler());

            httpServer.start();
            System.out.println("Web server listening on port " + port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        httpServer.stop();
    }

    public static ContainerTag GetHeader(String title, Collection<Tag> headerAdditions)
    {
        ContainerTag header = head().with(
                    title(title),
					meta().withName("viewport").withContent("width=device-width, initial-scale=1"),
                    link().withRel("stylesheet").withHref("/assets/css/bootstrap.min.css"),
                    link().withRel("stylesheet").withHref("/assets/css/dashboard.css"),
                    link().withRel("stylesheet").withHref("/assets/css/fixtures.css")
                );
        header.children.addAll(headerAdditions);
        return header;
    }

	public static Tag GetSuccess(String title, String successString)
	{
		return div().withClass("alert alert-success").with(
				strong(title),
				br(),
				text(successString)
		);
	}

	public static Tag GetFailure(String title, String failureMessage)
	{
		return div().withClass("alert alert-danger").with(
				strong(title),
				br(),
				text(failureMessage)
		);
	}

    public static Tag GetFailure(String title, String message, StackTraceElement[] stackTraceElements)
    {
        ContainerTag div = div().withClass("alert alert-danger").with(
                strong(title + " - " + message),
                br(),
                br());
        for(StackTraceElement element : stackTraceElements)
        {
            div.with(text(element.toString()), br());
        }
        return div;
    }

    public static ContainerTag GetInfo(String title, String noticeMessage)
    {
        return div().withClass("alert alert-info").with(
                strong(title),
                br(),
                text(noticeMessage)
        );
    }

    private static Tag GetNavbarItem(String currentPage, String title, String href)
    {
        if(currentPage.equals(title))
        {
            return li().with(a(title).withHref(href)).withClass("active");
        }
        else
        {
            return li().with(a(title).withHref(href));
        }
    }

    public static ContainerTag GetTopNavbar(String title)
    {
        return nav().withClass("navbar navbar-inverse navbar-fixed-top").with(
                div().withClass("container-fluid").with(
                        div().withClass("navbar-header").with(
								button().withType("button").withClass("navbar-toggle").withData("toggle", "collapse").withData("target", "#topNavbar").with(
									span().withClass("icon-bar"),
									span().withClass("icon-bar"),
									span().withClass("icon-bar")
								),
                                a("OSC to DMX").withHref("#").withClass("navbar-brand")
                        ),
						div().withClass("collapse navbar-collapse").withId("topNavbar").with(
							ul().withClass("nav navbar-nav").with(
								GetNavbarItem(title, "Preview", "/"),
								GetNavbarItem(title, "Status", "/status"),
								GetNavbarItem(title, "Settings", "/settings/edit")
							)
						)
                )
        );
    }

    public static ContainerTag GetTextInput(String text, String id, String defaultValue)
    {
        return div().withClass("form-group").with(
            label(text).withClass("control-label col-sm-2").attr("for", id),
            div().withClass("col-sm-10").with(
                input().withType("text").withClass("form-control").withId(id).withName(id).withValue(defaultValue)
            )
        );
    }

	public static ContainerTag GetNumberInput(String text, String id, String defaultValue)
	{
		return div().withClass("form-group").with(
				label(text).withClass("control-label col-sm-2").attr("for", id),
				div().withClass("col-sm-10").with(
						input().withType("number").withClass("form-control").withId(id).withName(id).withValue(defaultValue)
				)
		);
	}

	public static ContainerTag GetNumberInput(String text, String id, String defaultValue, int min, int max)
	{
		return div().withClass("form-group").with(
				label(text).withClass("control-label col-sm-2").attr("for", id),
				div().withClass("col-sm-10").with(
						input().withType("number").withClass("form-control").withId(id).withName(id).withValue(defaultValue).attr("min", min + "").attr("max", max + "")
				)
		);
	}

    public static ContainerTag GetTableHeader(String... headers)
    {
        ContainerTag thead = thead();
        for(String header : headers)
        {
            thead.with(
                    th(header)
            );
        }
        return thead;
    }

	public static ContainerTag GetSubmitContainer(Tag... children)
	{
		return div().withClass("form-group").with(
				div().withClass("col-sm-offset-2 col-sm-10").with(children)
		);
	}

	public static ContainerTag GetSubmitContainer(List<Tag> children)
	{
		return div().withClass("form-group").with(
				div().withClass("col-sm-offset-2 col-sm-10").with(children)
		);
	}

	public static Tag GetJSButtonLink(String text, String id, String glyph)
	{
		return button().withClass("btn btn-primary").withType("button").withId(id).withData("toggle", "confirmation").with(
				span().withClass("glyphicon " + glyph),
				text(" " + text)
		);
	}

	public static Tag GetButtonLink(String text, String href, String glyph)
	{
		return GetButtonLink(text, href, glyph, "btn btn-primary");
	}

	public static Tag GetButtonLink(String text, String href, String glyph, String buttonClass)
	{
		return a().withClass(buttonClass).withHref(href).with(
				span().withClass("glyphicon " + glyph),
				text(" " + text)
		);
	}

	public static ContainerTag GetSubmitButton()
	{
		return button().withType("submit").withClass("btn btn-success").withId("formSubmit").with(
				span().withClass("glyphicon glyphicon-ok"),
				text(" Submit")
		);
	}

	private static List<Tag> GetOptions(NameDisplayPair[] pairs, String selected)
	{
		List<String> selectedCollection = new ArrayList<>();
		selectedCollection.add(selected);
		return GetOptions(pairs, selectedCollection);
	}

	private static List<Tag> GetOptions(NameDisplayPair[] pairs, Collection<String> selected)
	{
		List<Tag> tags = new ArrayList<>();

		for(NameDisplayPair pair : pairs)
		{
			Tag option = option(pair.GetDisplay()).withValue(pair.GetName());
			if(selected.contains(pair.GetName()))
			{
				option.setAttribute("selected", null);
			}
			tags.add(option);
		}
		return tags;
	}

	public static Tag GetSelectBox(String id, String display, String selected, NameDisplayPair... nameDisplayPairs)
	{
		return div().withClass("form-group").with(
				label(display).withClass("control-label col-sm-2").attr("for", id),
				div().withClass("col-sm-10").with(
						select().withClass("form-control").withId(id).withName(id).with(
								GetOptions(nameDisplayPairs, selected)
						)
				)
		);
	}

	public static Tag GetListItem(String text, String href)
	{
		return a(text).withHref(href).withClass("list-group-item");
	}

	public static ContainerTag GetBadgeList(String text, List<Tag> badges)
	{
		return div().withClass("list-group-item clearfix").with(
				text(text),
				div().withClass("pull-right btn-group").with(
						badges
				)
		);
	}

	public static Tag GetBadge(String link, String glyph)
	{
		return GetBadge(link, glyph, "btn-default");
	}

	public static Tag GetBadge(String link, String glyph, String buttonClass)
	{
		return a().withClass("btn btn-lg " + buttonClass).withHref(link).with(
				span().withClass("glyphicon " + glyph)
		);
	}

	public static Tag GetDeleteBadge(String link, String glyph)
	{
		return button().withClass("btn btn-danger btn-lg").withData("toggle", "confirmation").withData("href", link).with(
				span().withClass("glyphicon " + glyph)
		);
	}

	public static ContainerTag GetListContainer()
	{
		return div().withClass("list-group");
	}

	public static ContainerTag GetEditForm(String url)
	{
		return form().withClass("form-horizontal").attr("role", "form").withAction(url).withMethod("POST");
	}

    public static Tag GetCheckBox(String text, String id, boolean checked)
    {
        Tag inputTag = input().withType("checkbox").withId(id).withName(id);
        if(checked)
        {
            inputTag.setAttribute("checked", null);
        }
        return div().withClass("form-group").with(
                div().withClass("col-sm-offset-2 col-sm-10 checkbox").with(
                        label().with(
                                inputTag,
                                text(text)
                        )
                )
        );
    }

	public static Tag GetCheckBox(String text, String id, String checked)
	{
		switch(checked)
		{
			case "on":
				return GetCheckBox(text, id, true);
			case "off":
			default:
				return GetCheckBox(text, id, false);
		}
	}

	public static ContainerTag GetCollectionForm(String label, String bodyID, String valueID, String addButtonID, String value, String... headerNames)
	{
		List<Tag> additionalData = new ArrayList<>();
		return GetCollectionForm(label, bodyID, valueID, addButtonID, value, additionalData, headerNames);
	}

	public static ContainerTag GetCollectionForm(String label, String bodyID, String valueID, String addButtonID, String value, List<Tag> additionalData, String... headerNames)
	{
		ContainerTag mainBody = tbody().withId(bodyID);

		ContainerTag headerRow = tr();
		for(String headerName : headerNames)
		{
			headerRow.with(th(headerName));
		}

		ContainerTag innerDiv = div().withClass("col-sm-10").with(
				table().withClass("table table-striped").with(
						thead().with(
								headerRow
						),
						mainBody
				),
				input().withType("hidden").withValue(value).withName(valueID).withId(valueID),
				WebServer.GetJSButtonLink("Add", addButtonID, "glyphicon-plus")
		);

		innerDiv.with(additionalData);

		ContainerTag div = div().withClass("form-group").with(
				label(label).withClass("control-label col-sm-2").attr("for", valueID),
			innerDiv
		);

		return div;
	}

	public static Tag GetHiddenData(String name, String value)
	{
		return input().withType("hidden").withValue(value).withName(name).withId(name);
	}

	public static Tag GetMultiSelectList(String label, String name, String defaultValue, List<Tag> extraData, NameDisplayPair... options)
	{
		Collection<String> selectedItems =  JSONHelper.ParseStringArray(defaultValue);

		ContainerTag divBody = div().withClass("col-sm-10").with(
			select().withClass("form-control").withId(name).withName(name).attr("multiple", null).with(
				GetOptions(options, selectedItems)
			)
		);

		divBody.with(extraData);

		return div().withClass("form-group").with(
				label(label).withClass("control-label col-sm-2").attr("for", name),
				divBody
		);
	}
}

