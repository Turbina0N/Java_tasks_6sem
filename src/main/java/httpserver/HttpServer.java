package httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpServer {
    private final String host;
    private final int port;
    private final Map<String, HttpHandler> handlers = new HashMap<>();
    public Map<String, HttpHandler> getHandlers(){
        return handlers;
    }
    public HttpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void addHandler(String path, String method, HttpHandler handler) {
        handlers.put(method + " " + path, handler);
    }

    public void addDefaultHandlers() {
        addHandler("/", "GET", (req, res) -> {
            res.send(200, "Received GET request");
        });
        addHandler("/", "POST", (req, res) -> {
            if (req.isJson()) {
                res.send(200, req.getJsonData());
            } else if (req.isMultipart()) {
                res.send(200, req.getFormData(), req.getFileData());
            }
            else {
                res.send(200, "Received POST request with body: " + req.getBody());
            }
        });
        addHandler("/", "PUT", (req, res) -> {
            if (req.isJson()) {
                res.send(200, req.getJsonData());
            } else if (req.isMultipart()) {
                res.send(200, req.getFormData(), req.getFileData());
            }
            else {
                res.send(200, "Received PUT request with body: " + req.getBody());
            }
        });

        addHandler("/", "PATCH", (req, res) -> {
            if (req.isJson()) {
                res.send(200, req.getJsonData());
            } else if (req.isMultipart()) {
                res.send(200, req.getFormData(), req.getFileData());
            }
            else {
                res.send(200, "Received PATCH request with body: " + req.getBody());
            }
        });

        addHandler("/", "DELETE", (req, res) -> {
            res.send(200, "Received DELETE request");
        });

    }

    public void start() throws IOException {
        addDefaultHandlers();
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    handleAccept(key, selector);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    public void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
            return;
        }
        buffer.flip();
        String request = new String(buffer.array(), 0, bytesRead);
        HttpRequest httpRequest = HttpRequest.parse(request);
        HttpResponse httpResponse = new HttpResponse(clientChannel);
        HttpHandler handler = handlers.get(httpRequest.getMethod() + " " + httpRequest.getPath());

        if (handler != null) {
            log(clientChannel, httpRequest, 200);
            handler.handle(httpRequest, httpResponse);
        } else {
            log(clientChannel, httpRequest, 404);
            httpResponse.send(404, "Not Found");
        }
    }

    /**
     * Log server action.
     *
     * @param client   socket channel
     * @param request  parsed from client
     * @param response code generated from application
     */
    private void log(SocketChannel client, HttpRequest request, Integer response) {
        String url = "?";
        String method = "?";
        if (request != null) {
            url = request.getPath();
            method = request.getMethod();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            System.out.printf("%s %s - %s %s > %d%n",
                    formatter.format(now),
                    client.getRemoteAddress().toString().substring(1),
                    method,
                    url,
                    response);
        } catch (IOException ignored) {
        }
    }
}
