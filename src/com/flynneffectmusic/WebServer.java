package com.flynneffectmusic;

import net.freeutils.httpserver.HTTPServer;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by higginsonj on 1/06/2015.
 */
public class WebServer
{
    HTTPServer httpServer;

    public WebServer(int port)
    {
        httpServer = new HTTPServer(port);
        HTTPServer.VirtualHost host = httpServer.getVirtualHost(null);
        host.addContext("/", getRootHandler());
        try
        {
            httpServer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    static String getIndexContent(String message)
    {
        Element html = new Element("html");
        DocType docType = new DocType("html");
        Document doc = new Document(html, docType);

        Element form = getForm();

        html.getChildren().add(getHead());
        html.getChildren().add(getBody(message, form));

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return outputter.outputString(doc);

    }

    private static Element getForm()
    {
        Element form = new Element("form");
        form.setAttribute("method", "post");
        form.setAttribute("action", "/");
        Element fieldset = new Element("fieldset");

        fieldset.addContent(createElement("legend", "Settings"));

        fieldset.getChildren().addAll(getInputTextSet("DMX Address", "dmxAddress", Settings.getDmxAddress() + ""));
        fieldset.getChildren().addAll(getInputTextSet("Universe", "universe", Main.listener.getUniverse() + ""));
        fieldset.getChildren().addAll(getInputTextSet("Pixel Count", "pixelCount", Settings.getPixelCount() + ""));
        fieldset.getChildren().addAll(
            getInputTextSet("OPC Channel", "opcChannel", Main.transmitter.getChannel() + "")
        );

        Element listenAddress = new Element("select");
        listenAddress.setAttribute("id", "listenAddress");
        try
        {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces()))
            {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses()))
                {
                    if (inetAddress instanceof Inet4Address)
                    {
                        Element addressOption = new Element("option");
                        addressOption.setAttribute("value", inetAddress.getHostAddress() + "");
                        addressOption.setText(
                            networkInterface.getDisplayName() + " -> " + inetAddress.getHostAddress()
                        );
                        listenAddress.addContent(addressOption);
                        if (Main.listener.getListenAddress().equals(inetAddress.getHostAddress()))
                        {
                            addressOption.setAttribute("selected", "selected");
                        }
                    }
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }

        fieldset.getChildren().addAll(
            getInputElementSet(
                listenAddress, "DMX Listen Address", "listenAddress", Main.listener.getListenAddress()
            )
        );

        Element submitElement = new Element("input");
        submitElement.setAttribute("type", "submit");
        fieldset.addContent(submitElement);

        form.getChildren().add(fieldset);

        return form;
    }

    public static Element createElement(String name, String value)
    {
        Element element = new Element(name);
        element.setText(value);
        return element;
    }

    private static Collection<Element> getInputElementSet(Element element, String displayName, String id, String value)
    {
        ArrayList<Element> elements = new ArrayList<>();
        Element label = createLabel(element, displayName, id);
        Element br = new Element("br");

        elements.add(label);
        elements.add(element);
        elements.add(br);

        return elements;
    }

    private static Collection<Element> getInputTextSet(String displayName, String id, String value)
    {
        return getInputElementSet(getInputText(value), displayName, id, value);
    }

    private static Element getInputText(String value)
    {
        Element element = new Element("input");
        element.setAttribute("type", "text");
        element.setAttribute("value", value);
        return element;
    }

    private static Element createLabel(Element input, String displayText, String id)
    {
        Element label = createElement("label", displayText);
        label.setAttribute("for", id);

        input.setAttribute("id", id);
        input.setAttribute("name", id);
        return label;
    }

    private static Element getBody(String message, Element form)
    {
        Element body = new Element("body");
        Element h1 = new Element("h1");
        h1.setText("sACN to OPC");
        body.getChildren().add(h1);

        Element p = new Element("p");
        p.setText(message);
        body.getChildren().add(p);

        body.getChildren().add(form);

        return body;
    }

    private static Element getHead()
    {
        Element head = new Element("head");
        Element title = new Element("title");
        title.setText("sACN to OPC");
        head.getChildren().add(title);

        return head;
    }

    static HTTPServer.ContextHandler getRootHandler()
    {
        HTTPServer.ContextHandler handler = (request, response) ->
        {
            String message = "";
            switch (request.getMethod())
            {
                default:
                case "GET":
                    message = "ready";
                    break;
                case "POST":
                    Settings.setDmxAddress(Short.parseShort(request.getParams().get("dmxAddress")));
                    short universe = Short.parseShort(request.getParams().get("universe"));
                    if (universe != Main.listener.getUniverse())
                    {
                        Main.listener.setUniverse(universe);
                    }
                    Settings.setPixelCount(Integer.parseInt(request.getParams().get("pixelCount")));
                    Main.transmitter.setChannel(Integer.parseInt(request.getParams().get("opcChannel")));
                    Main.listener.setListenAddress(request.getParams().get("listenAddress"));

                    Settings.save();

                    message = "Saved successfully";
                    break;
            }

            String responseContent = getIndexContent(message);
            response.send(200, responseContent);

            return 0;
        };

        return handler;
    }

}
