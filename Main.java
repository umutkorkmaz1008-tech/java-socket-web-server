import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Dependency Inversion & Open/Closed: HTML içerik üretimi soyutlandı
interface HtmlContentProvider {
    String getHtmlContent();
}

// Single Responsibility: Sadece HTML sayfasının oluşturulmasından sorumlu sınıf
class StudentBioHtmlProvider implements HtmlContentProvider {
    private final String fullName;
    private final String studentNumber;
    private final String biography;

    public StudentBioHtmlProvider(String fullName, String studentNumber, String biography) {
        this.fullName = fullName;
        this.studentNumber = studentNumber;
        this.biography = biography;
    }

    @Override
    public String getHtmlContent() {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"tr\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    <title>" + fullName + " - Web Sunucu Ödevi</title>\n"
                + "    <style>\n"
                + "        body {\n"
                + "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n"
                + "            background-color: #f0f4f8;\n"
                + "            color: #1e293b;\n"
                + "            display: flex;\n"
                + "            justify-content: center;\n"
                + "            align-items: center;\n"
                + "            min-height: 100vh;\n"
                + "            margin: 0;\n"
                + "        }\n"
                + "        .card {\n"
                + "            background-color: #ffffff;\n"
                + "            border-top: 6px solid #2563eb;\n"
                + "            border-radius: 12px;\n"
                + "            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);\n"
                + "            padding: 36px 42px;\n"
                + "            max-width: 620px;\n"
                + "            width: 90%;\n"
                + "        }\n"
                + "        h1 {\n"
                + "            color: #0f172a;\n"
                + "            font-size: 2.2rem;\n"
                + "            margin-bottom: 6px;\n"
                + "        }\n"
                + "        h2 {\n"
                + "            color: #2563eb;\n"
                + "            font-family: 'Courier New', Courier, monospace;\n"
                + "            font-size: 1.4rem;\n"
                + "            margin-top: 0;\n"
                + "            margin-bottom: 20px;\n"
                + "        }\n"
                + "        .bio {\n"
                + "            background-color: #f8fafc;\n"
                + "            border-left: 4px solid #3b82f6;\n"
                + "            padding: 16px 20px;\n"
                + "            font-size: 1.05rem;\n"
                + "            line-height: 1.65;\n"
                + "            color: #334155;\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div class=\"card\">\n"
                + "        <h1>" + fullName + "</h1>\n"
                + "        <h2>" + studentNumber + "</h2>\n"
                + "        <div class=\"bio\">\n"
                + "            <p>" + biography + "</p>\n"
                + "        </div>\n"
                + "    </div>\n"
                + "</body>\n"
                + "</html>";
    }
}

// Single Responsibility: Sadece gelen istemci isteğini okuyup HTTP yanıtı döndürmekten sorumlu sınıf
class ClientRequestHandler {
    private final HtmlContentProvider contentProvider;

    public ClientRequestHandler(HtmlContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public void handle(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            String requestLine = reader.readLine();
            if (requestLine != null && !requestLine.isEmpty()) {
                System.out.println("Gelen İstek: " + requestLine);
            }

            String htmlBody = contentProvider.getHtmlContent();
            byte[] bodyBytes = htmlBody.getBytes(StandardCharsets.UTF_8);

            String httpHeaders = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html; charset=UTF-8\r\n"
                    + "Content-Length: " + bodyBytes.length + "\r\n"
                    + "Connection: close\r\n"
                    + "\r\n";

            outputStream.write(httpHeaders.getBytes(StandardCharsets.UTF_8));
            outputStream.write(bodyBytes);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("İstemci işlenirken hata oluştu: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("İstemci soketi kapatılamadı: " + e.getMessage());
            }
        }
    }
}

// Single Responsibility: Sadece belirtilen port üzerinden sunucuyu başlatıp dinlemekten sorumlu sınıf
class SimpleHttpServer {
    private final int port;
    private final ClientRequestHandler requestHandler;

    public SimpleHttpServer(int port, ClientRequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Web sunucusu başlatıldı: http://localhost:" + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                requestHandler.handle(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Sunucu başlatılamadı (Port: " + port + "): " + e.getMessage());
        }
    }
}

public class Main {
    private static final int PORT = 1989;

    public static void main(String[] args) {
        String fullName = "Umut Korkmaz";
        String studentNumber = "1250505068";
        String biography = "Merhaba, ben Umut Korkmaz. Yazılım Mühendisliği öğrencisiyim. "
                + "Nesne yönelimli programlama, ağ tabanlı uygulamalar, veritabanı sistemleri "
                + "ve web teknolojileri üzerine projeler geliştirmekteyim.";

        HtmlContentProvider htmlProvider = new StudentBioHtmlProvider(fullName, studentNumber, biography);
        ClientRequestHandler requestHandler = new ClientRequestHandler(htmlProvider);
        SimpleHttpServer server = new SimpleHttpServer(PORT, requestHandler);

        server.start();
    }
}
