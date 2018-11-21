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
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		String individuales ="";
		while(instances.hasNext()){
			individuales += "individuals person: "+((Individual) instances.next()).getURI()+"\n";
		}
		System.out.println(individuales);
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		String subclases ="";
		while(subclasses.hasNext()){
			subclases += "subclasses person: "+((OntClass) subclasses.next()).getURI()+"\n";
		}
		System.out.println(subclases);
		
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		OntModel m1 = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF,model);
		OntClass m2 = m1.getOntClass(ns+"Person");
		
		ExtendedIterator subclase = person.listSubClasses();
		String printSubclase = ""; 
		while(subclase.hasNext()) {
			printSubclase += "subclasses: "+((OntClass) subclase.next()).getURI() + "\n";
		}
		System.out.println(printSubclase);
		
		ExtendedIterator instancia = person.listInstances();
		
		
		String printInstancia = ""; 
		while(instancia.hasNext()) {
			printInstancia += "instances: "+((Individual) instancia.next()).getURI() + "\n";
		}
		System.out.println(printInstancia);
	}
}
