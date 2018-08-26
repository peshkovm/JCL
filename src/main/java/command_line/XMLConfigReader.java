package command_line;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

class XMLConfigReader {
    static String find(String commandName) {
        String className = "";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("C:/JavaLessons/JCL/src/main/resources/config.xml"));
            document.getDocumentElement().normalize();

            NodeList commandList = document.getElementsByTagName("command");

            for (int i = 0; i < commandList.getLength(); i++) {
                if (!(commandList.item(i) instanceof Element))
                    continue;
                Node command = commandList.item(i);
                NamedNodeMap commandAttrs = command.getAttributes();
                if (commandAttrs.getNamedItem("name").getNodeValue().equals(commandName)) {
                    className = commandAttrs.getNamedItem("class").getNodeValue();
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return className;
    }
}