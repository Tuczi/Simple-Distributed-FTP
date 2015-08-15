package put.student.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tkuczma on 15.08.15.
 *
 * URI Util/Factory
 */
public class URIUtil {

    public static URI getURI(String hostport) throws URISyntaxException {
        return new URI("my://" + hostport);//protocol is necessary - just use "my" protocol
    }
}
