package cs407.onthedot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private ImageView layoutimage;
    private Bitmap bitmap;
    private boolean malformedUrl;

    public DownloadImage(ImageView Image) {
        this.layoutimage = Image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Bitmap doInBackground(String... Image_URL) {

        String url = Image_URL[0];
        try {

            // Handle redirect
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    url = conn.getHeaderField("Location");
            }

            // Get bitmap from URL
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(url)
                        .openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                // Error Log
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        } catch (MalformedURLException e) {
            malformedUrl = true;
            return null;
        } catch (IOException e) {
            malformedUrl = true;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // Set image into image.xml layout
        if (layoutimage != null)
            layoutimage.setImageBitmap(result);
        bitmap = result;
    }
}