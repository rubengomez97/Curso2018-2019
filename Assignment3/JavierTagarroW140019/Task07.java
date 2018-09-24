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
		System.out.println("\nTask 7.1");
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		while(instances.hasNext()) {
			Individual aux = (Individual) instances.next();
			System.out.println("Person: "+aux.getURI());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		System.out.println("\nTask 7.2");
		ExtendedIterator subclasses = person.listSubClasses();
		while(subclasses.hasNext()) {
			OntClass aux = (OntClass) subclasses.next();
			System.out.println("Subclass of Person: "+aux.getURI());
		}
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		System.out.println("\nTask 7.3");
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, model);
		OntClass person2 = m.getOntClass(ns+"Person");
		ExtendedIterator it1 = person2.listInstances();
		ExtendedIterator it2 = person2.listSubClasses();
		while(it1.hasNext()) {
			Individual aux1 = (Individual) it1.next();
			System.out.println("Person: "+aux1.getURI());
		}
		while(it2.hasNext()) {
			OntClass aux2 = (OntClass) it2.next();
			System.out.println("Subclass of Person: "+aux2.getURI());
		}
	
	}
}
