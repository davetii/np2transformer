package software.daveturner.np2transformer.sandbox;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XMLSandbox {

    public static void main(String[] args) {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();

// root element
        Element root = document.createElement("company");
        document.appendChild(root);

// employee element
        Element employee = document.createElement("employee");

        root.appendChild(employee);

// set an attribute to staff element
        Attr attr = document.createAttribute("id");
        attr.setValue("10");
        employee.setAttributeNode(attr);

//you can also use staff.setAttribute("id", "1") for this

// firstname element
        Element firstName = document.createElement("firstname");
        firstName.appendChild(document.createTextNode("James"));
        employee.appendChild(firstName);

// lastname element
        Element lastname = document.createElement("lastname");
        lastname.appendChild(document.createTextNode("Harley"));
        employee.appendChild(lastname);

// email element
        Element email = document.createElement("email");
        email.appendChild(document.createTextNode("james@example.org"));
        employee.appendChild(email);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        //transformer.transform(domSource, streamResult);
        StringWriter sw = new StringWriter();
        try {
            transformer.transform(new DOMSource(document), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        System.out.println(sw);
        System.out.println("Done creating XML File");
    }
}
