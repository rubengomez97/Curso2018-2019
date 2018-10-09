package upm.oeg.wsld.jena;

import java.io.InputStream;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
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
		System.out.println("\n** TASK 7.1");
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		
		while (instances.hasNext()) {
			Individual persona = (Individual) instances.next();
			System.out.println(persona.toString());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		System.out.println("\n** TASK 7.2");
		ExtendedIterator subclasses = person.listSubClasses();
		
		while (subclasses.hasNext()) {
			OntClass subclase = (OntClass) subclasses.next();
			System.out.println(subclase.toString());
		}
		
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		System.out.println("\n** TASK 7.3");
		OntModel modelinf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
		
		OntClass person1 = modelinf.getOntClass(ns+"Person");
		ExtendedIterator instances1 = person.listInstances();
		
		while (instances1.hasNext()) {
			Individual persona = (Individual) instances1.next();
			System.out.println(persona.toString());
		}
		
		ExtendedIterator subclasses1 = person1.listSubClasses();
		
		while (subclasses1.hasNext()) {
			OntClass subclase = (OntClass) subclasses1.next();
			System.out.println(subclase.toString());
		}
		
	}
}
