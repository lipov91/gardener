package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.survivingwithandroid.actionbartabnavigation.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jan Lipka on 2015-07-28.
 */
public class SlideshowActivity extends Activity {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private String title = "";
    Handler mHandler = new Handler();
    DelayedLooperThread imageThread = new DelayedLooperThread();
    Animation animIn;
    Animation animOut;
    TextSwitcher tsStatus;
    TextSwitcher tsInfo;
    ImageSwitcher isImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        animIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        tsStatus = (TextSwitcher) findViewById(R.id.tsStatus);
        tsInfo = (TextSwitcher) findViewById(R.id.tsInfo);
        isImages = (ImageSwitcher) findViewById(R.id.isImages);

        tsStatus.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {

                TextView tvInfo = new TextView(SlideshowActivity.this);

                tvInfo.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                tvInfo.setTextSize(12);

                return tvInfo;
            }
        });

        tsStatus.setInAnimation(animIn);
        tsStatus.setOutAnimation(animOut);

        tsInfo.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {

                TextView tvInfo = new TextView(SlideshowActivity.this);

                tvInfo.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                tvInfo.setTextSize(24);

                return tvInfo;
            }
        });

        isImages.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {

                ImageView imageView = new ImageView(SlideshowActivity.this);
                imageView.setBackgroundColor(0xFF000000);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT
                ));

                return imageView;
            }
        });

        isImages.setInAnimation(animIn);
        isImages.setOutAnimation(animOut);

        tsStatus.setText("");

        imageThread.start();
    }

    @Override
    protected void onPause() {

        super.onPause();
        imageThread.finish();
    }

    public void onClick(View view) {

        new Thread() {

            String tag;
            boolean inTitle = false;
            int imgCount = 0;
            int parserEvent;

            @Override
            public void run() {

                try {

                    URL text = new URL("http://api.flickr.com/services/feeds/photos_public.gne?id=26648248@N04&amp;lang=en-us&amp;format=atom");
                    XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser pullParser = parserFactory.newPullParser();

                    pullParser.setInput(text.openStream(), null);

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            tsStatus.setText("³adowanie...");
                        }
                    });


                    parserEvent = pullParser.getEventType();

                    while (parserEvent != XmlPullParser.END_DOCUMENT) {

                        switch (parserEvent) {

                            case XmlPullParser.TEXT:

                                if (inTitle) {

                                    title = pullParser.getText();
                                }
                                break;

                            case XmlPullParser.END_TAG:

                                tag = pullParser.getName();

                                if (tag.compareTo("title") == 0) {

                                    inTitle = false;
                                }
                                break;

                            case XmlPullParser.START_TAG:

                                tag = pullParser.getName();

                                if (tag.compareTo("title") == 0) {

                                    inTitle = true;
                                }
                                if (tag.compareTo("link") == 0) {

                                    String relType = pullParser.getAttributeValue(null, "rel");

                                    if (relType.compareTo("enclosure") == 0) {

                                        String encType = pullParser.getAttributeValue(null, "type");

                                        if (encType.startsWith("image/")) {

                                            final String imageSrc = pullParser.getAttributeValue(null, "href");
                                            final int curImageCount = ++imgCount;

                                            mHandler.post(new Runnable() {

                                                public void run() {

                                                    tsStatus.setText("Pobrano " + curImageCount
                                                            + " obrazków.");
                                                }
                                            });

                                            final String currentTitle = new String(title);

                                            imageThread.queueEvent(new Runnable() {

                                                public void run() {

                                                    BufferedInputStream in;
                                                    BufferedOutputStream out;

                                                    try {

                                                        in = new BufferedInputStream(new URL(imageSrc).openStream(), IO_BUFFER_SIZE);
                                                        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                                                        out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
                                                        copy(in, out);
                                                        out.flush();

                                                        final byte[] data = dataStream.toByteArray();
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                        final Drawable image = new BitmapDrawable(bitmap);

                                                        mHandler.post(new Runnable() {

                                                            public void run() {

                                                                isImages.setImageDrawable(image);

                                                                tsStatus.setText(currentTitle);
                                                            }
                                                        });

                                                    } catch (Exception e) {

                                                        Log.e("Net", "Failed to grab image", e);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                break;
                        }

                        parserEvent = pullParser.next();
                    }

                } catch (Exception e) {

                    Log.e("Network", "Error in network state", e);
                }
            }

        }.start();
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {

        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;

        while ((read = in.read(b)) != -1) {

            out.write(b, 0, read);
        }
    }


    class DelayedLooperThread extends Thread {

        private long lastTime = 0;
        private long waitTime = 5000;
        private boolean mDone = false;
        private ArrayList<Runnable> eventQueue = new ArrayList<Runnable>();

        public void run() {

            while (!mDone) {

                synchronized (this) {

                    long curTime = System.currentTimeMillis();
                    long diff = curTime - lastTime;

                    if (diff < waitTime) {

                        try {

                            wait(waitTime - diff);

                        } catch (InterruptedException e) {

                            Log.e("Net", "Looper Error", e);
                        }

                    } else {

                        Runnable runnable = getEvent();

                        if (runnable != null) {

                            lastTime = curTime;
                            runnable.run();

                        } else {

                            try {

                                wait();

                            } catch (InterruptedException e) {

                                Log.e("Net", "Runnable Error", e);
                            }
                        }
                    }
                }
            }
        }

        public void queueEvent(Runnable r) {

            synchronized (this) {

                eventQueue.add(r);
                notify();
            }
        }

        private Runnable getEvent() {

            synchronized (this) {

                if (eventQueue.size() > 0) {

                    return eventQueue.remove(0);
                }

            }

            return null;
        }

        private void finish() {

            synchronized (this) {

                mDone = true;
                notify();
            }

            try {

                join();

            } catch (InterruptedException ex) {

                Thread.currentThread().interrupt();
            }
        }
    }
}
