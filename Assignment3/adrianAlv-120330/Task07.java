import java.io.InputStream;

import org.apache.jena.ontology.ComplementClass;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
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
		String filename = "example6.rdf";
		
		// Create an empty model
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);
	
		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");
	
		// Read the RDF/XML file
		model.read(in, null);
		
		
		// ** TASK 7.1: List all individuals of "Person" **
		System.out.println("7.1: List all individuals  of Person");
		
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator<Individual> instances1 = model.listIndividuals(person);
		
		while(instances1.hasNext()){
			System.out.println("Individual person: " + instances1.next());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator<OntClass> subclasses2 = person.listSubClasses();
		
		while( subclasses2.hasNext()){
			System.out.println("Subclasses of person: " + subclasses2.next());
		}
		
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		System.out.println("7.3: Make the necessary changes to get as well indirect instances and subclasses");
		
		OntModel modelo = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF,model);
		OntClass persona = modelo.getOntClass(ns+"Person");
		ExtendedIterator<Individual> iterator1 = modelo.listIndividuals(persona);
		ExtendedIterator<OntClass> iterator2 = persona.listSubClasses();
		
		while(iterator1.hasNext()){
			Individual inferred = iterator1.next();
			System.out.println("Indirect individuals of Person: "+inferred.getURI());
		}
		while(iterator2.hasNext()){
			System.out.println("Indirect SubClass of Person: "+iterator2.next());
		}
	
	}
}