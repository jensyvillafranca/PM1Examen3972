package com.example.pm1examen3972;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pm1examen3972.transacciones.Conexion;
import com.example.pm1examen3972.transacciones.Operaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    //Creación de los botones
    Button btnSalvarContacto, btnContactosSalvados;
    int validarCampoVacio; //es decir que campo es el que esta vacio para mostrar el alertBuilder

    //Campos a guardar
    EditText nombre, telefono, nota;
    Spinner pais;
    String currentPhotoPath;

    ImageView fotocontacto;
    static final int peticion_captura_imagen = 101;
    static final int peticion_acceso_camara = 102;

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
        fotocontacto = (ImageView) findViewById(R.id.fotocontacto);

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

        fotocontacto.setOnClickListener(action->{
            permisos();
        });

        /*PARA CREACIÓN DE LOS ITEMS DEL SPINNER**/
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
            byte[] photoData = getPhotoData();

            ContentValues valores = new ContentValues();
            valores.put(Operaciones.pais, pais.getSelectedItem().toString());
            valores.put(Operaciones.nombre, nombre.getText().toString());
            valores.put(Operaciones.telefono, telefono.getText().toString());
            valores.put(Operaciones.nota, nota.getText().toString());
            valores.put(Operaciones.foto, photoData);

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

    private byte[] getPhotoData() {
        try {
            File photoFile = new File(currentPhotoPath);
            FileInputStream file = new FileInputStream(photoFile);
            byte[] photoData = new byte[(int) photoFile.length()];
            file.read(photoData);
            file.close();
            return photoData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    private void permisos()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},peticion_acceso_camara);
        }
        else
        {
            dispatchTakePictureIntent();
            //TomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_acceso_camara )
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                dispatchTakePictureIntent();
                //TomarFoto();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Se necesita el permiso para accder a la camara", Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode  == peticion_captura_imagen)
        {

            try {
                File foto = new File(currentPhotoPath);
                fotocontacto.setImageURI(Uri.fromFile(foto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        //Reducir la calidad de la imagen
        reducirCalidadImagen(image);

        return image;
    }
    private void reducirCalidadImagen(File imageFile) {
        // Define the quality. The value is between 0 and 100. 100 being the highest quality.
        int quality = 90; // You can adjust this value as per your requirement.

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pm1examen3972.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, peticion_captura_imagen);
            }
        }
    }

}