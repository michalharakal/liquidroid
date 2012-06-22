/*
 * Copyright 2012 Jakob Flierl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liqui.droid.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import lfapi.v2.schema.Member;
import liqui.droid.db.DB2Schema;
import liqui.droid.util.AsyncTask;

import liqui.droid.R;

public class MemberImage {
    Context mContext;
    String  mApiDB;

	Map<String,Bitmap> imageCache;
	
	public MemberImage(Context context, String apiDB) {
		imageCache = new HashMap<String, Bitmap>();
		mContext = context;
		mApiDB   = apiDB;
	}
	
	public void download(String url, ImageView imageView, int width, int height) {
	    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
	    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
	    imageView.setImageDrawable(downloadedDrawable);
	    	     
	    String memberId = url.split(";")[0].substring(13);
	    String type = url.split(";")[1];
	    	     
	    task.execute(memberId, type, String.valueOf(width), String.valueOf(height));
	}
	
	//gets an existing download if one exists for the imageview
	private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
	    if (imageView != null) {
	        Drawable drawable = imageView.getDrawable();
	        if (drawable instanceof DownloadedDrawable) {
	            DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
	            return downloadedDrawable.getBitmapDownloaderTask();
	        }
	    }
	    return null;
	}
 
    public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String memberId = (String)params[0];
            String type = (String)params[1];
            
            Integer w = Integer.valueOf(params[2]);
            Integer h = Integer.valueOf(params[3]);

            return getdMemberImage(memberId, type, w, h);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

                if (this == bitmapDownloaderTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.TRANSPARENT);
            bitmapDownloaderTaskReference =
                new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
    
    Bitmap getdMemberImage(String memberId, String type, int w, int h) {
        // Log.d("XXXXXXXXX", memberId);
        // Log.d("XXXXXXXXX", type);
        
        Uri CONTENT_URL = Uri.parse("content://liqui.droid.db/member_images").buildUpon().appendQueryParameter("db", mApiDB).build();
        
        Cursor c = mContext.getContentResolver().query(CONTENT_URL, null,
                "member_id = ? AND image_type = ?",
                new String[] { memberId, type}, null);

        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            Member.Image mi = DB2Schema.fillMemberImage(c);
            
            InputStream inputStream;
            try {
                
                // Log.d("XXXXXXX", mi.data);
                
                inputStream = new Base64.InputStream(new ByteArrayInputStream(mi.data.getBytes()));
                
                final Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scaleWidth = ((float) w) / width;
                float scaleHeight = ((float) h) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
                
                if (inputStream != null) {
                    inputStream.close();  
                }
                
                // Log.d("XXXXXXXXX", "return bitmap");
                return resizedBitmap;
            } catch (Exception e) {
                Log.w("MemberImage", "Error while retrieving bitmap from member_id: " + memberId + " type: " + type);
            } finally {
            }

        } else {
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gravatar);
            // Log.w("MemberImage", "No member image for member_id: " + memberId + " type: " + type);
        }
        return null;
    }
}

