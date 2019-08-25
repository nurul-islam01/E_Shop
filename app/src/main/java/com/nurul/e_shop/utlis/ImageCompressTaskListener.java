package com.nurul.e_shop.utlis;

import java.io.File;
import java.util.List;

public interface ImageCompressTaskListener {
    public void onComplete(List<File> compressed);
    public void onError(Throwable error);
}
