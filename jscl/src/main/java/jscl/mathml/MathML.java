package jscl.mathml;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

public class MathML {
    static Transformer xhtml;
    Node node;

    MathML(Node node) {
        this.node = node;
    }

    public MathML(String qualifiedName, String publicID, String systemID) {
        this(new CoreDocumentImpl());
        CoreDocumentImpl document = (CoreDocumentImpl) document();
        document.setXmlEncoding("utf-8");
        document.appendChild(new DocumentTypeImpl(document, qualifiedName, publicID, systemID));
    }

    static Transformer transformer() throws TransformerException {
        return xhtml == null ? xhtml = TransformerFactory.newInstance().newTransformer() : xhtml;
    }

    public Document document() {
        return node instanceof CoreDocumentImpl ? (Document) node : node.getOwnerDocument();
    }

    public MathML element(String name) {
        CoreDocumentImpl document = (CoreDocumentImpl) document();
        return new MathML(new ElementImpl(document, name));
    }

    public void setAttribute(String name, String value) {
        ((Element) node).setAttribute(name, value);
    }

    public MathML text(String data) {
        CoreDocumentImpl document = (CoreDocumentImpl) document();
        return new MathML(new TextImpl(document, data));
    }

    public void appendChild(MathML math) {
        node.appendChild(math.node);
    }

    public String toString() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            transformer().transform(new DOMSource(node), new StreamResult(os));
        } catch (TransformerException e) {
        }
        String s = os.toString();
        return s.substring(s.indexOf(">") + 1);
    }
}
