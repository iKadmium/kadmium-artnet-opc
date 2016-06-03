package com.flynneffectmusic.WebUI.ContextHandlers;

import com.flynneffectmusic.WebUI.WebServer;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import net.freeutils.httpserver.HTTPServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

/**
 * Created by higginsonj on 29/04/2016.
 */
public abstract class StandardContextHandler implements HTTPServer.ContextHandler
{
    private String title;
    protected StandardContextHandler(String title)
    {
        this.title = title;
    }

    protected List<Tag> GetPostHeaderContent(HTTPServer.Request request)
    {
        return new ArrayList<>();
    }
    protected List<Tag> GetPostBodyContent(HTTPServer.Request request)
    {
	    ArrayList<Tag> tags = new ArrayList<>();
	    tags.add(script().withSrc("/assets/js/jquery.min.js"));
	    tags.add(script().withSrc("/assets/js/bootstrap.min.js"));
        return tags;
    }

    public abstract List<Tag> GetBodyContent(HTTPServer.Request request);

    @Override
    public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException
    {
	    ContainerTag body = body();
	    body.with(WebServer.GetTopNavbar(title));
	    body.with(
			    div().withClass("container-fluid").with(
					    div().withClass("row").with(
							    div().withClass("col-sm-10 col-sm-offset-1 col-md-8 col-md-offset-2 main").with(
									    h1(title).withClass("page-header"),
									    div().withClass("row").with(
											    GetBodyContent(request)
									    )
							    )
					    )
			    )
	    );
	    body.with(GetPostBodyContent(request));

        Tag htmlTag = html().with(
                WebServer.GetHeader(title, GetPostHeaderContent(request)),
                body
        );

        response.send(200, htmlTag.render());
        return 200;
    }
}
