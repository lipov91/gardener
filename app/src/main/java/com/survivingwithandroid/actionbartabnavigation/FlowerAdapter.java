package com.survivingwithandroid.actionbartabnavigation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.survivingwithandroid.actionbartabnavigation.model.Flower;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by lipov91 on 2015-07-07.
 */
public class FlowerAdapter extends ArrayAdapter<Flower> {

    Context context;
    List<Flower> flowers;
    TextView tvData;
    LayoutInflater inflater;
    View view;
    Flower flower;
    ImageView ivPicture;
    FlowerView container;
    Bitmap bitmap;
    private LruCache<Integer, Bitmap> imageCache;
    final int maxMemory;
    final int cacheSize;

    public FlowerAdapter(Context context, int resource, List<Flower> flowers) {

        super(context, resource, flowers);
        this.context = context;
        this.flowers = flowers;
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cacheSize = maxMemory / 8;
        imageCache = new LruCache<Integer, Bitmap>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_flower, parent, false);

        flower = flowers.get(position);
        tvData = (TextView) view.findViewById(R.id.tvData);
        tvData.setText(flower.getName());

        // Obrazek został załadowany
        bitmap = imageCache.get(flower.getProductId());

        if (bitmap != null) {

            ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
            ivPicture.setImageBitmap(flower.getBitmap());

        } else {

            container = new FlowerView();
            container.flower = flower;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }


    // Wykorzystanie mechanizmu lazy load - optymalizacja ładowania obrazków
    class FlowerView {

        public Flower flower;
        public View view;
        public Bitmap bitmap;
    }

    // Asynchroniczne ładowanie obrazków (Podczas standardowego ładowania widok zostanie
    // wyświetlony dopiero po załadowaniu wszystkich obrazków)
    // Rozwiązanie zalecane w przypadku niedużej liczby obrazków
    private class ImageLoader extends AsyncTask<FlowerView, Void, FlowerView> {

        FlowerView container;
        Flower flower;
        String imageUrl;
        InputStream stream;
        Bitmap bitmap;

        @Override
        protected FlowerView doInBackground(FlowerView... params) {

            container = params[0];
            flower = container.flower;

            try {

                imageUrl = FlowerListActivity.PHOTOS_BASE_URL + flower.getPhoto();
                stream = (InputStream) new URL(imageUrl).getContent();
                bitmap = BitmapFactory.decodeStream(stream);
                flower.setBitmap(bitmap);
                stream.close();
                container.bitmap = bitmap;

                return container;

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(FlowerView result) {

            ivPicture = (ImageView) result.view.findViewById(R.id.ivPicture);
            ivPicture.setImageBitmap(result.bitmap);
            imageCache.put(result.flower.getProductId(), result.bitmap);
        }
    }
}
