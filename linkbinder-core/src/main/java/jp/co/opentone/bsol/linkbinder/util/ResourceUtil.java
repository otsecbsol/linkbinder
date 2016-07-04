package jp.co.opentone.bsol.linkbinder.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;

public class ResourceUtil {

    public static File getResource(String filename) {
        String externalResourceDir = System.getProperty("LB_RESOURCE_DIR");
        if (StringUtils.isNotEmpty(externalResourceDir)) {
            Path path = Paths.get(externalResourceDir, filename);
            if (path.toFile().exists()) {
                return path.toFile();
            }
        }

        URL resource = Thread.currentThread().getContextClassLoader().getResource(filename);
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
