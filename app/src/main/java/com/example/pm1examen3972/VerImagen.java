package com.example.pm1examen3972;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.pm1examen3972.transacciones.Conexion;
import com.example.pm1examen3972.transacciones.Operaciones;
import com.example.pm1examen3972.transacciones_actividades.Contactos;

public class VerImagen extends AppCompatActivity {
    Conexion conexion;
    ImageView fotoFinal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);

        fotoFinal = (ImageView) findViewById(R.id.fotover);

        Log.e("Mensaje",""+ListaContactos.selectedItem);
        fotoFinal.setImageBitmap(obtenerImagen()); //esto sirve para poner la imagen en el imageview
    }


    public Bitmap obtenerImagen(){
        conexion = new Conexion(this, Operaciones.nambeBD, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Bitmap bitmap = null;

        try {
            Cursor cursor = db.rawQuery(Operaciones.SelectImagenContacto,null);
            if (cursor.moveToNext()) {
                byte[] imagen = cursor.getBlob(0);
                int size=imagen.length;
                Log.e("tamaño",""+size);
                bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();  // Recuerda cerrar la base de datos
        }

        return bitmap;
    }


}