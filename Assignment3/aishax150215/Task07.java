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
 * @author Aisha Aljaber
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
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();

		System.out.println("People who belong to the class Person: " );

		while (instances.hasNext())
		{
			Individual name = (Individual) instances.next();
			System.out.println("-> " + name.getURI() );
		}
		System.out.println("\n");

				

		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		
		System.out.println("Subclasses who belong to the class Person: " );

		while (subclasses.hasNext())
		{
			OntClass sub = (OntClass) subclasses.next();
			System.out.println("-> " + sub.getURI());
		}
		System.out.println("\n");

		
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		
		OntModel model2 = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, model);
		OntClass person2 = model2.getOntClass(ns+"Person");
		ExtendedIterator instances2 = person2.listInstances();
		ExtendedIterator subclasses2 = person2.listSubClasses();
		
	
		System.out.println("People who belong to the indirect class Person: " );

		while (instances2.hasNext())
		{
			Individual name2 = (Individual) instances2.next();
			System.out.println("-> "+ name2.getURI());
		}
		System.out.println("\n");
	
		System.out.println("Indirect subclasses who belong to the class Person: " );

		while (subclasses2.hasNext())
		{
			OntClass sub2 = (OntClass) subclasses2.next();
			System.out.println("-> " + sub2.getURI());
		}
		System.out.println("\n");

	}
	
}
