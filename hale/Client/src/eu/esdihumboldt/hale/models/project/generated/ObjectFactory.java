//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.05 at 03:20:55 PM MESZ 
//


package eu.esdihumboldt.hale.models.project.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.esdihumboldt.hale.models.project.generated package. 
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

    private final static QName _ContextElement_QNAME = new QName("", "ContextElement");
    private final static QName _Task_QNAME = new QName("", "Task");
    private final static QName _MappedSchema_QNAME = new QName("", "MappedSchema");
    private final static QName _TaskStatus_QNAME = new QName("", "TaskStatus");
    private final static QName _HaleProject_QNAME = new QName("", "HaleProject");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.esdihumboldt.hale.models.project.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TaskStatus }
     * 
     */
    public TaskStatus createTaskStatus() {
        return new TaskStatus();
    }

    /**
     * Create an instance of {@link MappedSchema }
     * 
     */
    public MappedSchema createMappedSchema() {
        return new MappedSchema();
    }

    /**
     * Create an instance of {@link Task }
     * 
     */
    public Task createTask() {
        return new Task();
    }

    /**
     * Create an instance of {@link HaleProject }
     * 
     */
    public HaleProject createHaleProject() {
        return new HaleProject();
    }

    /**
     * Create an instance of {@link ContextElement }
     * 
     */
    public ContextElement createContextElement() {
        return new ContextElement();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContextElement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ContextElement")
    public JAXBElement<ContextElement> createContextElement(ContextElement value) {
        return new JAXBElement<ContextElement>(_ContextElement_QNAME, ContextElement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Task }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Task")
    public JAXBElement<Task> createTask(Task value) {
        return new JAXBElement<Task>(_Task_QNAME, Task.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MappedSchema }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MappedSchema")
    public JAXBElement<MappedSchema> createMappedSchema(MappedSchema value) {
        return new JAXBElement<MappedSchema>(_MappedSchema_QNAME, MappedSchema.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TaskStatus")
    public JAXBElement<TaskStatus> createTaskStatus(TaskStatus value) {
        return new JAXBElement<TaskStatus>(_TaskStatus_QNAME, TaskStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HaleProject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "HaleProject")
    public JAXBElement<HaleProject> createHaleProject(HaleProject value) {
        return new JAXBElement<HaleProject>(_HaleProject_QNAME, HaleProject.class, null, value);
    }

}
