package put.student.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by tkuczma on 13.08.15.
 *
 * Reads properties and provides helpers methods.
 */
public class PropertiesFactory {
    private static final String SERVER_PROPERTIES_FILE = "server.properties";
    private static final String CLIENT_PROPERTIES_FILE = "client.properties";
    private static final String ROOT_PATH_PROPERTY = "rootpath";
    private static final String BLOCK_SIZE_PROPERTY = "blocksize";
    private static final String SERVERS_PROPERTY = "servers";
    private static final String MAIN_HOST_PROPERTY = "mainhost";
    private static final String MAIN_PORT_PROPERTY = "mainport";
    private static final String PORT_PROPERTY = "port";
    private static final String SERVERS_LIST_SEPARATOR = " ";

    private Properties properties;

    private PropertiesFactory(Properties properties){
        this.properties = properties;
    }

    public static PropertiesFactory getServerProperties() throws IOException {
        return new PropertiesFactory(getProperties(SERVER_PROPERTIES_FILE));
    }

    public static PropertiesFactory getClientProperties() throws IOException {
        return new PropertiesFactory(getProperties(CLIENT_PROPERTIES_FILE));
    }

    private static Properties getProperties(String file) throws IOException {
        Properties prop = new Properties();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);

        // load a properties file
        prop.load(input);

        return prop;
    }

    public File getROOT() throws UnsupportedEncodingException {
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        return new File(new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name())).getParent(), properties.getProperty(ROOT_PATH_PROPERTY));
    }

    public long getBLOCK_SIZE() {
        return Long.parseLong(properties.getProperty(BLOCK_SIZE_PROPERTY));
    }

    public String[] getServerList() {
        return properties.getProperty(SERVERS_PROPERTY).split(SERVERS_LIST_SEPARATOR);
    }

    public String getMainHost() {
        return properties.getProperty(MAIN_HOST_PROPERTY);
    }

    public int getMainPort() {
        return Integer.parseInt(properties.getProperty(MAIN_PORT_PROPERTY));
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty(PORT_PROPERTY));
    }
}
