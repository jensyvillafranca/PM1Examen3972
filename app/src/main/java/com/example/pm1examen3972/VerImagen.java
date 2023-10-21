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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VerImagen extends AppCompatActivity {
    Conexion conexion;
    ImageView fotoFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);

        fotoFinal = (ImageView) findViewById(R.id.fotover);

        Log.e("Mensaje", "" + ListaContactos.selectedItem);
        fotoFinal.setImageBitmap(obtenerImagen());
    }

    public Bitmap obtenerImagen() {
        conexion = new Conexion(this, Operaciones.nambeBD, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Bitmap bitmap = null;
        byte[] imagen = null;

        try {
            Cursor cursor = db.rawQuery(Operaciones.SelectImagenContacto, null);
            if (cursor.moveToNext()) {
                imagen = cursor.getBlob(0);
                int size = imagen.length;
                Log.e("tamaño", "" + size);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        if (imagen != null) {
            try {
                File imageFile = convertByteArrToFile(imagen);
                bitmap = reducirCalidadImagen(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    private File convertByteArrToFile(byte[] fileBytes) throws IOException {
        File tempFile = File.createTempFile("tempImage", null, getCacheDir());
        tempFile.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(fileBytes);
        fos.close();

        return tempFile;
    }

    private Bitmap reducirCalidadImagen(File imageFile) {
        int quality = 90;
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
        return bitmap;
    }
}
