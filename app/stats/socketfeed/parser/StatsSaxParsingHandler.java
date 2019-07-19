package stats.socketfeed.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmaclean on 8/4/14.
 */
public class StatsSaxParsingHandler extends DefaultHandler {
    private StringBuilder sb = new StringBuilder();
    private List<String> xmlDocs = new ArrayList<>();
    private String rootNodeName;

    public StatsSaxParsingHandler(String rootNodeName) {
        this.rootNodeName = rootNodeName;
    }

    public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
        StringBuilder attrs = new StringBuilder();
        for(int i=0; i<attributes.getLength(); i++) {
            attrs.append(String.format(" %s=\"%s\"", attributes.getQName(i), attributes.getValue(i)));
        }

        sb.append("<" + qName + attrs.toString() + ">");
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        sb.append("</" + qName + ">");

        if(qName.equals(rootNodeName)) {
            xmlDocs.add(sb.toString());
            sb.setLength(0);
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {

    }

    public List<String> getXmlDocs() {
        return xmlDocs;
    }
}
