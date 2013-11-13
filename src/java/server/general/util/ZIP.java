package server.general.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author fnavia@enigmadev.com
 */
public class ZIP {
    public static int BUFFER_SIZE = 4098;

    public void Compress(Vector vectFilesToCompress, String pathFileContenedor) throws Exception {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(pathFileContenedor);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte[] data = new byte[BUFFER_SIZE];
            for (Iterator i = vectFilesToCompress.iterator(); i.hasNext();) {
                File filename = (File) i.next();
                FileInputStream fi = new FileInputStream(filename);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(filename.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public void Uncompress(File filename, File destination) throws Exception {
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(filename);
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            ZipEntry entry;
            if (destination.exists()) {
                destination.mkdirs();
            }

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String destFN = destination.getAbsolutePath() + entry.getName();
                    FileOutputStream fos = new FileOutputStream(destFN);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    try {
                        while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                            dest.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (Exception e) {
            throw e;
        }
    }
}