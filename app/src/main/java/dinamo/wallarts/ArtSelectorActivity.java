package dinamo.wallarts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ArtSelectorActivity extends Activity implements AdapterView.OnItemClickListener, OnClickListener
{

    private static final Integer[] THUMB_IDS = {
            R.drawable.wallpaper_azenhas_small,
            R.drawable.wallpaper_cabodaroca_small,
            R.drawable.wallpaper_eleandonan_small,
            R.drawable.wallpaper_flam_small,
            R.drawable.wallpaper_douro_small,
            R.drawable.wallpaper_foz_small,
            R.drawable.wallpaper_hicking2_small,
            R.drawable.wallpaper_hicking_small,
            R.drawable.wallpaper_highlands_small,
            R.drawable.wallpaper_oldstavanger_small,
            R.drawable.wallpaper_pena_small,
            R.drawable.wallpaper_pulpit_small,
            R.drawable.wallpaper_stockholm1_small,
            R.drawable.wallpaper_stockholm2_small,
            R.drawable.wallpaper_stockholm3_small
    };
    private static final Integer[] IMAGE_IDS = {
            R.drawable.wallpaper_azenhas,
            R.drawable.wallpaper_cabodaroca,
            R.drawable.wallpaper_eleandonan,
            R.drawable.wallpaper_flam,
            R.drawable.wallpaper_douro,
            R.drawable.wallpaper_foz,
            R.drawable.wallpaper_hicking2,
            R.drawable.wallpaper_hicking,
            R.drawable.wallpaper_highlands,
            R.drawable.wallpaper_oldstavanger,
            R.drawable.wallpaper_pena,
            R.drawable.wallpaper_pulpit,
            R.drawable.wallpaper_stockholm1,
            R.drawable.wallpaper_stockholm2,
            R.drawable.wallpaper_stockholm3
    };

    private ImageView mImageView;
    private boolean mIsWallpaperSet;
    private BitmapFactory.Options mOptions;
    private Bitmap mBitmap;
    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    private PhotoViewAttacher mAttacher;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        findWallpapers();
        setContentView(R.layout.activity_artselector);

        mOptions = new BitmapFactory.Options();
        mOptions.inDither = false;
        mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Gallery mGallery = (Gallery) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.setOnItemClickListener(this);
        mGallery.setCallbackDuringFling(false);

        Button b = (Button) findViewById(R.id.set);
        b.setOnClickListener(this);

        mImageView = (ImageView) findViewById(R.id.wallpaper);

        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void findWallpapers() {
        mThumbs = new ArrayList<>(THUMB_IDS.length + 4);
        Collections.addAll(mThumbs, THUMB_IDS);
        mImages = new ArrayList<>(IMAGE_IDS.length + 4);
        Collections.addAll(mImages, IMAGE_IDS);
        final Resources resources = getResources();
        final String[] extras = resources.getStringArray(R.array.extra_wallpapers);
        final String packageName = getApplication().getPackageName();
        for (String extra : extras) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                final int thumbRes = resources.getIdentifier(extra + "_small",
                        "drawable", packageName);
                if (thumbRes != 0) {
                    mThumbs.add(thumbRes);
                    mImages.add(res);
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mIsWallpaperSet = false;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        ImageView view = mImageView;
        Bitmap b = BitmapFactory.decodeResource(getResources(), mImages.get(position), mOptions);
        view.setImageBitmap(b);
        // Help the GC
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = b;
        final Drawable drawable = view.getDrawable();
        drawable.setFilterBitmap(true);
        drawable.setDither(true);

        mAttacher.update();
    }

    /*
 * When using touch if you tap an image it triggers both the onItemClick and
 * the onTouchEvent causing the wallpaper to be set twice. Ensure we only
 * set the wallpaper once.
 */
    private void selectWallpaper() {
        if (mIsWallpaperSet) {
            return;
        }
        mIsWallpaperSet = true;
        try {
            mImageView.buildDrawingCache();
            setWallpaper(mImageView.getDrawingCache());
            setResult(RESULT_OK);
            finish();
        } catch (IOException e) {
            Log.e(" ", "Failed to set wallpaper: " + e);
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private final LayoutInflater mLayoutInflater;
        ImageAdapter(ArtSelectorActivity context) {
            mLayoutInflater = context.getLayoutInflater();
        }
        public int getCount() {
            return mThumbs.size();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image;
            if (convertView == null) {
                image = (ImageView) mLayoutInflater.inflate(R.layout.artselctor_item, parent,false);
            } else {
                image = (ImageView) convertView;
            }
            image.setImageResource(mThumbs.get(position));
            image.getDrawable().setDither(true);
            return image;
        }
    }

    public void onClick(View v) {
        selectWallpaper();
    }

}
