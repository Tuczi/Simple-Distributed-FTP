package put.student.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tkuczma on 13.08.15.
 */
public class PropertiesFactory {
    public Properties getServerProperties() throws IOException {
        return getProperties("server.properties");
    }

    public Properties getClientProperties() throws IOException {
        return getProperties("client.properties");
    }

    private Properties getProperties(String file) throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(file);

        // load a properties file
        prop.load(input);

        return prop;
    }
}
