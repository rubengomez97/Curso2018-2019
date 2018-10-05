package upm.oeg.wsld.jena;

import java.io.InputStream;
import java.util.HashMap;

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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.VCARD;

/**
 * Task09: Data linking
 * 
 * @author isantana
 *
 */
public class Task09
{
	public static String ns = "http://data.three.org#";
	
	public static void main(String args[])
	{
		String file1 = "resources/data03.rdf";
		String file2 = "resources/data04.rdf";
		
		// Create an empty model
		OntModel model3 = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		OntModel model4 = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		OntModel modelSameAs = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		// Use the FileManager to find the input file
		InputStream in1 = FileManager.get().open(file1);
		InputStream in2 = FileManager.get().open(file2);
	
		if (in1 == null)
			throw new IllegalArgumentException("File: "+file1+" not found");
	
		if (in2 == null)
			throw new IllegalArgumentException("File: "+file2+" not found");
	
		// Read the RDF/XML file
		model3.read(in1,null);
		model4.read(in2,null);
		
		// ** TASK 9.1: Data Linking. Look for individuals that are the same in both datasets  and link them by owl:sameAs (OWL.sameAs)**
		// ** We consider that two individuals are the same when they have the same Given and Family names **
		// ** You can assume that all individuals have both properties filled **
		// ** Be aware that in both datasets the URIs are different. You can use either iterators or SPARQL (or both...) **
		// ** As a result, modelSameAs should contain the linking triples <r1, owl:sameAs, r2>  **

		// Read individual from m3
		//      search for same given name and same family name via sparql (better because they're multiple properties in the end) on m4
		//            add sameAs on both models (since no criteria is given)

		OntClass personClass = model3.getOntClass(ns+"Person");
		ExtendedIterator personIterator = personClass.listInstances();

		HashMap<String,Resource> hashMap = new HashMap();

		String queryString =
				"PREFIX ns: <" + ns + "> " + "PREFIX vcard: <" + VCARD.getURI() + "> " + " SELECT ?person ?given ?family WHERE { ?person vcard:Given ?given. ?person vcard:Family ?family. }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model3) ;
		ResultSet results = qexec.execSelect() ;

		while (results.hasNext())
		{
			QuerySolution binding = results.nextSolution();
			Resource subj = (Resource) binding.get("person");
			String given = (String) binding.getLiteral("given").getString();
			String family = binding.getLiteral("family").getString();
			hashMap.put(given+family, subj);
		}

		queryString =
				"PREFIX ns: <" + ns + "> " + "PREFIX vcard: <" + VCARD.getURI() + "> " + " SELECT ?person ?given ?family WHERE { ?person vcard:Given ?given. ?person vcard:Family ?family. }";
		query = QueryFactory.create(queryString);
		qexec = QueryExecutionFactory.create(query, model4) ;
		results = qexec.execSelect() ;

		while (results.hasNext())
		{
			QuerySolution binding = results.nextSolution();
			Resource subj = (Resource) binding.get("person");
			String given = (String) binding.getLiteral("given").getString();
			String family = binding.getLiteral("family").getString();
			if (hashMap.containsKey(given+family)) {
				modelSameAs.add(subj, OWL.sameAs, hashMap.get(given+family));
			}
		}

		modelSameAs.write(System.out, "TURTLE");

	}
}
