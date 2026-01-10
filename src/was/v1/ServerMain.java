package was.v1;

import java.io.IOException;

public class ServerMain {

    private static final int PORT = 12345;

    static void main() throws IOException {
        HttpServerV1 server = new HttpServerV1(PORT);
        server.start();
    }

}
