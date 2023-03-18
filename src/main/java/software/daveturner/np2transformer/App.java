package software.daveturner.np2transformer;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class App {

  public static void main(String[] args)  {

    App app = new App();
    String paymentReceiptXML = app.readXML("Payment-XMLReceipt.xml");
    invokePayReceipt(paymentReceiptXML);

    String invoiceXML = app.readXML("XMLInvoice.xml");
    invoiceInvoiceReceipt(invoiceXML);




  }

  private static void invoiceInvoiceReceipt(String xml) {
    System.out.println("here is source");
    System.out.println(xml);

  }

  private static void invokePayReceipt(String xml) {
    PaymentReceiptXMLMapper mapper = new PaymentReceiptXMLMapper(xml);
    System.out.println("show me the Payment receipt address object");
    System.out.println(mapper.getAddressXML());
    System.out.println("show me the Company receipt address object");
    System.out.println(mapper.getCompanyXML());
    System.out.println("show me the Payment Detail object");
    System.out.println(mapper.getPaymenntDetailXML());
    System.out.println("show me the Orders Array");
    System.out.println(mapper.getOrdersXML());
  }

  public String readXML(String fileName) {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new BufferedReader(new InputStreamReader
            (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c;
      while ((c = reader.read()) != -1) {
        textBuilder.append((char) c);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return textBuilder.toString();
  }
}