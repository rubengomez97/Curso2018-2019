package main.java.upm.oeg.wsld.jena;
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
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		
		while(instances.hasNext()) {
		Individual 	a = (Individual) instances.next();
		System.out.print("List of individuals:");
		System.out.print(a.getURI());
		}
		
		
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		while(subclasses.hasNext()) {
		OntClass	a = (OntClass) subclasses.next();
		System.out.print("List of subclasses:");
		System.out.print(a.getURI());		
		}
		
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		
		OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, model);
	
		OntClass person1 = mod.getOntClass(ns+"Person");
		ExtendedIterator b = person1.listInstances();
	
		
		while(b.hasNext()) {
			Individual i1=(Individual) b.next();
			System.out.print("List of instances:");
			System.out.println(i1.getURI());
		}
		
		
		ExtendedIterator c = person1.listSubClasses();
	
		while(c.hasNext()) {
			OntClass s1 = (OntClass) c.next();
			System.out.print("List of subclasses:");
			System.out.println(s1.getURI());
		}

		
		
		
	}
}
