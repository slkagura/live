package xyz.slkagura.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.ui.R;

public class RawUtil {
    // private void unzip() {
    //     String extractDir = ContextUtil.getApplication().getFilesDir().getAbsolutePath() + "/unzip /";
    //     try {
    //         BufferedOutputStream dest = null;
    //         ZipInputStream zis = new ZipInputStream(getResources().openRawResource(R.raw.book));
    //         ZipEntry entry;
    //         while ((entry = zis.getNextEntry()) != null) {
    //             File file = new File(extractDir + entry.getName());
    //             if (file.exists()) {
    //                 continue;
    //             }
    //             if (entry.isDirectory()) {
    //                 if (!file.exists()) {
    //                     file.mkdirs();
    //                 }
    //                 continue;
    //             }
    //             int count;
    //             byte[] data = new byte[BUFFER];
    //             FileOutputStream fos = new FileOutputStream(file);
    //             dest = new BufferedOutputStream(fos, BUFFER);
    //             while ((count = zis.read(data, 0, BUFFER)) != -1) {
    //                 dest.write(data, 0, count);
    //             }
    //             dest.flush();
    //             dest.close();
    //         }
    //         zis.close();
    //     } catch (Exception e) {
    //         // TODO: handle exception
    //         e.printStackTrace();
    //     }
    // }
}
