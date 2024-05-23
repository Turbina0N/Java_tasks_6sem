package httpserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class HttpResponse {
    private final SocketChannel clientChannel;

    public HttpResponse(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void send(int statusCode, String responseBody) throws IOException {
        String statusText = getStatusText(statusCode);
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + responseBody.getBytes().length + "\r\n"
                + "\r\n" +
                responseBody;
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        clientChannel.write(buffer);
        clientChannel.close();
    }

    public void send(int statusCode, Map<String, String> formData, Map<String, byte[]> fileData) throws IOException {
        StringBuilder responseBody = new StringBuilder("Received Multi part data with body:\n");
        responseBody.append("Received form data: ");
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            responseBody.append(entry.getKey()).append("= ").append(entry.getValue());
        }

        for (Map.Entry<String, byte[]> entry : fileData.entrySet()) {
            responseBody.append("\nReceived file: ").append(entry.getKey())
                    .append(", Content: ").append(new String(entry.getValue()));
        }

        String statusText = getStatusText(statusCode);
        String responseHeader = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + responseBody.toString().getBytes().length + "\r\n" +
                "\r\n";
        ByteBuffer headerBuffer = ByteBuffer.wrap(responseHeader.getBytes());
        ByteBuffer bodyBuffer = ByteBuffer.wrap(responseBody.toString().getBytes());

        clientChannel.write(headerBuffer);
        clientChannel.write(bodyBuffer);
        clientChannel.close();
    }

    public void send(int statusCode, Map<String, Object> jsonMap) throws IOException {
        String jsonBody = mapToJson(jsonMap);
        String statusText = getStatusText(statusCode);
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + jsonBody.getBytes().length + "\r\n" +
                "\r\n" +
                jsonBody;
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        clientChannel.write(buffer);
        clientChannel.close();
    }

    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Map) {
                json.append(mapToJson((Map<String, Object>) entry.getValue()));
            } else {
                json.append(entry.getValue());
            }
            json.append(",");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 1);
        }
        json.append("}");
        return "Received JSON with body: " + json;
    }

    private String getStatusText(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unknown";
        }
    }
}
