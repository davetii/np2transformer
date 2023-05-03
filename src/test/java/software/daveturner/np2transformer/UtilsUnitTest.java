package software.daveturner.np2transformer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class UtilsUnitTest {


    Utils u;

    @BeforeEach
    public void setup() {
        u = new Utils();
    }

    @Test public void ensureXmlToDocReturnsExpected() {
        String xml = "<parent><child>1</child></parent>";
        Document doc = u.xmlToDoc(xml);
        Assertions.assertEquals("1", doc.getElementsByTagName("child").item(0).getTextContent());
    }

    @Test
    public void ensureRemoveRootReturnsExpected() {
        Assertions.assertEquals("<child>1</child>", u.removeRoot("<parent><child>1</child></parent>"));
        Assertions.assertEquals("<child>1</child><child>2</child>", u.removeRoot("<parent><child>1</child><child>2</child></parent>"));
        Assertions.assertEquals("<child>1</child><child><child>3</child></child>", u.removeRoot("<parent><child>1</child><child><child>3</child></child></parent>"));
    }

    @Test
    public void ensureRemoveRootReturnsExpectedWhendealing1Node() {
        Assertions.assertEquals("", u.removeRoot("<child>1</child>"));
    }

    @Test public void testCompany () {
        String input = "<CompanyAddress>" +
                "  <Line1>OnStar Customer Care</Line1>" +
                "<Line2>P.O. Box 1027</Line2>" +
                "<Line3>Warren, MI 48090-1027</Line3>  " +
                "</CompanyAddress>";

        String expectedXML = "<Line1>OnStar Customer Care</Line1>" +
                "<Line2>P.O. Box 1027</Line2>" +
                "<Line3>Warren, MI 48090-1027</Line3>";
        Assertions.assertEquals(expectedXML, u.removeRoot(input));


    }



}
