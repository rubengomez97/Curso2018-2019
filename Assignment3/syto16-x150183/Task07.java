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
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
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
		
		
		// ** TASK 7.1: List all individuals of "Person" **
		OntClass person = model.getOntClass(ns+"Person");
		ExtendedIterator instances = person.listInstances();
		
		System.out.println("\nIndividuals of Person");
        while(instances.hasNext()){
            Individual ind = (Individual)instances.next();
            System.out.println(ind.getURI());
        }
		
		// ** TASK 7.2: List all subclasses of "Person" **
		ExtendedIterator subclasses = person.listSubClasses();
		
		System.out.println("\nSubClasses of Person");
		while(subclasses.hasNext()){
            OntClass sub = (OntClass)subclasses.next();
            System.out.println(sub.getURI());
        }
		
		
		
		// ** TASK 7.3: Make the necessary changes to get as well indirect instances and subclasses. TIP: you need some inference... **
		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		InfModel infmodel = ModelFactory.createInfModel(reasoner, model);
		
		String queryString = 
				"PREFIX ns: <" + ns + "> " +
				"SELECT ?instance "+
				"WHERE { ?instance a ns:Person.} ";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, infmodel) ;
		ResultSet results = qexec.execSelect() ;
		
		System.out.println("\nInstances(indirect) of person:");
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			Resource res = (Resource) sol.get("instance");
			System.out.println( res.getURI());
		}
		
		String queryString2 = 
				"PREFIX ns: <" + ns + "> " +
				"PREFIX rdfs: <" + RDFS.uri + "> " +
				"SELECT ?subclass "+
				"WHERE { ?subclass rdfs:subClassOf ns:Person.} ";
		Query query2 = QueryFactory.create(queryString2);
		QueryExecution qexec2 = QueryExecutionFactory.create(query2, infmodel) ;
		ResultSet results2 = qexec2.execSelect() ;
		
		System.out.println("\nSubclasses(indirect) of person:");
		while (results2.hasNext()) {
			QuerySolution sol = results2.nextSolution();
			Resource res = (Resource) sol.get("subclass");
			System.out.println( res.getURI());
		}
		
	}
}
