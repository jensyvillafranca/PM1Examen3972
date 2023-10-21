package com.example.pm1examen3972;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pm1examen3972.transacciones_actividades.Contactos;
import com.example.pm1examen3972.transacciones.Conexion;
import com.example.pm1examen3972.transacciones.Operaciones;


import java.util.ArrayList;
import java.util.List;

public class ListaContactos extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    //Declarando variables globales
    Conexion conexion;
    ListView lista;

    //Arreglo de tipo personas
    ArrayList<Contactos> listacontactos;

    //Arreglo String
    ArrayList<String> arreglocontactos;

    //Variable para llevar el estado del item, es decir si esta seleccionado o no
    public static int selectedItem = -1;

    Button verImagen,btncompartir;

    String data_selected;
    List<String> listaPaises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        verImagen = (Button)findViewById(R.id.btnverimagen);
        btncompartir = (Button)findViewById(R.id.btncompartir);

        try {
            //Establecer conexión a la base de datos
            conexion = new Conexion(this, Operaciones.nambeBD, null, 1);

            //Amarrar/Castear el objeto de ListView al objeto gráfico de la interfaz.
            lista = (ListView) findViewById(R.id.listcontactos);

            //Obtener la lista de personas de la base de datos.
            getContacts();

            //Crear un adaptador
            ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arreglocontactos);


            //Llenar la lista de la interaz de usuario
            lista.setAdapter(adp);
            actualizarListaFondo();

            //Podriamos seleccionar un elemento pero para ello necesitamos un evento, aquí se está creando ese evento
            //Al hacer clic en un elemento de la lista, lo vamos a mostrar.
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //del arreglo de personas podemos obtener la posición del empleado que seleccionemos

                    //obteniendo la posición del arreglo
                    String posicionPersona = listacontactos.get(i).getNombre();

                    //Ver cual es el elemento que estamos tocando.
                    //Toast.makeText(ListaContactos.this, "Nombre" + posicionPersona, Toast.LENGTH_LONG).show();
                }
            });




            lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                    data_selected=parent.getItemAtPosition(position).toString();


                    selectedItem = position; // Almacenamos la posición del ítem seleccionado
                    Log.e("Item Seleccionado Contacto",""+selectedItem);
                    
                    //Metodo para cambiar el fondo del item que esta seleccionado
                    actualizarListaFondo();
                    return true;
                }
            });


            verImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), VerImagen.class);
                    startActivity(intent);
                }
            });


        }
        catch (Exception ex)
        {
            ex.toString();
        }

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder= new AlertDialog.Builder(ListaContactos.this);
                builder.setMessage("¿Quiere realizar una llamada?");
                builder.setTitle("LLAMADA");

                data_selected=parent.getItemAtPosition(position).toString();
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mostrarnumero(parent.getItemAtPosition(position).toString());
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ListaContactos.this,"LLamada no realizada", Toast.LENGTH_LONG).show();

                    }
                });

                AlertDialog dialog= builder.create();
                dialog.show();
            }
        });

        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share=new Intent(Intent.ACTION_SEND);
                share.setType("text/text");
                share.putExtra(Intent.EXTRA_SUBJECT, 0+": "+(data_selected.substring((data_selected.indexOf("-")) + 1, data_selected.length())));
                share.putExtra(Intent.EXTRA_TEXT, (data_selected.substring(0, (data_selected.indexOf("-")) - 1)));
                startActivity(Intent.createChooser(share, "COMPARTIR"));
            }
        });

        Button btnActualizar = findViewById(R.id.btnactualizarcontacto);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //metodo para mandar el item y mandar a llamar la otra pantalla
                cargarPantalla();
            }
        });

    }

    private void cargarPantalla() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery(Operaciones.SelectPaisContacto, null);

        if (cursor.moveToFirst()) {
            String paisBuscado = cursor.getString(0);
            String nombre = cursor.getString(1);
            String telefono = cursor.getString(2);
            String nota = cursor.getString(3);
            Log.e("Mensaje", paisBuscado);

            Intent intent = new Intent(this, ActivitySpinner.class);
            intent.putExtra("paisSeleccionado", paisBuscado);
            intent.putExtra("nombre", nombre);
            intent.putExtra("telefono", telefono);
            intent.putExtra("nota", nota);

            cursor.close();
            db.close();

            startActivity(intent);
        }


    }

    private void mostrarnumero(String data) {
        String numero = data.substring((data.indexOf("-")) + 1, data.length());

        if (ContextCompat.checkSelfPermission(ListaContactos.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListaContactos.this,
                    new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String n = "tel:" + numero;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(n)));

        }
    }

        //Para indicarle al usuario que item esta seleccionado, cambiando el color de ese item
        private void actualizarListaFondo () {
            for (int i = 0; i < lista.getChildCount(); i++) {
                if (i == selectedItem) {
                    lista.getChildAt(i).setBackgroundResource(R.drawable.lista_item_seleccionado);
                } else {
                    lista.getChildAt(i).setBackgroundResource(android.R.color.transparent); // o cualquier otro fondo predeterminado que desees usar
                }
            }
        }

        private void getContacts () {
            //Poniendo la BD en modo lectura.
            SQLiteDatabase db = conexion.getReadableDatabase();

            //Creando objeto de clase personas
            Contactos contac = null;

            //Creando una instancia del arreglo lista personas
            listacontactos = new ArrayList<Contactos>();

            //Creando un cursor para poder seleccionar la data, es decir obtener todos los registros de la tabla personas.
            //SelectTablePersonas viene de la clase transacciones
            //null es el argumento pero no le vamos a pasar nada.
            Cursor cursor = db.rawQuery(Operaciones.SelectTableContactos, null);

            //Recorrer el cursor, o todos los datos que vienen de la tabla personas
            while (cursor.moveToNext()) {
                //Instancia de la clase Personas.
                contac = new Contactos();

                //Accediendo a los métodos setter de la clase personas, capturando valores.
                contac.setId(cursor.getInt(0)); // posicion 0 porque el id de la persona es el 0, veamoslo como un arreglo.
                contac.setPais(cursor.getString(1));
                contac.setNombre(cursor.getString(2));
                contac.setTelefono(cursor.getInt(3));
                contac.setNota(cursor.getString(4));
                //contac.setFoto(cursor.getBlob(5));

                //Meter esos datos dentro de la lista
                listacontactos.add(contac);
            }
            //Cerrar el objeto en memoria
            cursor.close();

            //Llenar el objeto de lista.
            FillList();
        }

        private void FillList () {
            //recorrer la lista

            //inicializar el arreglo.
            arreglocontactos = new ArrayList<String>();
            for (int i = 0; i < listacontactos.size(); i++) {
                //Llenando el arreglo de tipo String "arreglopersonas" con lo que trae el arreglo que llenamos arriba.
                arreglocontactos.add(listacontactos.get(i).getNombre() + " - " +
                        listacontactos.get(i).getTelefono());
            }
        }
}