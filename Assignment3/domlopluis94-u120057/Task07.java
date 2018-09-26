package upm.oeg.wsld.jena;

import java.io.InputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
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
		System.out.println("");
		System.out.println("TASK 7.1");
		System.out.println("");
		
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		while(instances.hasNext()) {		
			Individual instancias = (Individual) instances.next();
			System.out.println("Individuals of 'Person': "+instancias.getURI());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		System.out.println("");
		System.out.println("TASK 7.2");
		System.out.println("");
		
		ExtendedIterator subclasses = person.listSubClasses();
		while (subclasses.hasNext())
		{
			OntClass sub = (OntClass) subclasses.next();
			System.out.println("Subclass of 'Person': "+sub.getURI());
		}
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		System.out.println("");
		System.out.println("TASK 7.3");
		System.out.println("");
		OntModel infmodel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, model);
		OntClass infperson = infmodel.getOntClass(ns + "Person");
		ExtendedIterator instances1 = infperson.listSubClasses();	
		while (instances1.hasNext()) {
			OntClass next = (OntClass) instances1.next();
			System.out.println("(indirect) Subclasses of Person: " + next.getURI());
		}
		
		instances1 = infperson.listInstances();
		while (instances1.hasNext()) {
			Individual next = (Individual) instances1.next();
			System.out.println("(indirect) Individuals of Person: "+next.getURI());
		}
	
	}
}
