package org.onyxplatform.api.java.utils;

import java.io.InputStream;
import java.io.IOException;

public class ResourceUtils {


    public static void safeCloseInputStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                
            }
        }
    }

}
