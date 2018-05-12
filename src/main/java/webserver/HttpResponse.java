package webserver;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos;
    private String basePath;
    private Map<String, String> headers;

    public static HttpResponse of(OutputStream out, String basePath) {
        return new HttpResponse(out, basePath);
    }

    private HttpResponse(OutputStream out, String basePath) {
        this.dos = new DataOutputStream(out);
        this.basePath = basePath;
        this.headers = Maps.newHashMap();
    }

    public void forward(String resource) {
        if (resource.endsWith(".css")) {
            setContentType("text/css");
        } else if (resource.endsWith(".js")) {
            setContentType("application/javascript");
        } else {
            setContentType("text/html;charset=utf-8");
        }

        forwardBody(readFile(resource));
    }

    public void forwardBody(byte[] content) {
        response200Header();
        addHeader("Content-Length", String.valueOf(content.length));
        processHeader();
        write("\r\n");
        write(content);
        flush();
    }

    public void sendRedirect(String resource) {
        response302Header();
        write("Location: " + resource + " \r\n");
        processHeader();
        flush();
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void setContentType(String contentType) {
        addHeader("Content-Type", contentType);
    }

    private void response200Header() {
        write("HTTP/1.1 200 OK \r\n");
    }

    private void response302Header() {
        write("HTTP/1.1 302 Found \r\n");
    }

    private byte[] readFile(String resource) {
        try {
            return Files.readAllBytes(new File(basePath + resource).toPath());
        } catch (IOException e) {
            log.error(e.getMessage());
            return new byte[0];
        }
    }

    private void processHeader() {
        if (headers.isEmpty()) {
            return;
        }

        headers.forEach((name, value) -> write(name + ": " + value + " \r\n"));
    }

    private void write(String content) {
        try {
            this.dos.writeBytes(content);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void write(byte[] content) {
        try {
            this.dos.write(content, 0, content.length);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void flush() {
        try {
            this.dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
