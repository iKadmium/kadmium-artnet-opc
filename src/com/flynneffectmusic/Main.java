package com.flynneffectmusic;

import com.flynneffectmusic.DMXOPCAdapter.DMXOPCAdapter;
import com.flynneffectmusic.Listener.DMXListener;
import net.freeutils.httpserver.HTTPServer;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Main
{
    static String settingsFile = "settings.xml";
    static int updatesPerSecond = 0;

    static Timer timer;

    static short dmxAddress = 1;

    static int pixelCount = 0;

    static DMXOPCAdapter adapter;
    static DMXListener listener;
    static OPCTransmitter transmitter;

    static int webServerPort;

    static net.freeutils.httpserver.HTTPServer httpServer;

    public static void main(String[] args)
    {
        readXML();

        try
        {
            startWebserver(webServerPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        byte[] dmx = null;
        byte[] pixelData;

        setupTimer();

        while(true)
        {
            if(listener.read())
            {
                updatesPerSecond++;
                dmx = listener.getDMX(dmxAddress - 1, adapter.getDMXLength(pixelCount));
                pixelData = adapter.adaptDMX(dmx, pixelCount);

                transmitter.SendPixels(pixelData);
            }
        }
    }

    public static void readXML()
    {
        SAXBuilder builder = new SAXBuilder();
        try
        {
            Document settingsDoc = builder.build(settingsFile);
            Element rootElement = settingsDoc.getRootElement();

            dmxAddress = Short.parseShort(rootElement.getChild("dmxAddress").getValue());
            pixelCount = Integer.parseInt(rootElement.getChild("pixelCount").getValue());
            transmitter = OPCTransmitter.deserialize(rootElement.getChild("opcTransmitter"));
            adapter = DMXOPCAdapter.deserialize(rootElement.getChild("adapter"));
            listener = DMXListener.deserialize(rootElement.getChild("listener"));


        }
        catch (JDOMException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void setupTimer()
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("Updates per second: " + updatesPerSecond);
                updatesPerSecond = 0;
            }
        };

        timer = new Timer("Update output timer", true);
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    private static void startWebserver(int port) throws IOException
    {
        httpServer = new HTTPServer(port);

        HTTPServer.VirtualHost host = httpServer.getVirtualHost(null);

        host.addContext("/", getRootHandler());

        httpServer.start();
    }

    private static HTTPServer.ContextHandler getRootHandler() throws IOException
    {
        HTTPServer.ContextHandler handler = (request, response) ->
        {
            String message = "";
            switch(request.getMethod())
            {
                default:
                case "GET":
                    message = "ready";
                    break;
                case "POST":
                    dmxAddress = Short.parseShort(request.getParams().get("dmxAddress"));
                    short universe = Short.parseShort(request.getParams().get("universe"));
                    if(universe != listener.getUniverse())
                    {
                        listener.setUniverse(universe);
                    }
                    pixelCount = Integer.parseInt(request.getParams().get("pixelCount"));
                    transmitter.setChannel(Integer.parseInt(request.getParams().get("opcChannel")));
                    listener.setListenAddress(request.getParams().get("listenAddress"));

                    save();

                    message = "Saved successfully";
                    break;
            }

            String responseContent = getIndexContent(message);
            response.send(200, responseContent);

            return 0;
        };

        return handler;
    }

    public static Element createElement(String name, String value)
    {
        Element element = new Element(name);
        element.setText(value);
        return element;
    }

    private static void save()
    {
        Element rootElement = new Element("settings");
        Document settingsDoc = new Document(rootElement);

        rootElement.getChildren().add(createElement("dmxAddress", dmxAddress + ""));
        rootElement.getChildren().add(createElement("pixelCount", pixelCount + ""));
        rootElement.getChildren().add(transmitter.serialize());
        rootElement.getChildren().add(adapter.serialize());
        rootElement.getChildren().add(listener.serialize());

        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try
        {
            xmlOutputter.output(settingsDoc, new FileOutputStream(settingsFile));
        }
        catch (IOException e)
        {
            System.err.println("Unable to write to settings file " + settingsFile);
            e.printStackTrace();
        }

    }

    private static String getIndexContent(String message) throws IOException
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

        fieldset.getChildren().addAll(getInputTextSet("DMX Address", "dmxAddress", dmxAddress + ""));
        fieldset.getChildren().addAll(getInputTextSet("Universe", "universe", listener.getUniverse() + ""));
        fieldset.getChildren().addAll(getInputTextSet("Pixel Count", "pixelCount", pixelCount + ""));
        fieldset.getChildren().addAll(getInputTextSet("OPC Channel", "opcChannel", transmitter.getChannel() + ""));

        Element listenAddress = new Element("select");
        listenAddress.setAttribute("id", "listenAddress");
        try
        {
            for(NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces()) )
            {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses()))
                {
                    if(inetAddress instanceof Inet4Address)
                    {
                        Element addressOption = new Element("option");
                        addressOption.setAttribute("value", inetAddress.getHostAddress() + "");
                        addressOption.setText(networkInterface.getDisplayName() + " -> " + inetAddress.getHostAddress());
                        listenAddress.addContent(addressOption);
                        if(listener.getListenAddress().equals(inetAddress.getHostAddress()))
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
            getInputElementSet(listenAddress, "DMX Listen Address", "listenAddress", listener.getListenAddress())
        );

        Element submitElement = new Element("input");
        submitElement.setAttribute("type", "submit");
        fieldset.addContent(submitElement);

        form.getChildren().add(fieldset);

        return form;
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

    private static String getFileContents(File file) throws IOException
    {
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder builder = new StringBuilder();
        String currentLine;
        while((currentLine = reader.readLine()) != null)
        {
            builder.append(currentLine);
        }

        return builder.toString();
    }


}
