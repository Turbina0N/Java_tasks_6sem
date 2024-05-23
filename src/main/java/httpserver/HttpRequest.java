package httpserver;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path; // URI
    private final Map<String, String> headers = new HashMap<>();
    private final String body;
    private final Map<String, String> formData = new HashMap<>();
    private final Map<String, byte[]> fileData = new HashMap<>();
    private final Map<String, Object> jsonData = new HashMap<>();

    private HttpRequest(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers.putAll(headers);
        this.body = body;
        if (isMultipart()) {
            parseMultipartData(body);
        } else if (isJson()) {
            parseJson(body);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public Map<String, Object> getJsonData() {
        return jsonData;
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    public Map<String, byte[]> getFileData() {
        return fileData;
    }

    public boolean isMultipart() {
        return headers.getOrDefault("Content-Type", "").startsWith("multipart/form-data");
    }

    public boolean isJson() {
        return headers.getOrDefault("Content-Type", "").startsWith("application/json");
    }

    public static HttpRequest parse(String request) {
        String[] lines = request.split("\r\n");
        String[] requestLine = lines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        Map<String, String> headers = new HashMap<>();
        int i = 1;
        while (lines.length < i && !lines[i].isEmpty()) {
            String[] header = lines[i].split(": ");
            if (header.length == 2) {
                headers.put(header[0], header[1]);

            }
            i++;
        }
        StringBuilder body = new StringBuilder();
        for (int j = i + 1; j < lines.length; j++) {
            body.append(lines[j]).append("\r\n");
        }
        return new HttpRequest(method, path, headers, body.toString().trim());
    }

    private void parseMultipartData(String body) {
        String boundary = headers.get("Boundary");
        String[] parts = body.split("--" + boundary);
        for (String part : parts) {
            if (part.contains("Content-Disposition")) {
                String[] lines = part.split("\r\n");
                String disposition = lines[1];
                String name = disposition.split("name=\"")[1].split("\"")[0];
                if (disposition.contains("filename=\"")) {
                    String filename = disposition.split("filename=\"")[1].split("\"")[0];
                    StringBuilder fileContent = new StringBuilder();
                    for (int j = 3; j < lines.length; j++) {
                        fileContent.append(lines[j]).append("\r\n");
                    }
                    fileData.put(filename, fileContent.toString().trim().getBytes());
                } else {
                    StringBuilder value = new StringBuilder();
                    for (int j = 3; j < lines.length; j++) {
                        value.append(lines[j]).append("\r\n");
                    }
                    formData.put(name, value.toString().trim());
                }
            }
        }
    }

    private void parseJson(String body) {
        System.out.println(body);
        if (body != null && !body.isEmpty()) {
            String jsonString = body.trim();
            if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
                String[] pairs = jsonString.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        jsonData.put(keyValue[0].trim().replace("\"", ""), keyValue[1].trim().replace("\"", ""));
                    }
                }
            }
        }
    }
}
