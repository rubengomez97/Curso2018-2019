package upm.oeg.wsld.jena;

import java.io.InputStream;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.jena.base.Sys;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.InfModelImpl;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasoner;
import org.apache.jena.reasoner.transitiveReasoner.TransitiveReasoner;
import org.apache.jena.reasoner.transitiveReasoner.TransitiveReasonerFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import javax.swing.plaf.nimbus.State;

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

		System.out.println("Instances of Person (exclusively of Person)");
		while (instances.hasNext()) {
			Resource instance = (Resource) instances.next();
			System.out.println(instance.getURI());
		}
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();

		System.out.println("Subclasses of Person");
		while (subclasses.hasNext()) {
			System.out.println(((Resource) subclasses.next()).getURI());
		}

		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		// declare reasoner
		Reasoner reasoner = ReasonerRegistry.getTransitiveReasoner();
		InfModel infModel = ModelFactory.createInfModel(reasoner, model);

		ExtendedIterator extendedIteratorReasoner = infModel.listStatements(null, RDFS.subClassOf, person, model);

		System.out.println("Ahora con reasoner");
		while (extendedIteratorReasoner.hasNext()) {
			Statement st = (Statement) extendedIteratorReasoner.next();
			Resource subj = st.getSubject();
			RDFNode fn = st.getObject();

			ExtendedIterator instancesOfPerson = infModel.listStatements(null, RDF.type, subj, model);

			while (instancesOfPerson.hasNext()) {
				Statement instanceOfPerson = (Statement) instancesOfPerson.next();
				Resource personItself = instanceOfPerson.getSubject();
				Resource classOfPerson = instanceOfPerson.getResource();

				System.out.println(personItself.getURI() + " " + classOfPerson.getURI());
			}
		}
	}
}
