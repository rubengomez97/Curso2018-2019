package upm.oeg.wsld.jena;

import java.io.*;

import org.apache.jena.ontology.*;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;

public class Demo
{
	public static String ns = "http://www.semanticweb.org/pc/ontologies/2018/10/parkingontology#";
	public static String pwl ="http://www.semanticweb.org/pc/ontologies/2018/9#";
	public static String topo="http://data.ign.fr/def/topo#";
	public static String rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String owl="http://www.w3.org/2002/07/owl#";
	public static String xsd="http://www.w3.org/2001/XMLSchema#";
	public static String rdfs= "http://www.w3.org/2000/01/rdf-schema#";
	public static String stringTypeURI = "http://www.w3.org/2001/XMLSchema#string";
	public static String schema="http://schema.org/";

	public static void main(String args[])
	{
		String filename = "202625-0-aparcamientos-publicos-csv.ttl";
		Model model = ModelFactory.createDefaultModel();
		InputStream in = FileManager.get().open(filename);

		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");

		model.read("202625-0-aparcamientos-publicos-csv.ttl");
		int option = -1;

		do {
			System.out.print("Pulsa 1 para obtener una lista de todos los Parkings\n");
			System.out.print("Pulsa 2 para obtener el numero de Parkings publicos de Madrid\n");
			System.out.print("Pulsa 3 y luego pulsa X numero de Parkings y te mostraremos toda la informacion disponible de X parkings\n");
			System.out.print("Pulsa 4 para obtener los 2 Parkings más cercanos al punto que nos indiques\n");
			System.out.print("Pulsa 5 para obtener la informacion de un Parking concreto\n");
			System.out.print("Pulsa 6 para obtener los Parkings de un Barrio concreto\n");
			System.out.print("Pulsa 0 para finalizar\n");


			try {
				option = readInt();
				switch (option) {
				case 1:
					Peticion1(model);
					option=-1;
					break;
				case 2:
					Peticion2(model);
					option=-1;
					break;
				case 3:
					Peticion3(model);
					option=-1;
					break;
				case 4:
					Peticion4(model);
					option=-1;
					break;
				case 5:
					Peticion5(model);
					option=-1;
					break;
				case 6:
					Peticion6(model);
					option=-1;
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Opción introducida no válida!");
			}
		}
		while (option < 0 || option > 6);

		System.out.print("Hasta Pronto\n");
	}

	public static void Peticion1(Model model){
		int numParkings=0;
		String queryString = 
				"SELECT ?Subject "+
						"WHERE { ?Subject a <"+topo+"Parking> .} ";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;

		while (results.hasNext())
		{
			numParkings++;
			QuerySolution binding = results.nextSolution();
			Resource Subject=binding.getResource("Subject");
			System.out.println("RECURSO-->");
			System.out.println("Parking "+numParkings+": "+Subject.getURI()+"\n");
		}
	}
	public static void Peticion2(Model model){
		String queryString = 
				"SELECT (COUNT(?Subject) AS ?number) "+
						"WHERE { ?Subject a <"+topo+"Parking> .} ";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		System.out.println("El numero de Parkings es: "+results.next().getLiteral("number").getInt()+"\n" );
	}

	public static void Peticion3(Model model){
		int option=-1;
		System.out.print("Introduzca el numero de Parkings que quiere obtener informacion\n");
		try {
			option = readInt();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Opción introducida no válida!");
		}
		if (option>56){
			System.out.println("Lo sentimos Madrid aun no tiene tantos parkings publicos, "
					+ "te mostraremos los 56 que tiene en lugar de "+option);
			option=56;
		}
		else if(option==0){
			System.out.println("Todos esos?? como quiera ");
			return;
		}
		String queryString = 
				"SELECT ?Subject "+
						"WHERE { ?Subject a <"+topo+"Parking>"
						+ " . } LIMIT "+option ;

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		while (results.hasNext())
		{
			QuerySolution binding = results.nextSolution();
			Resource Subject=binding.getResource("Subject");
			String queryString1 = 
					"SELECT ?Latitud ?Longitud ?URL ?Ciudad ?Provincia "
							+ "?Address ?Barrio ?Distrito ?CodPostal "
							+ "?Nombre ?Descripcion ?Horario ?Accesible "
							+ "?PlazasPublicas ?PlazasResidentes ?Telefono ?Email "+
							"WHERE { <"+Subject+ "> <"+pwl+"latitud> ?Latitud ;"
							+ " <"+pwl+"longitud> ?Longitud ;"
							+ " <"+pwl+"hasURL> ?URL ;"
							+ " <"+pwl+"hasCiudad> ?Ciudad ;"
							+ " <"+pwl+"hasProvincia> ?Provincia ;"
							+ " <"+pwl+"address> ?Address ;"
							+ " <"+pwl+"hasBarrio> ?Barrio ;"
							+ " <"+pwl+"hasDistrito> ?Distrito ;"
							+ " <"+pwl+"codigoPostal> ?CodPostal ;"
							+ " <"+pwl+"nombre> ?Nombre ;"
							+ " <"+pwl+"descripcion> ?Descripcion ;"
							+ " <"+pwl+"horario> ?Horario ;"
							+ " <"+pwl+"accesibilidad> ?Accesible ;"
							+ " <"+pwl+"plazasPublicas> ?PlazasPublicas ;"
							+ " <"+pwl+"plazasResidentes> ?PlazasResidentes . }";
			//	+ " <"+pwl+"telefono> ?Telefono ;"
			//	+ " <"+pwl+"email> ?Email . } ";
			Query query1 = QueryFactory.create(queryString1);
			QueryExecution qexec1 = QueryExecutionFactory.create(query1, model) ;
			ResultSet results1 = qexec1.execSelect() ;
			if (results1.hasNext())
			{
				System.out.println("El Parking: "+Subject+" tiene: ");
				QuerySolution binding1 = results1.nextSolution();
				mostrarInfoParking(binding1);
			}
		}
	}

	public static void Peticion4(Model model) throws Exception{
		System.out.println("Introduzca la Latitud: ");
		double latitud= readDouble();
		System.out.println("Introduzca la Longitud: ");
		double longitud = readDouble();

		String queryString = 
				"SELECT ?Subject "+
						" WHERE { ?Subject <"+pwl+"latitud> ?latitud . "
						+ " ?Subject <"+pwl+"longitud> ?longitud . "
						+ "FILTER(((((?longitud) - ("+longitud+")) * "
						+ "((?longitud) - ("+longitud+"))) < 0.00006)"
						+ "&& ((((?latitud) - ("+latitud+")) * "
						+ "((?latitud) - ("+latitud +"))) < 0.00006 )) ."
						+ " } LIMIT 2  ";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		while(results.hasNext()){
			QuerySolution binding =results.nextSolution();
			System.out.println("Tienes cerca: ");
			String queryString1 = 
					"SELECT ?Nombre "+
							"WHERE { <"+binding.getResource("Subject") + ">" + " <"+pwl+"nombre> ?Nombre . }";
			Query query1 = QueryFactory.create(queryString1);
			QueryExecution qexec1 = QueryExecutionFactory.create(query1, model) ;
			ResultSet results1 = qexec1.execSelect() ;
			QuerySolution binding1 =results1.nextSolution();
			System.out.println("El Parking:"+binding.getResource("Subject"));
			System.out.println("Nombre del parking: "+binding1.getLiteral("Nombre")+"\n");
		}

	}

	public static void Peticion5(Model model) throws IOException{
		System.out.println("Introduzca el nombre del Parking para obtener su informacion: ");
		String Parking = readString();
		String queryString = 
				"SELECT ?Subject "+
						"WHERE { ?Subject  <"+pwl+"nombre> \""+Parking
						+ "\" . }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		if (results.hasNext())
		{
			QuerySolution binding = results.nextSolution();
			Resource Subject=binding.getResource("Subject");
			String queryString1 = 
					"SELECT ?Latitud ?Longitud ?URL ?Ciudad ?Provincia "
							+ "?Address ?Barrio ?Distrito ?CodPostal "
							+ "?Nombre ?Descripcion ?Horario ?Accesible "
							+ "?PlazasPublicas ?PlazasResidentes ?Telefono ?Email "+
							"WHERE { <"+Subject+ "> <"+pwl+"latitud> ?Latitud ;"
							+ " <"+pwl+"longitud> ?Longitud ;"
							+ " <"+pwl+"hasURL> ?URL ;"
							+ " <"+pwl+"hasCiudad> ?Ciudad ;"
							+ " <"+pwl+"hasProvincia> ?Provincia ;"
							+ " <"+pwl+"address> ?Address ;"
							+ " <"+pwl+"hasBarrio> ?Barrio ;"
							+ " <"+pwl+"hasDistrito> ?Distrito ;"
							+ " <"+pwl+"codigoPostal> ?CodPostal ;"
							+ " <"+pwl+"nombre> ?Nombre ;"
							+ " <"+pwl+"descripcion> ?Descripcion ;"
							+ " <"+pwl+"horario> ?Horario ;"
							+ " <"+pwl+"accesibilidad> ?Accesible ;"
							+ " <"+pwl+"plazasPublicas> ?PlazasPublicas ;"
							+ " <"+pwl+"plazasResidentes> ?PlazasResidentes . }";
			Query query1 = QueryFactory.create(queryString1);
			QueryExecution qexec1 = QueryExecutionFactory.create(query1, model) ;
			ResultSet results1 = qexec1.execSelect() ;
			if (results1.hasNext())
			{
				System.out.println("La informacion relativa a "+Parking +" es: ");
				QuerySolution binding1 = results1.nextSolution();
				mostrarInfoParking(binding1);
			}
		}
	}

	public static void Peticion6(Model model) throws IOException{
		System.out.println("Introduzca el nombre del Barrio: ");
		String Barrio = readString().toUpperCase();

		String queryString = 
				"SELECT ?Subject "+
						" WHERE { ?Subject <"+pwl+"hasBarrio> ?barrio . "
						+ "{ SELECT ?barrio "
						+ "WHERE { "
						+ "?barrio <"+pwl+"nombre> \""+ Barrio+"\" . } } }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		while(results.hasNext()){
			QuerySolution binding =results.nextSolution();
			System.out.println("En el barrio de "+Barrio+" tienes estos parkings:\n");
			System.out.println("El Parking:"+binding.getResource("Subject")+ "\n");
		}
	}

	private static int readInt()throws Exception {
		try {
			System.out.print("> ");
			return Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		} catch (Exception e) {
			throw new Exception("Not number");
		}
	}
	private static double readDouble()throws Exception {
		try {
			System.out.print("> ");
			return Double.parseDouble(new BufferedReader(new InputStreamReader(System.in)).readLine());
		} catch (Exception e) {
			throw new Exception("Not number");
		}
	}
	private static String readString() throws IOException{
		return new BufferedReader(new InputStreamReader(System.in)).readLine();
	}
	public static void mostrarInfoParking(QuerySolution binding1){
		String minusvalidos="No";
		if(binding1.getLiteral("Accesible").getBoolean())
			minusvalidos="Si";
		System.out.println(" - Informacion: ");
		System.out.println("\t +Nombre del Parking: "+binding1.getLiteral("Nombre"));
		System.out.println("\t +Breve descripcion: "+binding1.getLiteral("Descripcion"));
		System.out.println("\t +Horario: "+binding1.getLiteral("Horario"));
		System.out.println("\t +Accesibilidad para minusvalidos: "+minusvalidos);
		System.out.println("\t +Sitio Web: "+binding1.get("URL"));
		System.out.println("\t +Numero de plazas publicas: "+binding1.getLiteral("PlazasPublicas").getInt());
		System.out.println("\t +Numero de plazas para residentes: "+binding1.getLiteral("PlazasResidentes").getInt());
		System.out.println(" - Localizacion: ");
		System.out.println("\t +Provincia: "+binding1.get("Provincia"));
		System.out.println("\t +Ciudad: "+binding1.get("Ciudad"));
		System.out.println("\t +Distrito: "+binding1.get("Distrito"));
		System.out.println("\t +Barrio: "+binding1.get("Barrio"));
		System.out.println("\t +Calle: "+binding1.getLiteral("Address"));
		System.out.println("\t +Codigo Postal: "+binding1.getLiteral("CodPostal").getInt());
		System.out.println("\t +Coordenadas: ["+binding1.getLiteral("Latitud").getDouble()
				+", "+binding1.getLiteral("Longitud").getDouble()+"]");
		//System.out.println(" - Contacto: ");
		//System.out.println("\t +Telefono: "+binding1.getLiteral("Telefono"));
		//System.out.println("\t +Email: "+binding1.getLiteral("Email"));
	}
}

