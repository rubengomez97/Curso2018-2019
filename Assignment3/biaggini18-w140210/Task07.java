package upm.oeg.wsld.jena;

import java.io.InputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 * Task 07: Querying ontologies (RDFs)
 * @author elozano
 * @author isantana
 *
 */
public class Task07
{
	public static String ns = "http://somewhere#";
	
	public static void main(String args[])
	{
		String filename = "resources/example6.rdf";
		
		// Create an empty model
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);
	
		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");
	
		// Read the RDF/XML file
		model.read(in, null);
		
		
		// ** TASK 7.1: List all individuals of "Person" **
		System.out.println("TASK 7.1");
		
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		while(instances.hasNext()) {
			Individual persona = (Individual) instances.next();
			System.out.println("Person: " + persona.getURI());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		System.out.println("TASK 7.2");
		
		ExtendedIterator subclasses = person.listSubClasses();
		while(subclasses.hasNext()) {
			OntClass subPerson = (OntClass) subclasses.next();
			System.out.println("Subclass of person: " + subPerson.getURI());
		}
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		System.out.println("TASK 7.3");
		
		OntModel infModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF,model);
		OntClass infPerson = infModel.getOntClass(ns+"Person");
		ExtendedIterator instancias = infPerson.listInstances();
		ExtendedIterator subclases = infPerson.listSubClasses();
		
		while(instancias.hasNext()) {
			Individual ins = (Individual) instancias.next();
			System.out.println("Person: "+ins.getURI());
		}
		
		while(subclases.hasNext()) {
			OntClass subCla = (OntClass) subclases.next();
			System.out.println("Subclass of Person: " + subCla.getURI());
		}
	
	}
}
