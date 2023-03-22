package software.daveturner.np2transformer;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class App {

  public static void main(String[] args)  {

    App app = new App();
    //String paymentReceiptXML = app.readXML("Payment-XMLReceipt.xml");
    //invokePayReceipt(paymentReceiptXML);

    String invoiceXML = app.readXML("XMLInvoice.xml");
    invoiceInvoiceReceipt(invoiceXML);




  }

  private static void invoiceInvoiceReceipt(String xml) {
    System.out.println("Mappings for Invoice");
    InvoiceXMLMapper mapper = new InvoiceXMLMapper(xml);
    System.out.println("show me the Contact object");
    System.out.println(mapper.getContactXML());
    System.out.println("show me the Payment receipt address object");
    System.out.println(mapper.getAddressXML());
    System.out.println("show me the Company receipt address object");
    System.out.println(mapper.getCompanyXML());
    System.out.println("show me the invoice object");
    System.out.println(mapper.getInvoiceXML());
    System.out.println("show me the Adjustment object");
    System.out.println(mapper.getAdjustmentsXML());
    System.out.println("show me the Payments object");
    System.out.println(mapper.getPaymentsXML());
    /*
    System.out.println("show me the MRC object");
    System.out.println(mapper.getMrcXML());
    System.out.println("show me the NRC object");
    System.out.println(mapper.getNrcXML());
    System.out.println("show me the Cancellation object");
    System.out.println(mapper.getCancelXML());
    System.out.println("show me the Adjustments object");
    System.out.println(mapper.getAdjustmentsXML());


     */
    System.out.println("**************************************************");
    System.out.println("");
    System.out.println("");
  }

  private static void invokePayReceipt(String xml) {
    System.out.println("Mappings for Payment receipt");
    PaymentReceiptXMLMapper mapper = new PaymentReceiptXMLMapper(xml);
    System.out.println("show me the Contact object");
    System.out.println(mapper.getContactXML());
    System.out.println("show me the Payment receipt address object");
    System.out.println(mapper.getAddressXML());
    System.out.println("show me the Company receipt address object");
    System.out.println(mapper.getCompanyXML());
    System.out.println("show me the Payment Detail object");
    System.out.println(mapper.getPaymentDetailXML());
    System.out.println("show me the Orders Array");
    System.out.println(mapper.getOrdersXML());
    System.out.println("**************************************************");
    System.out.println("");
    System.out.println("");

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