package pe.edu.ulima.fotosapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FotosActivity extends AppCompatActivity{
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private String mCurrentPhotoPath;

    private ImageView iviFoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_thumbnail);

        iviFoto = (ImageView) findViewById(R.id.iviFoto);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK){
            redimensionarMostrarFoto();
        }
    }

    private File crearImageFile() throws IOException {
        String timestamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = "JPEG_" + timestamp + "_";
        File directorio =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombreArchivo,
                ".jpg",
                directorio
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void tomarFoto(View view){
        Log.i("TAG", "Entra");
        enviarTomarFotoIntent();
    }

    private void redimensionarMostrarFoto() {
        // Obtener las dimensiones del ImageView
        int targetW = iviFoto.getWidth();
        int targetH = iviFoto.getHeight();
        // Obtener las dimensiones del bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determinamos cuanto debemos escalar la imagen
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decodificamos el archivo imagen en un bitmap que pueda caber
        // en el ImageView
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; // <= KITKAT
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        iviFoto.setImageBitmap(bitmap);
    }

    private void enviarTomarFotoIntent() {
        Intent intent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // SI hay app que reacciona a intent
            File photoFile = null;
            try {
                photoFile = crearImageFile();
            } catch (IOException e) {
                Log.e("FotosActivity", "Error creando archivo");
            }
            if (photoFile != null){
                Uri uri = FileProvider.getUriForFile(
                        this,
                        "pe.edu.ulima.fileprovider",
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
