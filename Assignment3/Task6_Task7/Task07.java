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
		Individual aux1;
		OntClass aux2;
		// Create an empty model
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);
	
		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");
	
		// Read the RDF/XML file
		model.read(in, null);
		
		
		// ** TASK 7.1: List all individuals of "Person" **
		System.out.println("List all individuals of \"Person\"");
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		System.out.println("Person List \n");
		while(instances.hasNext()) {
			aux1 = (Individual) instances.next();
			System.out.println(aux1.getURI());
		}
		// ** TASK 7.2: List all subclasses of "Person" **
		System.out.println("List all subclasses of \"Person\"");
		ExtendedIterator subclasses = person.listSubClasses();
		System.out.println("SubClasses List");
		while(subclasses.hasNext()) {
			aux2 = (OntClass) subclasses.next();
			System.out.println(aux2.getURI());
		}
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		System.out.println("Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference...");
	
		System.out.println("New model");
		OntModel modelINF = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF,model);
		System.out.println("New OntClass");
		OntClass auxPerson = modelINF.getOntClass(ns+"Person");
		System.out.println("Create ExtendedIterator");
		ExtendedIterator aux3= auxPerson.listInstances();
		System.out.println("Person List");
		while(aux3.hasNext()){
		      Individual aux4 = (Individual) aux3.next();      
		      System.out.println("Individuo: "+ aux4.getLocalName());
		    }    
		System.out.println("Create ExtendedIterator");
		ExtendedIterator aux5 = auxPerson.listSubClasses();  
	    System.out.println("SubClasses List");
	    while(aux5.hasNext()){
	      OntClass subClass = (OntClass) aux5.next();      
	      System.out.println("Subclase: "+ subClass.getLocalName());
	    }
	}
}
