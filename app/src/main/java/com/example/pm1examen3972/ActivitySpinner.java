package com.example.pm1examen3972;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pm1examen3972.transacciones.Conexion;
import com.example.pm1examen3972.transacciones.Operaciones;

public class ActivitySpinner extends AppCompatActivity {

    Button btnguardarcambios;
    Button btneliminar;

    Conexion conexion;
    Spinner combocontactos;
    EditText nombre, telefono, nota;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);




        try {
            conexion = new Conexion(this, Operaciones.nambeBD, null, 1);
            combocontactos = findViewById(R.id.cbopais);
            nombre = findViewById(R.id.txtnombre);
            telefono = findViewById(R.id.txttelefono);
            nota = findViewById(R.id.txtnota);
            btnguardarcambios = findViewById(R.id.btnguardarcambios);

            /*PARA CREACIÓN DE LOS ITEMS DEL SPINNER**/
            Spinner mySpinner = (Spinner) findViewById(R.id.cbopais);

            // Crea un ArrayAdapter utilizando el array de strings y el layout predeterminado del spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.items_spinner, android.R.layout.simple_spinner_item);

            // Especifica el layout a usar cuando aparece la lista de selecciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Aplica el adaptador al Spinner
            mySpinner.setAdapter(adapter);
            

            cargarPaisContacto();


            btnguardarcambios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String nuevoNombre = nombre.getText().toString();
                    String nuevoPais = combocontactos.getSelectedItem().toString();
                    int nuevoTelefono = Integer.parseInt(telefono.getText().toString());
                    String nuevaNota = nota.getText().toString();

                    // Actualiza la base de datos con los nuevos valores
                    SQLiteDatabase db = conexion.getWritableDatabase();

                    ContentValues valores = new ContentValues();
                    valores.put("pais", nuevoPais);
                    valores.put("nombre", nuevoNombre);
                    valores.put("telefono", nuevoTelefono);
                    valores.put("nota", nuevaNota);
                    valores.put("foto", "");

                    try {
                        // Actualiza la base de datos con los nuevos valores
                        Log.e("Mensaje", Operaciones.SelectIdContactoUpdate);
                        int numRowsUpdated = db.update("contactos", valores, "id=?", new String[] { String.valueOf(Operaciones.SelectIdContactoUpdate)});

                        if (numRowsUpdated > 0) {
                            Toast.makeText(ActivitySpinner.this, "Cambios guardados", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ActivitySpinner.this, "No se encontró ningún registro para actualizar", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ActivitySpinner.this, "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        db.close(); // Importante cerrar la base de datos después de su uso
                    }
                }
            });



        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int cargarIndice(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) combocontactos.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItem(position).equals(value)) {
                Log.e("Indice del pais", ""+position);
                return position;
            }
        }
        return -1; // No encontrado
    }

    private void cargarPaisContacto() {
        String paisSeleccionado = getIntent().getStringExtra("paisSeleccionado");
        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");
        String nota = getIntent().getStringExtra("nota");

        int spinnerPosition = cargarIndice(combocontactos, paisSeleccionado);
        if (spinnerPosition != -1) {
            combocontactos.setSelection(spinnerPosition);
        }

        // Asumiendo que tienes controles como EditText o TextView para establecer estos valores
        EditText nombreEditText = findViewById(R.id.txtnombre);
        EditText telefonoEditText = findViewById(R.id.txttelefono);
        EditText notaEditText = findViewById(R.id.txtnota);

        nombreEditText.setText(nombre);
        telefonoEditText.setText(telefono);
        notaEditText.setText(nota);
    }


}