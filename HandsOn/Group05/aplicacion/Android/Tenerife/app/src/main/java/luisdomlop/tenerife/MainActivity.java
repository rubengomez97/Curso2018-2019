package luisdomlop.tenerife;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationProvider;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.util.FileManager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Permission;
import java.util.ArrayList;

import static luisdomlop.tenerife.R.raw.bio_saludables_final;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button ball, sele;
    Spinner spin;
    GoogleMap mapita;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISION_REQUEST_CODE = 1234;
    private Boolean mlocationPermisiongrant = false;
    private FusedLocationProviderClient mFusedlocationclient;
    private static Model modelo;

    String name;

    //oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ajusta la orientacion de la pantall
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //conexion();
        conexion();
        //spin
        ArrayList<String> myList = listademaquinas();

        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.spinner_item_spin, myList);  // pass List to ArrayAdapter
        spin = findViewById(R.id.spinner);
        spin.setAdapter(ad);
        // hacer llama al style del spinner despues de añadir los datos

        //en el on selected poner funcion de añadir marcadores
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                borrarMarcadores();
                //añadir los marcadores
                name = (String) adapterView.getItemAtPosition(adapterView.getSelectedItemPosition());
                // llamada a la funcion que guarda las coordenadas
                borrarMarcadores();
                ArrayList<Double> coordenadas = coordenadasMaquinas(name);
                anadirMarcadores(coordenadas);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /// Boton de info
        ball = findViewById(R.id.button);
        //info
        ball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri;
                //funcion que llama a la Uri de la maquina
                ArrayList uriL = UriMaquina(name);
                uri =(String) uriL.get(0);
                if(uriL.size() <=  1 ){
                    uri = "https://www.santacruzdetenerife.es";
                }
                //llamada con el browser
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(uri));
                startActivity(viewIntent);
            }
        });

        //Boton de todos
        sele = findViewById(R.id.button2);
        sele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarMarcadores();
                ArrayList<Double> coordenadas = coordenadasTodasLasMaquinas();
                anadirMarcadores(coordenadas);
            }
        });

        ///mapa cargar el mapa
        if (isServiceOk()) {
            mFusedlocationclient = LocationServices.getFusedLocationProviderClient(this);
            getlocationPermision();

        }


    }


    // Tema GOOGLE MAPS
    //permisos de localización
    private void getlocationPermision() {
        String[] permision = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mlocationPermisiongrant = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permision, LOCATION_PERMISION_REQUEST_CODE);
                //initMap();
            }
        } else {
            ActivityCompat.requestPermissions(this, permision, LOCATION_PERMISION_REQUEST_CODE);
            //initMap();
        }
    }

    // comp de los servicios de google
    public Boolean isServiceOk() {
        Log.d(TAG, "is:serivceOK: Checking version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "is:serivceOK:google play service is Working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "is:serivceOK:google play service is Working");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "No puede hacer la solicitud del mapa", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //iniciar mapa
    private void initMap() {
        SupportMapFragment mapFr = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFr.getMapAsync(MainActivity.this);
    }

    //
    private void getDeviceLocation() {
        mFusedlocationclient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        try {
            if (mlocationPermisiongrant) {
                Task location = mFusedlocationclient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location currentLocation = (Location) task.getResult();
                            moveCamara(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                        } else {
                            Toast.makeText(MainActivity.this, "imposible obtener posicion actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Toast.makeText(MainActivity.this, "imposible obtener posicion actual", Toast.LENGTH_SHORT).show();
        }

    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode ==  LOCATION_PERMISION_REQUEST_CODE ) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mlocationPermisiongrant = true;
                mapita.setMyLocationEnabled(true);
                initMap();
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    //
    private void moveCamara(LatLng lat,float zoom){
        mapita.moveCamera(CameraUpdateFactory.newLatLngZoom(lat,zoom));
    }

    //
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapita = googleMap;
        //Toast.makeText(MainActivity.this, " pre posconseguida", Toast.LENGTH_SHORT).show();
        //mlocationPermisiongrant=true;
        // aqui no entra porque no pilla los permisos bien
        if (mlocationPermisiongrant) {
            getDeviceLocation();
            //Toast.makeText(MainActivity.this, "posconseguida", Toast.LENGTH_SHORT).show();
            mapita.getUiSettings().setMyLocationButtonEnabled(true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            mapita.setMyLocationEnabled(true);

            // añadir todas las coordenadas
            ArrayList<Double> coordenadas = coordenadasTodasLasMaquinas();
            anadirMarcadores(coordenadas);
        }
    }

    // permite añadir marcadores a granel
    public void anadirMarcadores(ArrayList<Double> coordenadas){
        for (int i = 0; i < coordenadas.size(); i++){
            LatLng punto1 = new LatLng(coordenadas.get(i), coordenadas.get(i+1));
            mapita.addMarker(new MarkerOptions().position(punto1));
            i++;
        }

    }

    // Borrar los Marcadores
    public void borrarMarcadores(){
        mapita.clear();
    }

    // Funciones de Jena

    // Funcion privada que establece conexion con el RDF y prepara el atributo modelo
    private void conexion() {//HECHO
        String filename = "res/raw/bio_saludables_final.rdf";

        //InputStream in = FileManager.get().open(filename);
        InputStream intandroid = getResources().openRawResource(R.raw.bio_saludables_final) ;
        // Create an empty model
        modelo = ModelFactory.createDefaultModel();

        // Use the FileManager to find the input file


        if (intandroid == null)
            throw new IllegalArgumentException("File: "+filename+" not found");

        // Read the RDF/XML file
        modelo.read(intandroid,null);
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


}
