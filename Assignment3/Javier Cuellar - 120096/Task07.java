package upm.oeg.wsld.jena;

import java.io.InputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.VCARD;

/**
 * Task 07: Querying ontologies (RDFs)
 * @author elozano
 * @author isantana
 *
 */
public class Task07
{
	public static String ns = "http://somewhere#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	
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
		
		while (instances.hasNext())
		{
			Resource subj = (Resource) instances.next();
		    System.out.println("Person individual: "+subj.getURI());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		
		while (subclasses.hasNext())
		{
			Resource subj = (Resource) subclasses.next();
		    System.out.println("Person subclass: "+subj.getURI());
		}
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		ExtendedIterator subclasses2 = person.listSubClasses();
		inference(model,subclasses2);
	
	}
	
	private static void inference(OntModel model, ExtendedIterator subclass) {
		if(subclass.hasNext()) {
			Resource subj = (Resource) subclass.next();
			OntClass sub = model.getOntClass(subj.getURI());
			ExtendedIterator list = sub.listSubClasses();
			ExtendedIterator ind = sub.listInstances();
			System.out.println("Person subclass inference: "+subj.getURI());
			while(ind.hasNext()) {
				Resource subj2 = (Resource) ind.next();
				System.out.println("Person individual inference: "+subj2.getURI());
			}
			inference(model,list);
		}
		return;
	}
}
