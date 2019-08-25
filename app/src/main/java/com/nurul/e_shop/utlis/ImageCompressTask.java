package com.nurul.e_shop.utlis;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class ImageCompressTask implements Runnable {

    private Context mContext;
    private List<String> originalPaths = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<File> result = new ArrayList<>();
    private ImageCompressTaskListener mIImageCompressTaskListener;


    public ImageCompressTask(Context context, String path, ImageCompressTaskListener compressTaskListener) {

        originalPaths.add(path);
        mContext = context;

        mIImageCompressTaskListener = compressTaskListener;
    }
    public ImageCompressTask(Context context, List<String> paths, ImageCompressTaskListener compressTaskListener) {
        originalPaths = paths;
        mContext = context;
        mIImageCompressTaskListener = compressTaskListener;
    }
    @Override
    public void run() {

        for (String path : originalPaths) {
            File file = null;
            try {
                file = ImageCompress.getCompressed(mContext, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //add it!
            result.add(file);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(mIImageCompressTaskListener != null)
                    mIImageCompressTaskListener.onComplete(result);
            }
        });
    }

}
