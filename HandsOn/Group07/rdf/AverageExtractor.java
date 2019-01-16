package upm.oeg.wsld.jena;


import org.apache.jena.base.Sys;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.DecimalLiteral;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.jena.rdf.model.ModelFactory;

import javax.xml.bind.helpers.DefaultValidationEventHandler;

public class AverageExtractor {
    public static String ns = "http://www.group07.linkeddata.org/";
    public static String nsProp = ns + "property#";
    public static String dbpedia = "http://dbpedia.org/resource/";
    public static String nsClass = ns + "class#";
    public static String nsIndividual = ns + "individual/";
    public static String ssn = "http://purl.oclc.org/NET/ssnx/ssn#";
    public static String sosa = "http://www.w3.org/ns/sosa/";
    public static String uo = "http://purl.obolibrary.org/obo/";

    public static void main(String args[]) throws IOException {

        String path = "/home/luciano/syncthing/actualesUPM/semWeb/Curso2018-2019/HandsOn/Group07/rdf/";
        
        String filename1 = path + "measurements_2018-with-links-updated_with_slash.ttl";
        int year = 2018;
        InputStream input1 = FileManager.get().open(filename1);
        String filename2 = path + "stations-with-links-updated.ttl";
        InputStream input2 = FileManager.get().open(filename2);

        Repository db = new SailRepository(new MemoryStore());
        db.initialize();

        RepositoryConnection conn = db.getConnection();
        conn.add(input1, "", RDFFormat.TURTLE);
        conn.add(input2, "", RDFFormat.TURTLE);

        System.out.println("Read all the files");

        Model outputModel = new LinkedHashModel();
        ValueFactory vfOutput = SimpleValueFactory.getInstance();
        IRI ssnObservation = vfOutput.createIRI(ssn + "Observation");
        IRI hasSubstance = vfOutput.createIRI(nsProp + "hasSubstance");
        IRI hasMetricValue = vfOutput.createIRI(nsProp + "hasMetricValue");
        IRI hasUnit = vfOutput.createIRI(nsProp + "hasUnit");
        IRI observedProperty = vfOutput.createIRI(sosa + "observedProperty");
        IRI resultTime = vfOutput.createIRI(sosa + "resultTime");
        IRI madeBySensor = vfOutput.createIRI(sosa + "madeBySensor");

        outputModel.setNamespace("ssn", ssn);
        outputModel.setNamespace("property", nsProp);
        outputModel.setNamespace("uo", uo);
        outputModel.setNamespace("dbpedia", dbpedia);
        outputModel.setNamespace("observableproperty", nsIndividual + "observableProperty#");
        outputModel.setNamespace("observation", nsIndividual + "observation#");
        outputModel.setNamespace("station", nsIndividual + "station/");
        outputModel.setNamespace("sosa", sosa);

        String prefixes = "PREFIX ssn:<" + ssn + "> " + " " +
                "PREFIX sosa:<" + sosa + ">" + " " +
                "PREFIX ns:<" + ns + ">" + " " +
                "PREFIX property:<" + nsProp + ">" + " " +
                "PREFIX class:<" + nsClass + ">" + " " +
                "PREFIX dbpedia:<" + dbpedia + ">" + " " +
                "PREFIX rdfs:<" + RDFS.getURI() + ">" + " ";



        String sensorsQuery = prefixes +
                "SELECT ?sensor ?sensorName" + " " +
                "WHERE {" + " " +
                "?sensor a ssn:Sensor." + " " +
                "?sensor rdfs:label ?sensorName." + " " +
                "}";
        TupleQuery sensorsTupleQuery = conn.prepareTupleQuery(sensorsQuery);

        List<BindingSet> sensors = QueryResults.asList(sensorsTupleQuery.evaluate());
        for (BindingSet sensor : sensors) {
            String sensorURI = sensor.getValue("sensor").toString();
            System.out.println(sensorURI);

            for (int i=1; i<=12; i++) {
                System.out.println("\t" + i);
                String measurementsOfTheMonthQuery = prefixes +
                        "SELECT ?substance (AVG(?value) AS ?value)" + " " +
                        "WHERE {" + " " +
                        "?observation sosa:madeBySensor <" + sensorURI + ">." + " " +
                        "?observation sosa:resultTime ?date." + " " +
                        "?observation sosa:observedProperty ?observableProperty." + " " +
                        "?observableProperty property:hasSubstance ?substance." + " " +
                        "?observableProperty property:hasMetricValue ?value." + " " +
                        "?observableProperty property:hasUnit ?unit." + " " +
                        "FILTER (month(?date) = " + i + ") "+
                        "} " +
                        "GROUP BY ?substance " +
                        "HAVING (AVG(?value) > 0)";

                TupleQuery measurementsQuery = conn.prepareTupleQuery(measurementsOfTheMonthQuery);

                List<BindingSet> observationsOfTheMonth = QueryResults.asList(measurementsQuery.evaluate());
                String sensorID = sensor.getValue("sensor").toString().split("/")[5];
                IRI newObservation = vfOutput.createIRI(nsIndividual + "observation#" + sensorID + "_" + i + "_" + year);
                outputModel.add(newObservation, RDF.TYPE, ssnObservation);

                // Set time
                GregorianCalendar cal = new GregorianCalendar(year, i-1, 1);
                Literal time = vfOutput.createLiteral(new Date(cal.getTimeInMillis()));
                outputModel.add(newObservation, resultTime, time);

                for (BindingSet observation : observationsOfTheMonth) {
                    if (observation != null && observation.getValue("substance") != null) {
                        String substanceName = observation.getValue("substance").toString().split("/")[observation.getValue("substance").toString().split("/").length - 1].replace("\"", "");
//                    System.out.println(substanceName);

                        // Create new observable property
                        IRI observableProperty = vfOutput.createIRI(nsIndividual + "observableProperty#" + sensorID + "_" + substanceName + "_" + i + "_" + year);

                        // Bind it to sensor
                        outputModel.add(observableProperty, madeBySensor, sensor.getValue("sensor"));

                        // Bind it to observation
                        outputModel.add(newObservation, observedProperty, observableProperty);

                        // Set substance
                        outputModel.add(observableProperty, hasSubstance, observation.getValue("substance"));

                        // Set unit
                        outputModel.add(observableProperty, hasUnit, vfOutput.createIRI(uo, "UO_0000083"));

                        // Set value
                        BigDecimal value = vfOutput.createLiteral(observation.getValue("value").stringValue()).decimalValue();
                        Literal value2 = vfOutput.createLiteral(value);
                        outputModel.add(observableProperty, hasMetricValue, value2);
                    }
                }
//                break;
            }
//            break;
        }

        OutputStream outputStream = new FileOutputStream(path + "measurements_averages_again_" + year + ".ttl");
        Rio.write(outputModel, outputStream, RDFFormat.TURTLE);
        Rio.write(outputModel, System.out, RDFFormat.TURTLE);

        System.out.println("Finish");

        db.shutDown();
    }
}
