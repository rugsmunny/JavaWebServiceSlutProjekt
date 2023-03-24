import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MyServer {

    public static void runBrowserHttpHandlerServer() throws IOException {

        for (; ; ) {
            MyHttpHandler myHttpHandler = new MyHttpHandler();
            try (ServerSocket serverSocket = new ServerSocket(5432)) {
                for (; ; ) {
                    try (Socket socket = serverSocket.accept()) {
                        System.out.println("Online Browser HTTP handler port: " + 5432 + " is open for biz!");
                        myHttpHandler.setInputOutputStreamAndBufferedReader(socket);
                        if (myHttpHandler.bufferedReader.ready()) {
                            System.out.println("LINE 21: BUFFERED READER IS READY!!");
                            myHttpHandler.handleRequest();
                            String httpResponse = "HTTP/1.1 200 OK\r\nContent-Type: html; charset=UTF-8\r\n\r\n<p>Success</p>";
                            myHttpHandler.outputStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                            myHttpHandler.outputStream.flush();
                            break;
                        } else System.out.println("LINE 29: READER NOT READY!!");

                        System.out.println("END OF LOOP!");
                    }
                }
            }
        }

    }
}




