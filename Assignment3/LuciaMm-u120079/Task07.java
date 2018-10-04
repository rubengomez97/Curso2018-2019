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
		OntClass person = model.getOntClass(ns + "Person");
		ExtendedIterator instances = person.listInstances();
		
		while(instances.hasNext()) {
			Individual i = (Individual)instances.next();
			System.out.println("Individual of Person --> " + i.getURI());
		} // END WHILE
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		while(subclasses.hasNext()) {
			OntClass sc = (OntClass)subclasses.next();
			System.out.println("Subclass of Person --> " + sc.getURI());
		} // END WHILE
		
		// ** TASK 7.3: Make the necessary changes to get
		// as well indirect instances and subclasses.
		// TIP: you need some inference... **
		OntModel inference_model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, model);
		OntClass inference_person = inference_model.getOntClass(ns + "Person");
		ExtendedIterator instance = inference_person.listInstances();
		ExtendedIterator subclass = inference_person.listSubClasses();
		
		while(instance.hasNext()) {
			Individual individual_instance = (Individual)instance.next();
			System.out.println("Person --> " + individual_instance.getURI());
		} // END WHILE
		
		while (subclass.hasNext()) {
			OntClass individual_subclass = (OntClass)subclass.next();
			System.out.println("Subclass --> " + individual_subclass.getURI());
		}
		
	
	}
}
