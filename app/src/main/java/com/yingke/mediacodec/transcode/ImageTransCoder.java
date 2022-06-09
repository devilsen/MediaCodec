package com.yingke.mediacodec.transcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Size;

import com.yingke.mediacodec.transcode.listener.SlimProgressListener;
import com.yingke.mediacodec.transcode.opengl.OpenGLContext;

import java.util.ArrayList;
import java.util.List;

public class ImageTransCoder {

    private static final String TAG = ImageTransCoder.class.getSimpleName();

    private OpenGLContext mGlContextA;
    private OpenGLContext mGlContextB;

    public ImageTransCoder() {
    }

    public boolean convertImage(String srcVideoPath, String outputVideoPath,
                                int newWidth,
                                int newHeight,
                                int newBitrate,
                                SlimProgressListener listener) {

        List<String> pathList = new ArrayList<>();

        // add from local

        // polling images
        for (int i = 0; i < pathList.size(); i++) {
            BitmapInfo bitmapInfo = readImage(pathList.get(i));
            Log.e(TAG, "bitmap info = " + bitmapInfo);

            int texture = createTexture(bitmapInfo);

//            worker.dispatch(new Runnable());
        }

        return true;
    }

    private BitmapInfo readImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapInfo(bitmap, new Size(options.outWidth, options.outHeight));
    }

    // thread 1
    private int createTexture(BitmapInfo bitmapInfo) {
        if (mGlContextA == null) {
            mGlContextA = new OpenGLContext();
        }


    }

    // thread 2
    private void drawImage(int texture) {
        if (mGlContextB == null) {
            mGlContextB = new OpenGLContext();
        }


    }

    private void readPixels(int texture, Size size) {

    }

    private void saveBitmap() {

    }

    static class BitmapInfo {

        private final Bitmap bitmap;
        private final Size size;
        private final int rotation;

        public BitmapInfo(Bitmap bitmap, Size size) {
            this.bitmap = bitmap;
            this.size = size;
            this.rotation = 0;
        }

        public BitmapInfo(Bitmap bitmap, Size size, int rotation) {
            this.bitmap = bitmap;
            this.size = size;
            this.rotation = rotation;
        }

        @Override
        public String toString() {
            return "BitmapInfo{" +
                    "bitmap=" + bitmap +
                    ", size=" + size +
                    ", rotation=" + rotation +
                    '}';
        }
    }

}
