package webApp;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import webApp.App;
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
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class App {

	public static String ns = "http://www.wifipointsmad.org/grupo06/";
        static ArrayList<String> distritos=new ArrayList<String>();
	static ArrayList<String> wifis=new ArrayList<String>();
        static ArrayList<String> calles=new ArrayList<String>();
        static ArrayList<WifiPoint> mostrar=new ArrayList<WifiPoint>();
        public App(){
            
        }
    public static ArrayList<WifiPoint> imprimir(String lugar){
    String distrito;
		distrito=lugar;
		distritos=new ArrayList<String>();
		wifis=new ArrayList<String>();
                calles=new ArrayList<String>();
                mostrar=new ArrayList<WifiPoint>();
		
		String filename = "resources/group06-wifi-municipal-csv-updated.ttl";

		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);

		if (in == null)
			throw new IllegalArgumentException("File: " + filename + " not found");
		
		String nombre="Agencia de Actividades. Registro";
		// Read the RDF/XML file
		model.read(in, null, "TURTLE");
		System.out.println("cargado el modelo");
		String queryString = "PREFIX prf: <http://www.wifipointsmad.org/group06/> "
				+ "SELECT ?x " + "WHERE { "
				+" ?x prf:hasDistrict <http://www.wifipointsmad.org/group06/resource/"+distrito+">"
				+ " .\r\n}";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet results = qexec.execSelect();

		while (results.hasNext()) {

			QuerySolution binding = results.nextSolution();
			RDFNode zona = (RDFNode) binding.get("x");
			RDFNode zona1 = (RDFNode) binding.get("y");
			distritos.add(zona.toString());//tenemos todos los address
			System.out.println(zona);
			//RDFNode direccion = (RDFNode) binding.get("dbPedia");
			/*if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasName")||zona.toString().equals("http://www.wifipointsmad.org/group06/hasDescription")) {
			System.out.println(zona );
			System.out.println(zona1 );
			}*/
			//System.out.println("----------------------");
			
		}
		Iterator it = distritos.iterator();
		while(it.hasNext()){
			//System.out.println("eeeeeeeee"+it.next().toString());
		String queryString1 = "PREFIX prf: <http://www.wifipointsmad.org/group06/> "
		+ "SELECT ?x " + "WHERE { "
		+" ?x prf:hasAddress <"+it.next().toString()+">"
		+ " .\r\n}";
			query = QueryFactory.create(queryString1);
			qexec = QueryExecutionFactory.create(query, model);
			results = qexec.execSelect();
			//System.out.println(queryString1);
			while (results.hasNext()) {

	QuerySolution binding = results.nextSolution();
	RDFNode zona = (RDFNode) binding.get("x");
	RDFNode zona1 = (RDFNode) binding.get("y");
	wifis.add(zona.toString());//tenemos todos los wifis
	//distritos.add(zona.toString());
	//System.out.println(zona);
	//RDFNode direccion = (RDFNode) binding.get("dbPedia");
	/*if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasName")||zona.toString().equals("http://www.wifipointsmad.org/group06/hasDescription")) {
	System.out.println(zona );
	System.out.println(zona1 );
	}*/
	//System.out.println("----------------------");
	
			}
		}
		
		Iterator it1 = wifis.iterator();
                String name=null;
                String descripcion=null;
                String tlf=null;
                String url=null;
      
		while(it1.hasNext()){
			String wifi=it1.next().toString();
			
		String queryString2 = "PREFIX prf: <http://www.wifipointsmad.org/group06/> "
		+ "SELECT ?x ?y " + "WHERE { "
		+ " <"+wifi+"> ?x ?y"
		+ " .\r\n}";
		query = QueryFactory.create(queryString2);
		qexec = QueryExecutionFactory.create(query, model);
		results = qexec.execSelect();
		//System.out.println(queryString2);
		//System.out.println("--------------------------");
		//System.out.println(it1.next().toString());
		
		while (results.hasNext()) {
                       // System.out.println(i++);
			QuerySolution binding = results.nextSolution();
			RDFNode zona = (RDFNode) binding.get("x");
			RDFNode zona1 = (RDFNode) binding.get("y");
                        //System.out.println("info"+zona1.toString());
                        if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasName")){
                            name=zona1.toString();
                        }else if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasPhone")){
                            tlf=zona1.toString();
                        }else if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasContentUrl")){
                            url=zona1.toString();
                        }
			//System.out.println(name);
			//System.out.println(descripcion);
                        //System.out.println(tlf);
			if(name!=null&&tlf!=null&&url!=null){
                            WifiPoint w= new WifiPoint(name, null, tlf,url);
                            mostrar.add(w);
                            name=null;
                            descripcion=null;
                            tlf=null;
                                    
                        }
			
			}
                }
                Iterator ite = distritos.iterator();
                int i = 0;
                String calle=null;
                String codigoPostal=null;
                String numero=null;
                      
                while(ite.hasNext()){
			
			
		String queryString2 = "PREFIX prf: <http://www.wifipointsmad.org/group06/> "
		+ "SELECT ?x ?y " + "WHERE { "
		+ " <"+ite.next().toString()+"> ?x ?y"
		+ " .\r\n}";
		query = QueryFactory.create(queryString2);
		qexec = QueryExecutionFactory.create(query, model);
		results = qexec.execSelect();
		//System.out.println(queryString2);
		//System.out.println("--------------------------");
		//System.out.println(it1.next().toString());
		
		while (results.hasNext()) {
                        
			QuerySolution binding = results.nextSolution();
			RDFNode zona = (RDFNode) binding.get("x");
			RDFNode zona1 = (RDFNode) binding.get("y");
                        //System.out.println("AQUI1 "+zona.toString());
                        //System.out.println("AQUI "+zona1.toString());
                        if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasSteet")){
                            calle=zona1.toString();
                        }else if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasPostalCode")){
                            codigoPostal=zona1.toString();
                        }else if(zona.toString().equals("http://www.wifipointsmad.org/group06/hasNumber")){
                            numero=zona1.toString();
                        }
                        
                        if(calle!=null&&codigoPostal!=null&&numero!=null){    
                            mostrar.get(i).setCalle(calle+", "+numero+", "+codigoPostal);
                            //mostrar.add(i, w2);
                            //System.out.println("ESTA ES LA CALLE "+mostrar.get(i).getCalle());
                            //System.out.println("ESTA ES EL NOMBRE "+mostrar.get(i).getNombre());
                            //System.out.println(i);
                            calle=null;
                            numero=null;
                            codigoPostal=null;
                            i++;
                        }
                            
		}
                
	}
                return mostrar;
}
}