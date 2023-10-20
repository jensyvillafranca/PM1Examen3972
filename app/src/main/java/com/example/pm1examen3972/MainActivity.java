package com.example.pm1examen3972;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pm1examen3972.R;
import com.example.pm1examen3972.transacciones.Conexion;
import com.example.pm1examen3972.transacciones.Operaciones;



public class MainActivity extends AppCompatActivity {
    //Creación de los botones
    Button btnSalvarContacto, btnContactosSalvados;
    int validarCampoVacio; //es decir que campo es el que esta vacio para mostrar el alertBuilder

    //Campos a guardar
    EditText nombre, telefono, nota;
    Spinner pais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Obteniendo el id del boton en la interfaz
        btnSalvarContacto = (Button)findViewById(R.id.btnsalvar);
        btnContactosSalvados = (Button)findViewById(R.id.btnver);

        //aqui ya le estamos haciendo el cast
        pais = (Spinner) findViewById(R.id.cbopais);
        nombre = (EditText)findViewById(R.id.txtnombre);
        telefono = (EditText)findViewById(R.id.txttelefono);
        nota = (EditText)findViewById(R.id.txtnota);

        View.OnClickListener butonclick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class<?> actividad = null;

                /*if (view.getId() == R.id.btnsalvar) {
                    Log.e("HOLA","ENTRA");
                    guardarPersona();
                }*/
                if (view.getId() == R.id.btnver) {
                    actividad = ListaContactos.class;
                }

                if(actividad != null){
                    MoveActivity (actividad);
                }
            }
        };
        btnContactosSalvados.setOnClickListener(butonclick);

        btnSalvarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validar() == true){
                    guardarPersona();
                }else{
                    mensajesVacios();
                }

            }
        });

        /****PARA CREACIÓN DE LOS ITEMS DEL SPINNER*****/
        Spinner mySpinner = (Spinner) findViewById(R.id.cbopais);

        // Crea un ArrayAdapter utilizando el array de strings y el layout predeterminado del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.items_spinner, android.R.layout.simple_spinner_item);

        // Especifica el layout a usar cuando aparece la lista de selecciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adaptador al Spinner
        mySpinner.setAdapter(adapter);
    }

    private void mensajesVacios() {
        //Creando el AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(validarCampoVacio == 1){ //el campo vacio es el nombre
            //Configurar el AlertDialog.Builder
            builder.setTitle("Alerta");
            builder.setMessage("Debe de escribir un nombre");
        }
        else if(validarCampoVacio == 2){ //el campo vacio es el telefono
            builder.setTitle("Alerta");
            builder.setMessage("Debe de escribir un teléfono");
        }
        else if(validarCampoVacio == 3){ //el campo vacio es el de notas
            builder.setTitle("Alerta");
            builder.setMessage("Debe de escribir una nota");
        }

        //Botón para cerrar el cuadro de dialogo
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Cerrar el cuadro de diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void guardarPersona()
    {
        try {
            Conexion conexion = new Conexion(this, Operaciones.nambeBD, null,1);
            SQLiteDatabase db =  conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Operaciones.pais, pais.getSelectedItem().toString());
            valores.put(Operaciones.nombre, nombre.getText().toString());
            valores.put(Operaciones.telefono, telefono.getText().toString());
            valores.put(Operaciones.nota, nota.getText().toString());

            Long Result = db.insert(Operaciones.tabla, Operaciones.id, valores);
            mensajeExitoso();
            //Utilizando AlertDialog.Builder
            db.close();
        }
        catch (Exception exception)
        {
            //Utilizando AlertDialog.Builder
            errorRegistro();
        }

    }

    private void mensajeExitoso() {
        //Creando el AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Configurar el AlertDialog.Builder
        builder.setTitle("Registro exitoso");
        builder.setMessage("El contacto "+nombre.getText()+" ha sido creado correctamente");

        //Botón para cerrar el cuadro de dialogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Cerrar el cuadro de diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void errorRegistro() {
        //Creando el AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Configurar el AlertDialog.Builder
        builder.setTitle("Error al registrar");
        builder.setMessage("El contacto "+nombre.getText()+" no se ha podido guardar.");

        //Botón para cerrar el cuadro de dialogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Cerrar el cuadro de diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void MoveActivity(Class<?>actividad) {
        Intent intent = new Intent(getApplicationContext(), actividad);
        startActivity(intent);
    }

    //Validar Campos Vacios
    public boolean validar(){
        boolean retorna = true;

        if(nombre.getText().toString().isEmpty()){
            validarCampoVacio = 1; //campo nombre vacio
            retorna = false;
        }
        if(telefono.getText().toString().isEmpty()){
            validarCampoVacio = 2; //campo vacio telefono
            retorna = false;
        }
        if(nota.getText().toString().isEmpty()){
            validarCampoVacio = 3; //campo vacio notas
            retorna = false;
        }
        return retorna;
    }
}