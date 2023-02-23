package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentReceiptNotificationRequest extends NotificationRequest{

    DocumentBuilder builder = null;
    Document doc = null;

    public PaymentReceiptNotificationRequest (
            String newLanguageCode,
            String newEmail,
            String newCustomerIdType,
            String newCustomerId,
            String xmlString
    ) {
        super( "PAYMNTRECEIPTNOTFN",
                newLanguageCode.toUpperCase() + "-" + "PAYMNTRECEIPTNOTFN" + "-EM",
                "PAYMNTRECEIPTNOTFN",
                newLanguageCode,
                "EMAIL",
                newEmail,
                newCustomerIdType,
                newCustomerId
                );
        mapXmlToNotificationRequest(xmlString);


    }

    public void mapXmlToNotificationRequest(String xmlString) {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        InputSource is = new InputSource(new StringReader(xmlString));
        try {
            doc = builder.parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        doc.getDocumentElement().normalize();
        maybePutValues("AccountNumber", "ACCOUNTID");
        maybePutValues("PrintedDate", "PRINTED_DATE");
        maybePutValues("OfficialName", "CUSTOMER_NAME");
        maybePutValues("BillingReceiptNr", "RECPT_NBR");
        maybePutValues("ReferenceTransactionType", "REF_TRANS_TYPE");
        maybePutValues("ReferenceTransactionNumber", "REF_TRANS_NBR");
        maybePutValues("CurrencySymbol", "CURRENCY");
        maybePutValues("PaymentDisplayStatus", "PAY_DISPLAY_STATUS");
        maybePutValues("ReferenceTransactionDate", "PAY_TRANS_DATE");
        maybePutValues("ReferenceTransactionType", "PAY_TRANS_TYPE");
        maybePutCurrencyAmount();
        maybePutAddress();
        maybePutCompanyLine();
    }

    private void maybePutValues(String xmlNode, String newKey) {
        Node n = doc.getElementsByTagName(xmlNode).item(0);
        this.keyValues.put(newKey, n.getTextContent());
    }

    private void maybePutCurrencyAmount() {
        NodeList list = doc.getElementsByTagName("CurrencyAmount").item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().toUpperCase(Locale.ENGLISH).equals("RAWAMOUNT")) {
                this.keyValues.put("TOTAL_AMOUNT", n.getTextContent());
            }
        }
    }

    private void maybePutCompanyLine() {
        Map<String, String> map = getNodeAsMap("CompanyAddress");
        for(String k : map.keySet()) {
            this.keyValues.put(maybeMapCompanyNodeName(k), map.get(k));
        }
    }

    private void maybePutAddress() {
        Map<String, String> map = getNodeAsMap("AddressDetails");
        for(String k : map.keySet()) {
            keyValues.put(maybeMapAddressNodeName(k), map.get(k));
            this.keyValues.put(maybeMapAddressNodeName(k), map.get(k));
        }
    }

    private String maybeMapCompanyNodeName(String nodeName) {
        if(nodeName == null) { return null; }
        switch(nodeName.toUpperCase(Locale.ENGLISH)) {
            case "LINE1" : return "COMPANY_LINE_1";
            case "LINE2" : return "COMPANY_LINE_2";
            case "LINE3" : return "COMPANY_LINE_3";
        }
        return nodeName.toUpperCase(Locale.ENGLISH);
    }

    private String maybeMapAddressNodeName(String nodeName) {
        if(nodeName == null) { return null; }
        switch(nodeName.toUpperCase(Locale.ENGLISH)) {
            case "LINE1" : return "ADDRESS_LINE_1";
            case "LINE2" : return "ADDRESS_LINE_2";
            case "POSTCODE" : return "POSTAL_CODE";
        }
        return nodeName.toUpperCase(Locale.ENGLISH);
    }

    private Map<String, String> getNodeAsMap(String nodeName) {
        Map<String, String> map = new HashMap<>();
        NodeList list = doc.getElementsByTagName(nodeName).item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                map.put(n.getNodeName(), n.getTextContent());
            }
        }
        return map;
    }

}
