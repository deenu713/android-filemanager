package com.bresan.learning.filemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.bresan.learning.filemanager.R;
import com.bresan.learning.filemanager.util.SharedPreferencesUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by rodrigobresan on 4/27/16.
 *
 * I do know that splash screens are an anti-pattern** in Android development.. but I couldn't
 * miss this one... All the waiting time will be worth, I promise you :P
 *
 * Hope you all enjoy :-)
 *
 * References **:
 *
 * https://www.youtube.com/watch?v=pEGWcMTxs3I&feature=youtu.be&t=1434
 * http://cyrilmottier.com/2012/05/03/splash-screens-are-evil-dont-use-them/
 *
 */
public class SplashActivity extends Activity {

    private static final int ANIMATION_DURATION_MS = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        // check if it's the user first run, just to know if we're going to display the splash screen
        boolean isFirstRun = SharedPreferencesUtils.isFirstRun(getApplicationContext());

        if (isFirstRun) {
            ImageView imgLoading = (ImageView) findViewById(R.id.img_loading_splash);

            // we need to use the SOURCE as disk cache strategy for Glide, mainly because gifs take
            // a long time to encode
            //
            // (check it here: https://github.com/bumptech/glide/issues/281#issuecomment-64737957)

            Glide.with(this).load(R.drawable.cleevio_preloader_travolta).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<Integer, GifDrawable>() {
                @Override
                public boolean onException(Exception e, Integer model, Target<GifDrawable> target, boolean isFirstResource) {
                    // well, the world isnt perfect.. sometimes Glide may have a strange behaviour..
                    // but we need to display our main screen
                    openMainActivity();
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, Integer model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    // set up the handler for post image execution
                    setHandlerForCompletionDone();

                    // change the first run value on shared preferences. we don't want to see a splash
                    // screen every time we run our app, right? :-)
                    SharedPreferencesUtils.setFirstRunDone(getApplicationContext());
                    return false;
                }
            }).into(imgLoading);
        } else {
            openMainActivity();
        }
    }

    private void setHandlerForCompletionDone() {
        Handler handlerSplash = new Handler();
        handlerSplash.postDelayed(new Runnable() {

            @Override
            public void run() {
                openMainActivity();
            }

        }, ANIMATION_DURATION_MS);
    }

    private void openMainActivity() {
        Intent intentMain = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intentMain);
        finish();
    }
}
