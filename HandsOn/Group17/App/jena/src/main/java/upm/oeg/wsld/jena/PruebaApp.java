package upm.oeg.wsld.jena;

import java.awt.Desktop;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
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
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class PruebaApp {
	public static String ns = "http://www.acusticpoll.com/ontology/AcusticPoll#";
	public static OntModel model = null;
	public static OntModel mInf = null;
	private static final int INIT = 0;
	private static final int ZONA = 1;
	private static final int FECHA = 2;
	private static final int FECHA2 = 3;
	private static final int ESTACION = 4;

	public static void main(String args[])
	{
		/* Configuracion del modelo */
		String filename = "resources/test01.ttl";

		// Create an empty model
		model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);

		// Create an inference model
		mInf = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, model);

		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);

		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");

		// Read the RDF/XML file
		model.read(filename);

		/* FIN DE CONFIGURACION */

		/* INICIO DEL CLIENTE */
		Scanner reader = new Scanner(System.in);
		gui(INIT);	//Muestra opciones
		int numeroEscogido = reader.nextInt();
		if(numeroEscogido == ZONA) {
			gui(ZONA);
			queryZona(reader.next());
		}else if(numeroEscogido == FECHA) {
			gui(FECHA);
			queryFecha(reader.next());
		}else if(numeroEscogido == FECHA2) {
			gui(FECHA2);
			String tipo = reader.next();
			gui(FECHA);
			String fecha = reader.next();
			queryFecha2(tipo, fecha);
		}else if(numeroEscogido == ESTACION) {
			gui(ESTACION);
			queryEstacion(reader.next());
		}

		reader.close();
		/* */

	} //main


	private static void gui(int option) {
		switch(option) {
		case INIT:
			System.out.println("[1] Contaminación de una zona dada.(cada espacio es un +)");
			System.out.println("[2] Zona con más contaminación media en la fecha dada.");
			System.out.println("[3] Contaminación pedida en una fecha dada.");
			System.out.println("[4] Consultar localizacion de una estacion dada.");
			break;
		case ZONA:
			System.out.println("Introduce el nombre de la zona: ");
			break;
		case FECHA:
			System.out.println("Introduce la fecha formado aaaa-mm-dd: ");
			break;
		case FECHA2:
			System.out.println("Introduce tipo de contaminación: ");
			break;
		case ESTACION:
			System.out.println("Introduce el nombre de la estacion: ");
			break;
		}//del SWITCH
	}

	private static void queryZona(String zona) {
		String query = 
				"PREFIX ns: <http://www.acusticpoll.com/ontology/AcusticPoll#>\r\n" + 
						"PREFIX station: <http://www.acusticpoll.com/resource/NoiseStation/>\r\n" +
						"PREFIX recurso: <http://www.acusticpoll.com/resource/>\r\n" +
						"SELECT ?Le ?Ld ?Ln ?LAeq24 ?Station WHERE {\r\n" + 
						"\t?Station ns:hasMeasurement ?MedAux .\r\n" +
						"\t?Station ns:isLocated ?Locacion .\r\n" +
						"\t?MedAux recurso:Ld ?Ld .\r\n" +
						"\t?MedAux recurso:Le ?Le .\r\n" +
						"\t?MedAux recurso:Ln ?Ln .\r\n" +
						"\t?MedAux recurso:LAeq24 ?LAeq24 .\r\n" +
						"\tFILTER(?Locacion=" + "<http://www.acusticpoll.com/resource/Location/" + zona +">).\r\n" +
						"}";

		Query queryTest = QueryFactory.create(query);
		QueryExecution qexec1 = QueryExecutionFactory.create(queryTest, model);
		ResultSet results1 = qexec1.execSelect();
		System.out.println("\nQuery: " + query);
		while(results1.hasNext()) {
			QuerySolution binding1 = results1.nextSolution();
			RDFNode ld = binding1.get("Ld");
			RDFNode le = binding1.get("Le");
			RDFNode ln = binding1.get("Ln");
			RDFNode la = binding1.get("LAeq24");
			RDFNode station = binding1.get("Station");
			System.out.println("\nEstacion: " + station.toString());
			System.out.println("Ld: " + ld.toString() + "\n");
			System.out.println("Le: " + le.toString() + "\n");
			System.out.println("Ln: " + ln.toString() + "\n");
			System.out.println("LAeq24: " + la.toString() + "\n");
		}
	}

	private static void queryFecha(String fecha) {

		String query =  
				"PREFIX ns: <http://www.acusticpoll.com/ontology/AcusticPoll#>\r\n" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" +
						"PREFIX recurso: <http://www.acusticpoll.com/resource/>\r\n" + 
						"SELECT ?LAeq24 ?Localizacion WHERE {\r\n" + 
						"\t?Station ns:hasMeasurement ?MedAux .\r\n" +
						"\t?Station ns:isLocated ?LocalizacionAux .\r\n" +
						"\t?LocalizacionAux owl:sameAs ?Localizacion .\r\n" +
						"\t?MedAux recurso:date ?date .\r\n" +
						"\t?MedAux recurso:LAeq24 ?LAeq24 .\r\n" +
						"\tFILTER(str(?date)='" +fecha +"').\r\n" + 
						"} ORDER BY DESC(?LAeq24) \r\n" + 
						"LIMIT 1";

		Query queryTest = QueryFactory.create(query);
		QueryExecution qexec1 = QueryExecutionFactory.create(queryTest, model);
		ResultSet results1 = qexec1.execSelect();
		System.out.println("\nQuery: " + query);
		while(results1.hasNext()) {
			QuerySolution binding1 = results1.nextSolution();
			RDFNode cont = binding1.get("LAeq24");
			RDFNode loc = binding1.get("Localizacion");
			System.out.println("\nLocalizacion: " + loc.toString());
			System.out.println("Medida: " + cont.toString() + "\n");
		}

	}

	private static void queryFecha2(String tipo, String fecha3) {

		String query =  
				"PREFIX ns: <http://www.acusticpoll.com/ontology/AcusticPoll#>\r\n" + 
						"PREFIX recurso: <http://www.acusticpoll.com/resource/>\r\n" + 
						"SELECT ?" + tipo + " ?Station WHERE {\r\n" + 
						"\t?Station ns:hasMeasurement ?MedAux .\r\n" +
						"\t?MedAux recurso:date ?date .\r\n" +
						"\t?MedAux recurso:" + tipo + "?" + tipo + ".\r\n" +
						"\tFILTER(str(?date)='" +fecha3 +"').\r\n" + 
						"} ORDER BY DESC(?" + tipo +")";

		Query queryTest = QueryFactory.create(query);
		QueryExecution qexec1 = QueryExecutionFactory.create(queryTest, model);
		ResultSet results1 = qexec1.execSelect();
		System.out.println("\nQuery: " + query);
		while(results1.hasNext()) {
			QuerySolution binding1 = results1.nextSolution();
			RDFNode cont = binding1.get(tipo);
			RDFNode station = binding1.get("Station");
			System.out.println("\nEstacion: " + station.toString());
			System.out.println("Medida: " + cont.toString() + "\n");
		}

	}

	private static void queryEstacion(String estacion) {
		String query =
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" +
						"PREFIX ns: <http://www.acusticpoll.com/ontology/AcusticPoll#>\r\n" + 
						"PREFIX station: <http://www.acusticpoll.com/resource/NoiseStation/>\r\n" + 
						"SELECT ?Localizacion WHERE {\r\n" + 
						"\tstation:" + estacion + " ns:isLocated ?LocAux .\r\n" +
						"\t?LocAux owl:sameAs ?Localizacion .\r\n"
						+ "}";

		Query queryTest = QueryFactory.create(query);
		QueryExecution qexec1 = QueryExecutionFactory.create(queryTest, model);
		ResultSet results1 = qexec1.execSelect();

		QuerySolution binding1 = results1.nextSolution();
		RDFNode loc = binding1.get("Localizacion");

		//String nombre_sitio = loc.toString().substring(28, loc.toString().length());
		String query2 = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" +
						"PREFIX ns: <http://www.acusticpoll.com/ontology/AcusticPoll#>\r\n" + 
						"PREFIX station: <http://www.acusticpoll.com/resource/NoiseStation/>\r\n" +
						"PREFIX dbpedia: <http://dbpedia.org/resource/>\r\n" + 
						"PREFIX dbo: <http://dbpedia.org/ontology/>\r\n" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" +
						"SELECT ?descripcion WHERE {\r\n" + 
						"\t<"+loc.toString() + ">" + " dbo:abstract ?descripcion .\r\n" +
						"\tfilter(langMatches(lang(?descripcion),\"es\"))\r\n" + 
						"}";

		System.out.println("\nQuery: " + query);
		System.out.println("\nEstacion: " + estacion);
		System.out.println("Lugar: " + loc.toString() + "\n");

		URL url = null;
		try {
			url = new URL(loc.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		openWebpage(url);

		queryTest = QueryFactory.create(query2);
		qexec1 = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryTest);
		ResultSet results2 = qexec1.execSelect();
		QuerySolution binding2= results2.nextSolution();
		System.out.println(binding2.get("descripcion").toString());
	}

	public static boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean openWebpage(URL url) {
		try {
			return openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
}
