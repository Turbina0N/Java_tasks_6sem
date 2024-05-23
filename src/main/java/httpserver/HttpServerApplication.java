package httpserver;


import java.io.IOException;

public class HttpServerApplication {
    public static void main(String[] args) {
        HttpServer server = new HttpServer("localhost", 8080);
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
