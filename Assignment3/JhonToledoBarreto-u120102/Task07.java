package upm.oeg.wsld.jena;

import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
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
		//model.write(System.out);
		
		// ** TASK 7.1: List all individuals of "Person" **
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		
		while (instances.hasNext())
		{
			Individual i = (Individual) instances.next();
		    System.out.println("individual : "+i.getURI());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		while (subclasses.hasNext())
		{
			OntClass i = (OntClass) subclasses.next();
		    System.out.println("SubClass of Person : " + i.getURI());
		}
		    
		//model.write(System.out);
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, model);
		ExtendedIterator listInst = inf.getOntClass(ns+"Person").listInstances();
		//Individuals
		while (listInst.hasNext())
		{
		    System.out.println("Individual : " + ((Individual)(listInst.next())).getURI());
		}
		//SubClass
		for( ExtendedIterator listSubClassOf = inf.getOntClass(ns+"Person").listSubClasses(); listSubClassOf.hasNext();){
			
			System.out.println("SubClass of Person  : " + ((OntClass)(listSubClassOf.next())).getURI());
		}
				
		//Las dos anteriores en una(consultes person y te devuelva todos )
		//inferencias
	
	}
}
