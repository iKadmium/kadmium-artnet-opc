package com.flynneffectmusic.WebUI.ContextHandlers;

import com.flynneffectmusic.WebUI.NameDisplayPair;
import com.flynneffectmusic.WebUI.WebServer;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import net.freeutils.httpserver.HTTPServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static j2html.TagCreator.script;
import static j2html.TagCreator.unsafeHtml;

/**
 * Created by kadmi on 7/05/2016.
 */
public abstract class StandardListContextHandler extends StandardContextHandler
{
	private Function<String, Tag>[] badgeGetters;
	protected StandardListContextHandler(String title, Function<String, Tag>... badgeGetters)
	{
		super(title);
		this.badgeGetters = badgeGetters;
	}

	public abstract Collection<NameDisplayPair> GetItems();

	@Override
	public List<Tag> GetBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = new ArrayList<>();
		ContainerTag containerTag = WebServer.GetListContainer();
		String[] parts = request.getPath().split("/");
		String area = parts[parts.length - 1];
		for(NameDisplayPair item : GetItems())
		{
			List<Tag> badges = new ArrayList<>();
			for(Function<String, Tag> badgeGetter : badgeGetters)
			{
				Tag badge = badgeGetter.apply(item.GetName());
				badges.add(badge);
			}
			containerTag.with(WebServer.GetBadgeList(item.GetDisplay(), badges));
		}
		tags.add(containerTag);
		tags.add(WebServer.GetButtonLink("Add", area + "/add", "glyphicon-plus"));
		return tags;
	}

	@Override
	protected List<Tag> GetPostBodyContent(HTTPServer.Request request)
	{
		List<Tag> tags = super.GetPostBodyContent(request);
		tags.add(script().withSrc("/assets/js/bootstrap-tooltip.js"));
		tags.add(script().withSrc("/assets/js/bootstrap-confirmation.js"));
		tags.add(script().with(
				unsafeHtml("$('[data-toggle=\"confirmation\"]').confirmation()")
		));
		return tags;
	}
}
