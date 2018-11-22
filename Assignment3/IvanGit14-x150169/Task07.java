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
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		while(instances.hasNext()){
			Individual persona = (Individual)instances.next();
			System.out.println("Person: "+persona.getURI());
		}

		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();

		while(subclasses.hasNext()){
			OntClass subclass = (OntClass)subclasses.next();
			System.out.println("Person: "+subclass.getURI());
		}

		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **

		OntModel model2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RDFS_INF);
		InputStream in2 = FileManager.get().open(filename);

		if (in2 == null)
			throw new IllegalArgumentException("File: "+filename+" not found");

		model.read(in, null);
		
		//Individual
		
		OntClass person2 = model2.getOntClass(ns+"Person");
		ExtendedIterator instances2 = person2.listInstances();
		while(instances2.hasNext()){
			Individual persona2 = (Individual)instances2.next();
			System.out.println("Person: "+persona2.getURI());
		}

		//SubClasses
		
		ExtendedIterator subclasses2 = person2.listSubClasses();

		while(subclasses2.hasNext()){
			OntClass subclass2 = (OntClass)subclasses2.next();
			System.out.println("Person: "+subclass2.getURI());
		}
	}
}
