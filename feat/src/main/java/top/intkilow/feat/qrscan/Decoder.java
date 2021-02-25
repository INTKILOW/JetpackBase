/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.intkilow.feat.qrscan;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.os.Bundle;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import top.intkilow.feat.qrscan.core.BinaryBitmap;
import top.intkilow.feat.qrscan.core.MultiFormatReader;
import top.intkilow.feat.qrscan.core.PlanarYUVLuminanceSource;
import top.intkilow.feat.qrscan.core.ReaderException;
import top.intkilow.feat.qrscan.core.Result;
import top.intkilow.feat.qrscan.core.common.HybridBinarizer;

public class Decoder {

    private final MultiFormatReader multiFormatReader;

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    public Decoder() {
        multiFormatReader = new MultiFormatReader();

    }

    public Result decode(ImageProxy imageProxy) {

        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();

        //cameraX 获取yuv
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);
        YuvImage yuvImg = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);

        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(yuvImg.getYuvData(), yuvImg.getWidth(), yuvImg.getHeight(), 0, 0, yuvImg.getWidth(), yuvImg.getHeight(), false);

        Result rawResult = null;

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }
        return rawResult;
//        if (rawResult != null) {
//            // Don't log the barcode contents for security.
//            Bundle bundle = new Bundle();
//            bundleThumbnail(source, bundle);
//        } else {
//
//        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
    }

}
