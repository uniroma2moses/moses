
package org.moses.optimization_engine;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.moses.optimization_engine package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CalculateProcessSolutionForServiceChangeResponse_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForServiceChangeResponse");
    private final static QName _CalculateProcessSolutionForProcessCreation_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForProcessCreation");
    private final static QName _CalculateProcessSolutionForSLADeletionResponse_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForSLADeletionResponse");
    private final static QName _CalculateProcessSolutionForSLACreation_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForSLACreation");
    private final static QName _CalculateProcessSolutionForSLADeletion_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForSLADeletion");
    private final static QName _Exception_QNAME = new QName("http://optimization_engine.moses.org/", "Exception");
    private final static QName _CalculateProcessSolutionForServiceChange_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForServiceChange");
    private final static QName _CalculateProcessSolutionForQoSChangeResponse_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForQoSChangeResponse");
    private final static QName _CalculateProcessSolutionForQoSChange_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForQoSChange");
    private final static QName _ExistProcessSolutionResponse_QNAME = new QName("http://optimization_engine.moses.org/", "existProcessSolutionResponse");
    private final static QName _ExistProcessSolution_QNAME = new QName("http://optimization_engine.moses.org/", "existProcessSolution");
    private final static QName _CalculateProcessSolutionForProcessCreationResponse_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForProcessCreationResponse");
    private final static QName _CalculateProcessSolutionForSLACreationResponse_QNAME = new QName("http://optimization_engine.moses.org/", "calculateProcessSolutionForSLACreationResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.moses.optimization_engine
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForSLACreationResponse }
     * 
     */
    public CalculateProcessSolutionForSLACreationResponse createCalculateProcessSolutionForSLACreationResponse() {
        return new CalculateProcessSolutionForSLACreationResponse();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForQoSChangeResponse }
     * 
     */
    public CalculateProcessSolutionForQoSChangeResponse createCalculateProcessSolutionForQoSChangeResponse() {
        return new CalculateProcessSolutionForQoSChangeResponse();
    }

    /**
     * Create an instance of {@link ExistProcessSolution }
     * 
     */
    public ExistProcessSolution createExistProcessSolution() {
        return new ExistProcessSolution();
    }

    /**
     * Create an instance of {@link ExistProcessSolutionResponse }
     * 
     */
    public ExistProcessSolutionResponse createExistProcessSolutionResponse() {
        return new ExistProcessSolutionResponse();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForServiceChangeResponse }
     * 
     */
    public CalculateProcessSolutionForServiceChangeResponse createCalculateProcessSolutionForServiceChangeResponse() {
        return new CalculateProcessSolutionForServiceChangeResponse();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForServiceChange }
     * 
     */
    public CalculateProcessSolutionForServiceChange createCalculateProcessSolutionForServiceChange() {
        return new CalculateProcessSolutionForServiceChange();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForProcessCreationResponse }
     * 
     */
    public CalculateProcessSolutionForProcessCreationResponse createCalculateProcessSolutionForProcessCreationResponse() {
        return new CalculateProcessSolutionForProcessCreationResponse();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForSLACreation }
     * 
     */
    public CalculateProcessSolutionForSLACreation createCalculateProcessSolutionForSLACreation() {
        return new CalculateProcessSolutionForSLACreation();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForProcessCreation }
     * 
     */
    public CalculateProcessSolutionForProcessCreation createCalculateProcessSolutionForProcessCreation() {
        return new CalculateProcessSolutionForProcessCreation();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForSLADeletion }
     * 
     */
    public CalculateProcessSolutionForSLADeletion createCalculateProcessSolutionForSLADeletion() {
        return new CalculateProcessSolutionForSLADeletion();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForSLADeletionResponse }
     * 
     */
    public CalculateProcessSolutionForSLADeletionResponse createCalculateProcessSolutionForSLADeletionResponse() {
        return new CalculateProcessSolutionForSLADeletionResponse();
    }

    /**
     * Create an instance of {@link CalculateProcessSolutionForQoSChange }
     * 
     */
    public CalculateProcessSolutionForQoSChange createCalculateProcessSolutionForQoSChange() {
        return new CalculateProcessSolutionForQoSChange();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForServiceChangeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForServiceChangeResponse")
    public JAXBElement<CalculateProcessSolutionForServiceChangeResponse> createCalculateProcessSolutionForServiceChangeResponse(CalculateProcessSolutionForServiceChangeResponse value) {
        return new JAXBElement<CalculateProcessSolutionForServiceChangeResponse>(_CalculateProcessSolutionForServiceChangeResponse_QNAME, CalculateProcessSolutionForServiceChangeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForProcessCreation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForProcessCreation")
    public JAXBElement<CalculateProcessSolutionForProcessCreation> createCalculateProcessSolutionForProcessCreation(CalculateProcessSolutionForProcessCreation value) {
        return new JAXBElement<CalculateProcessSolutionForProcessCreation>(_CalculateProcessSolutionForProcessCreation_QNAME, CalculateProcessSolutionForProcessCreation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForSLADeletionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForSLADeletionResponse")
    public JAXBElement<CalculateProcessSolutionForSLADeletionResponse> createCalculateProcessSolutionForSLADeletionResponse(CalculateProcessSolutionForSLADeletionResponse value) {
        return new JAXBElement<CalculateProcessSolutionForSLADeletionResponse>(_CalculateProcessSolutionForSLADeletionResponse_QNAME, CalculateProcessSolutionForSLADeletionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForSLACreation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForSLACreation")
    public JAXBElement<CalculateProcessSolutionForSLACreation> createCalculateProcessSolutionForSLACreation(CalculateProcessSolutionForSLACreation value) {
        return new JAXBElement<CalculateProcessSolutionForSLACreation>(_CalculateProcessSolutionForSLACreation_QNAME, CalculateProcessSolutionForSLACreation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForSLADeletion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForSLADeletion")
    public JAXBElement<CalculateProcessSolutionForSLADeletion> createCalculateProcessSolutionForSLADeletion(CalculateProcessSolutionForSLADeletion value) {
        return new JAXBElement<CalculateProcessSolutionForSLADeletion>(_CalculateProcessSolutionForSLADeletion_QNAME, CalculateProcessSolutionForSLADeletion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForServiceChange }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForServiceChange")
    public JAXBElement<CalculateProcessSolutionForServiceChange> createCalculateProcessSolutionForServiceChange(CalculateProcessSolutionForServiceChange value) {
        return new JAXBElement<CalculateProcessSolutionForServiceChange>(_CalculateProcessSolutionForServiceChange_QNAME, CalculateProcessSolutionForServiceChange.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForQoSChangeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForQoSChangeResponse")
    public JAXBElement<CalculateProcessSolutionForQoSChangeResponse> createCalculateProcessSolutionForQoSChangeResponse(CalculateProcessSolutionForQoSChangeResponse value) {
        return new JAXBElement<CalculateProcessSolutionForQoSChangeResponse>(_CalculateProcessSolutionForQoSChangeResponse_QNAME, CalculateProcessSolutionForQoSChangeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForQoSChange }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForQoSChange")
    public JAXBElement<CalculateProcessSolutionForQoSChange> createCalculateProcessSolutionForQoSChange(CalculateProcessSolutionForQoSChange value) {
        return new JAXBElement<CalculateProcessSolutionForQoSChange>(_CalculateProcessSolutionForQoSChange_QNAME, CalculateProcessSolutionForQoSChange.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistProcessSolutionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "existProcessSolutionResponse")
    public JAXBElement<ExistProcessSolutionResponse> createExistProcessSolutionResponse(ExistProcessSolutionResponse value) {
        return new JAXBElement<ExistProcessSolutionResponse>(_ExistProcessSolutionResponse_QNAME, ExistProcessSolutionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistProcessSolution }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "existProcessSolution")
    public JAXBElement<ExistProcessSolution> createExistProcessSolution(ExistProcessSolution value) {
        return new JAXBElement<ExistProcessSolution>(_ExistProcessSolution_QNAME, ExistProcessSolution.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForProcessCreationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForProcessCreationResponse")
    public JAXBElement<CalculateProcessSolutionForProcessCreationResponse> createCalculateProcessSolutionForProcessCreationResponse(CalculateProcessSolutionForProcessCreationResponse value) {
        return new JAXBElement<CalculateProcessSolutionForProcessCreationResponse>(_CalculateProcessSolutionForProcessCreationResponse_QNAME, CalculateProcessSolutionForProcessCreationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalculateProcessSolutionForSLACreationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://optimization_engine.moses.org/", name = "calculateProcessSolutionForSLACreationResponse")
    public JAXBElement<CalculateProcessSolutionForSLACreationResponse> createCalculateProcessSolutionForSLACreationResponse(CalculateProcessSolutionForSLACreationResponse value) {
        return new JAXBElement<CalculateProcessSolutionForSLACreationResponse>(_CalculateProcessSolutionForSLACreationResponse_QNAME, CalculateProcessSolutionForSLACreationResponse.class, null, value);
    }

}
