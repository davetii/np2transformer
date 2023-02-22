package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class App {

  public static void main(String[] args)  {

    // read a file from classpath
    App app = new App();
    String xmlString = null;
    try {
      xmlString  = app.readPaymentXML();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    NotificationRequest request = PaymentReceiptNotificationRequest.init("EN-US", "bob@nowhere.com", "profileid", "12345-wedf");
    PaymentReceiptNotificationMapper mapper = new PaymentReceiptNotificationMapper();
    mapper.mapXmlToNotificationRequest(request, xmlString);
    System.out.println(request.toJson());

  }

  public String readPaymentXML() throws IOException {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Payment-XMLReceipt.xml");
    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new BufferedReader(new InputStreamReader
            (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c = 0;
      while ((c = reader.read()) != -1) {
        textBuilder.append((char) c);
      }
    }
    return textBuilder.toString();
  }
}