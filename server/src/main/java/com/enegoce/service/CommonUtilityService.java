package com.enegoce.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@Service
public class CommonUtilityService {

    /////////////Convert TXT MT700 to XML////////////////
    ////////////////////////////////////////////////////
    public File convertTextToXml(File textFile, String outputFilePath) throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (Exception e) {
            throw new IOException("Error initializing XML document", e);
        }

        Element rootElement = doc.createElement("MT700");
        doc.appendChild(rootElement);

        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            String line;
            StringBuilder valueBuilder = new StringBuilder();
            String currentTag = null;
            while ((line = br.readLine()) != null) {
                if (line.contains(":") && !line.startsWith(" ")) {
                    if (currentTag != null) {
                        addField(doc, rootElement, currentTag, valueBuilder.toString());
                    }
                    String[] parts = line.split(":", 2);
                    currentTag = parts[0];
                    valueBuilder.setLength(0); // Clear the builder
                    valueBuilder.append(parts[1]);
                } else if (currentTag != null) {
                    valueBuilder.append("\n").append(line);
                }
            }
            if (currentTag != null) {
                addField(doc, rootElement, currentTag, valueBuilder.toString());
            }
        } catch (IOException e) {
            throw new IOException("Error reading text file", e);
        }

        File outputFile = new File(outputFilePath);
        try (FileWriter writer = new FileWriter(outputFile)) {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (Exception e) {
            throw new IOException("Error writing XML file", e);
        }

        return outputFile;
    }

    private void addField(Document doc, Element rootElement, String tag, String value) {
        Element field = doc.createElement("field");

        Element tagElement = doc.createElement("tag");
        tagElement.appendChild(doc.createTextNode(tag));
        field.appendChild(tagElement);

        Element valueElement = doc.createElement("value");
        valueElement.appendChild(doc.createTextNode(value.trim()));
        field.appendChild(valueElement);

        rootElement.appendChild(field);
    }

    ///////////////////////////////////////////////////

}
