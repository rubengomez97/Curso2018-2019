package upm.oeg.wsld.jena;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.codec.StringEncoderComparator;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.VCARD;

import com.github.andrewoma.dexx.collection.internal.adapter.ListAdapater;

public class FuncionesAndroid
{	
	// Atributo de clase que contiene el modelo de datos RDF
	private static Model modelo;
	
	// Funcion privada que establece conexion con el RDF y prepara el atributo modelo
	private static void conexion() {//HECHO
		String filename = "bin/resources/bio_saludables_final.rdf";
		// Create an empty model
		modelo = ModelFactory.createDefaultModel();
		
		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);

		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");
		
		// Read the RDF/XML file
		modelo.read(in,null);
	}
	
	/**
	 * ArrayList con el nombre de las maquinas (modelo) sin repetir nombres 
	 * @return
	 */
	public static ArrayList<String> listademaquinas (){//HECHO
		// Array de Strings a devolver por la funcion
		ArrayList<String> ganso = new ArrayList<String>();
		String[] elemento;

		// Creo la propiedad que quiero buscar (HasModelo)
		Property modelos = modelo.getProperty("https://www.santacruzdetenerife.es/Propiedades/HasModelo");

		// Creo un iterador que me recorra todos los nodos con la propiedad HasModelo
		NodeIterator iterador = modelo.listObjectsOfProperty(modelos);

		// Recorro y almaceno los valores de los nodos (modelos de maquinas)
		RDFNode nodo = null;
		while(iterador.hasNext()) {
			nodo = iterador.next();
			elemento = nodo.toString().split("\\^",2);
			ganso.add(elemento[0]);
			//System.out.println(elemento[0]);
		}
		
		return ganso;
	}
		
	/**
	 * Le pasamos el Nombre de la maquina y nos retorna todas las latitudes y longitudes de las maquinas con ese modelo  
	 * @param Nmaquina
	 * @return retorna ArrayList de Doubles [lat , long , lat, long...]
	 */
	public static ArrayList<Double> coordenadasMaquinas (String Nmaquina){//HECHO
		// Array de Doubles a devolver por la funcion
		ArrayList<Double> coordenadas = new ArrayList<Double>();
		Double coordenada;
		String [] elemento;

		// Creo las propiedades que quiero buscar (HasModelo, lat y long)
		Property modelos = modelo.getProperty("https://www.santacruzdetenerife.es/Propiedades/HasModelo");
		Property latitud = modelo.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
		Property longitud = modelo.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");

		// Recorro todos los recursos con coordenadas
		ResIterator iteradorR = modelo.listResourcesWithProperty(latitud);
		NodeIterator iteradorN;
		RDFNode nodo;

		// Almaceno las coordenadas en el array
		Resource recurso = null;
		while(iteradorR.hasNext()) {
			recurso = iteradorR.next();
			//System.out.println("Recurso: " + recurso);
			iteradorN = modelo.listObjectsOfProperty(recurso, modelos);
			while(iteradorN.hasNext()) {
				nodo = iteradorN.next();
				elemento = nodo.toString().split("\\^",2);
				if(elemento[0].equals(Nmaquina)) {
					iteradorN = modelo.listObjectsOfProperty(recurso, latitud);
					while(iteradorN.hasNext()) {
						nodo = iteradorN.next();
						elemento = nodo.toString().split("\\^",2);
						coordenada = new Double(elemento[0].replace(",", "."));
						coordenadas.add(coordenada);
						//System.out.println("Latitud: " + coordenada);
					}
					iteradorN = modelo.listObjectsOfProperty(recurso, longitud);
					while(iteradorN.hasNext()) {
						nodo = iteradorN.next();
						elemento = nodo.toString().split("\\^",2);
						coordenada = new Double(elemento[0].replace(",", "."));
						coordenadas.add(coordenada);
						//System.out.println("Longitud: " + coordenada);
					}			
					//System.out.println();
				}
			}
		}

		return coordenadas;
	}
	
	/**
	 * nos retorna todas las latitudes y longitudes de las maquinas   
	 * 
	 * @return retorna ArrayList de Doubles [lat , long , lat, long...]
	 */
	public static ArrayList<Double> coordenadasTodasLasMaquinas (){//HECHO
		// Array de Doubles a devolver por la funcion
		ArrayList<Double> coordenadas = new ArrayList<Double>();
		Double coordenada;
		String [] elemento;
		
		// Creo las propiedades que quiero buscar (lat y long)
		Property latitud = modelo.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
		Property longitud = modelo.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
		
		// Recorro todos los recursos con coordenadas
		ResIterator iteradorR = modelo.listResourcesWithProperty(latitud);
		NodeIterator iteradorN;
		RDFNode nodo;
		
		// Almaceno las coordenadas en el array
		Resource recurso = null;
		while(iteradorR.hasNext()) {
			recurso = iteradorR.next();
			//System.out.println("Recurso: " + recurso);
			iteradorN = modelo.listObjectsOfProperty(recurso, latitud);
			while(iteradorN.hasNext()) {
				nodo = iteradorN.next();
				elemento = nodo.toString().split("\\^",2);
				coordenada = new Double(elemento[0].replace(",", "."));
				coordenadas.add(coordenada);
				//System.out.println("Latitud: " + coordenada);
			}
			iteradorN = modelo.listObjectsOfProperty(recurso, longitud);
			while(iteradorN.hasNext()) {
				nodo = iteradorN.next();
				elemento = nodo.toString().split("\\^",2);
				coordenada = new Double(elemento[0].replace(",", "."));
				coordenadas.add(coordenada);
				//System.out.println("Longitud: " + coordenada);
			}			
			//System.out.println();
		}
		
		return coordenadas;
	}
	
	/**
	 * Le paso el nombre de la maquina y me da su URI
	 * @param Nmaquina
	 * @return
	 */
	public static ArrayList<String> UriMaquina (String Nmaquina){//HECHO
		// Array de URIs a devolver por la funcion
		ArrayList<String> uris = new ArrayList<String>();
		String [] elemento;
		
		// Propiedades a buscar (HasModelo)
		Property modelos = modelo.getProperty("https://www.santacruzdetenerife.es/Propiedades/HasModelo");
		Property enlaces = modelo.getProperty("https://www.santacruzdetenerife.es/Propiedades/HasEnlace");
		
		// Recorro los recursos en busca del modelo requerido
		ResIterator iteradorR = modelo.listResourcesWithProperty(modelos);
		NodeIterator iteradorN;
		Resource recurso;
		RDFNode nodo;
		
		// Voy almacenando las URIs de las maquinas con el modelo pedido
		while(iteradorR.hasNext()) {
			recurso = iteradorR.next();
			iteradorN = modelo.listObjectsOfProperty(recurso, modelos);
			while(iteradorN.hasNext()) {
				nodo = iteradorN.next();
				elemento = nodo.toString().split("\\^", 2);
				if(elemento[0].equals(Nmaquina)){
					iteradorN = modelo.listObjectsOfProperty(recurso, enlaces);
					while(iteradorN.hasNext()) {
						nodo = iteradorN.next();
						elemento = nodo.toString().split("\\^", 2);
						uris.add(elemento[0]);
						//System.out.println(elemento[0]);
					}
				}
			}
		}
		
		return uris;
	}
	
	//para pruebas
	public static void main(String args[])
	{
		// Establezco conexion con los datos del RDF
		conexion();
		
		// Funcion a probar
		UriMaquina("155B Los Patines");
				
	}
	
}
