package com.imagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownloader {

    ExecutorService executorService;
    Handler handler = new Handler();
    private Map<String, Bitmap> bitmapCache = Collections.synchronizedMap(new WeakHashMap<String, Bitmap>());

    public ImageDownloader(Context context) {
        executorService = Executors.newFixedThreadPool(5);
    }

    public void DownloadImage(String url, ImageView imageView) {
        ImageDownloadQueue(url, imageView);
    }

    private void ImageDownloadQueue(String url, ImageView imageView) {
        Bitmap bitmap = bitmapCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        UrlAndImageViewHolder urlAndImageViewHolder = new UrlAndImageViewHolder(url, imageView);
        executorService.submit(new BitmapLoader(urlAndImageViewHolder));
    }

    private class UrlAndImageViewHolder {
        public String url;
        public ImageView imageView;

        public UrlAndImageViewHolder(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
    }

    class BitmapLoader implements Runnable {
        UrlAndImageViewHolder urlAndImageViewHolder;

        BitmapLoader(UrlAndImageViewHolder urlAndImageViewHolder) {
            this.urlAndImageViewHolder = urlAndImageViewHolder;
        }

        @Override
        public void run() {
            try {
                Bitmap bmp = getBitmap(urlAndImageViewHolder.url);
                AddBitmapToImageView bd = new AddBitmapToImageView(bmp, urlAndImageViewHolder);
                handler.post(bd);

            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Bitmap getBitmap(String url) {

        HttpURLConnection httpURLConnection = null;
        try {
            URL uri = new URL(url);
            httpURLConnection = (HttpURLConnection) uri.openConnection();
            int status = httpURLConnection.getResponseCode();
            if (status == -1) {
                return null;
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            httpURLConnection.disconnect();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    class AddBitmapToImageView implements Runnable {
        Bitmap bitmap;
        UrlAndImageViewHolder urlAndImageViewHolder;

        public AddBitmapToImageView(Bitmap bitmap, UrlAndImageViewHolder urlAndImageViewHolder) {
            this.bitmap = bitmap;
            this.urlAndImageViewHolder = urlAndImageViewHolder;
        }

        public void run() {
            if (bitmap != null) {
                bitmapCache.put(urlAndImageViewHolder.url, bitmap);
                urlAndImageViewHolder.imageView.setImageBitmap(bitmap);
            }else {
                urlAndImageViewHolder.imageView.setImageResource(R.drawable.placeholder);
            }
        }
    }

}
