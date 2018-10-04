package upm.oeg.wsld.jena;

import java.io.InputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 * Task 07: Querying ontologies (RDFs)
 * @author elozano
 * @author isantana
 *
 */

//MARIO LUENGO GONZALEZ

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
		
//		ExtendedIterator instances = person.listInstances();
		ExtendedIterator<Individual> instances = model.listIndividuals(person);
		
		System.out.println("List of individuals of 'Person': ");
		while (instances.hasNext()) {
			Individual ind = (Individual) instances.next();
			System.out.println("Local Name: " + ind.getLocalName() + " | URI: " + ind.getURI());
		}
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator<OntClass> subclasses = person.listSubClasses();
		
		String newline = System.getProperty("line.separator");
		System.out.println(newline + "List of subClasses of 'Person': ");
		while (subclasses.hasNext()) {
			OntClass subC = (OntClass) subclasses.next();
			System.out.println("Local Name: " + subC.getLocalName() + " | URI: " + subC.getURI());
		}
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. 
		// TIP: you need some inference... **
		ExtendedIterator<OntClass> subClass = person.listSubClasses();
		
		System.out.println(newline + "Including indirect instances & subclasses: ");
		
		while (instances.hasNext()) {
			OntClass ontInst = (OntClass) instances.next();
			
			ExtendedIterator<? extends OntResource> indInstIter =  ontInst.listInstances();
			
			while (indInstIter.hasNext()) {
				OntClass inst = (OntClass) indInstIter.next();
				System.out.println("Indirect instances: " + inst);
			}
			
		}
		
		while (subClass.hasNext()) {
			OntClass ontSubC = (OntClass) subClass.next();
			
			ExtendedIterator<? extends OntResource> indSubIter =  ontSubC.listSubClasses();
			
			while (indSubIter.hasNext()) {
				OntClass sub = (OntClass) indSubIter.next();
				System.out.println("Indirect subclasses: " + sub);
			}
		}
	}
}
