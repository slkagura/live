package xyz.slkagura.common.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class ConvertUtil {
    public static final int COLOR_FormatI420 = 1;
    
    public static final int COLOR_FormatNV21 = 2;
    
    private static final String TAG = ConvertUtil.class.getSimpleName();
    
    public static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }
    
    public static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        // LogUtil.d(TAG, "get data from ", planes.length, " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            // LogUtil.d(TAG, "pixelStride: ", pixelStride, " rowStride: ", rowStride, " width: ", width, " height: ", height, " buffer size: ", buffer.remaining());
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            // LogUtil.d(TAG, "Finished reading data from plane ", i);
        }
        return data;
    }
    
    public static byte[] toByteArray(@NonNull Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        // YUV420 大小总是 width * height * 3 / 2
        byte[] result = new byte[width * height * 3 / 2];
        // YUV_420_888
        Image.Plane[] planes = image.getPlanes();
        // Y通道，对应planes[0]
        // Y size = width * height
        // yBuffer.remaining() = width * height;
        // pixelStride = 1
        ByteBuffer yBuffer = planes[0].getBuffer();
        int yLen = width * height;
        yBuffer.get(result, 0, yLen);
        // U通道，对应planes[1]
        // U size = width * height / 4;
        // uBuffer.remaining() = width * height / 2;
        // pixelStride = 2
        ByteBuffer uBuffer = planes[1].getBuffer();
        int pixelStride = planes[1].getPixelStride(); // pixelStride = 2
        for (int i = 0; i < uBuffer.remaining(); i += pixelStride) {
            result[yLen++] = uBuffer.get(i);
        }
        // V通道，对应planes[2]
        // V size = width * height / 4;
        // vBuffer.remaining() = width * height / 2;
        // pixelStride = 2
        ByteBuffer vBuffer = planes[2].getBuffer();
        pixelStride = planes[2].getPixelStride(); // pixelStride = 2
        for (int i = 0; i < vBuffer.remaining(); i += pixelStride) {
            result[yLen++] = vBuffer.get(i);
        }
        return result;
    }
}
