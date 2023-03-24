import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyHttpHandler {

    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;
    OutputStream outputStream;
    ArrayList<String> requestStringArrayList;
    String endpoint;
    String queryString;
    Map<String, String> queryParams;
    String requestBody;


    public void setInputOutputStreamAndBufferedReader(Socket socket) throws IOException {
        this.inputStreamReader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        this.bufferedReader = new BufferedReader(this.inputStreamReader);
        this.outputStream = socket.getOutputStream();
    }

    // Read the request line, extract and index all values
    public void requestLineIndexing() throws IOException {
        String tempString;
        int contentLength;
        this.requestStringArrayList = new ArrayList<>();

        while ((tempString = this.bufferedReader.readLine()) != null) {
            if (!tempString.isEmpty()) {

                String[] parts = tempString.split(" ");
                this.requestStringArrayList.addAll(Arrays.asList(parts));

                if (tempString.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(tempString.split(":")[1].trim());
                    if (contentLength >= 0) {
                        setRequestBody(contentLength);
                        break;
                    }
                }

            } else if ((tempString.equals("")) && (this.requestStringArrayList.size() > 0)) {
                break;
            }
        }
    }

    // Get the HTTP method
    public String getHttpMethod() {
        return this.requestStringArrayList.get(0);
    }


    // Set endpoint and query parameters
    public void extractEndpointAndQueryParams() {

        String endpointAndQuery = this.requestStringArrayList.get(1);

        if (endpointAndQuery.indexOf('?') != -1) {

            this.endpoint = endpointAndQuery.substring(0, endpointAndQuery.indexOf('?'));
            this.queryString = endpointAndQuery.substring(endpointAndQuery.indexOf('?') + 1);
            setQueryParams();
        } else {

            this.endpoint = endpointAndQuery;
        }

    }

    private void setQueryParams() {
        // Parse the query parameters into a map

        if (this.queryString.length() > 0) {
            this.queryParams = new HashMap<>();
            String[] pairs = this.queryString.split("&");

            for (String pair : pairs) {

                String[] keyValue = pair.split("=", 2);
                this.queryParams.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");

            }
        }
    }

    private void setRequestBody(int contentLength) throws IOException {
        if (contentLength > 0) {

            StringBuilder bodyBuilder = new StringBuilder();
            this.bufferedReader.readLine();

            do {
                bodyBuilder.append((char) this.bufferedReader.read());
            } while (bodyBuilder.length() != contentLength);
            this.requestBody = String.valueOf(bodyBuilder);
            System.out.println( this.requestBody);
        }
    }

    public void handleRequest() throws IOException {
        requestLineIndexing();
        extractEndpointAndQueryParams();
    }
}




