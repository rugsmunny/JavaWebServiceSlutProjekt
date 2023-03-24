import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    static boolean serverRunning = true;
    public static void main(String[] args) throws IOException {

        File jsonDb = new File("C:\\Users\\karim\\Desktop\\Java Web Services\\JavaWebServiceServer\\src\\jsonDb.json");
        System.out.println("Server är nu Redo");
       // runLocalClientServerPort(jsonDb);
        MyServer.runBrowserHttpHandlerServer();
    }

    private static void runLocalClientServerPort(File jsonDb) {

            while (serverRunning) {
                try (ServerSocket serverSocket = new ServerSocket(4321);
                     Socket socket = serverSocket.accept()) {

                    // Initiera input och output stream över socket
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                    // Casta Objekt till JSON objektet 'jsonRequest'
                    JSONObject jsonRequest = (JSONObject) inputStream.readObject();
                    System.out.println("RAD 25: " + jsonRequest);

                    // Hantera begäran, skapa ett JSON-svar och
                    // skicka response tillbaka till klienten
                    outputStream.writeObject(handleRequest(jsonRequest, jsonDb));
                    outputStream.flush();

                } catch (ClassNotFoundException | ParseException |IOException e) {
                    throw new RuntimeException(e);
                } finally { System.out.println("Server Avslutas"); }
        }
    }

    private static JSONObject handleRequest(JSONObject jsonRequest, File jsonDb) throws IOException, ParseException {
        String method = jsonRequest.get("HTTPMethod").toString();
        String path = jsonRequest.get("URLParametrar").toString();
        JSONObject requestBody = (JSONObject) jsonRequest.get("Body");
        JSONObject responseBody;
        JSONObject jsonResponse = new JSONObject();

        switch (method) {
            case "GET":
                if (path.equals("/listMovies")) {
                    System.out.println("GET/listMovies FUNKAR");

                    JSONParser parser = new JSONParser();

                    try (BufferedReader reader = new BufferedReader(new FileReader(jsonDb))) {
                        responseBody = (JSONObject) parser.parse(reader);

                    }

                    jsonResponse.put("status", "200 OK");
                    jsonResponse.put("contentType", "text/plain");
                    jsonResponse.put("body", responseBody);

                } else if (path.equals("/")) {
                    System.out.println("Browser Kontakt");
                } else {
                    jsonResponse.put("status", "404 Not Found");
                    jsonResponse.put("body", "");
                }
                break;
            case "POST":
                System.out.println("Testar POST/");

                if (path.equals("/addMovie") && (requestBody != null)) {
                    System.out.println("POST/addMovie FUNKAR");

                    try {
                        addMovie(requestBody, jsonDb);
                        // Add your logic here to add a movie to the database

                        jsonResponse.put("status", "200 OK");
                        jsonResponse.put("contentType", "text/plain");
                        jsonResponse.put("body", "Movie added.");

                    } catch (Exception e) {
                        System.out.println("Add movie issue triggered an exception: " + e);
                    }

                } else {
                    jsonResponse.put("status", "404 Not Found");
                    jsonResponse.put("body", "");
                }
                break;
            case "QUIT":
                if (path.equals("/")) {
                    System.out.println("QUIT / FUNKAR");
                    jsonResponse.put("status", "200 OK");
                    jsonResponse.put("contentType", "text/plain");
                    jsonResponse.put("body", "Server shutting down.");
                    serverRunning = false;

                } else {
                    jsonResponse.put("status", "404 Not Found");
                    jsonResponse.put("body", "");
                }
                break;
            default:
                jsonResponse.put("status", "400 Bad Request");
                jsonResponse.put("body", "");
        }
        return jsonResponse;
    }


    private static void addMovie(JSONObject requestBody, File jsonDb) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonTempDbObj;

        // 1. Läs in JSON-filen i ett JSONObject
        try (FileReader fileReader = new FileReader(jsonDb)) {
            jsonTempDbObj = (JSONObject) parser.parse(fileReader);
        }
        System.out.println("TEMP DB OBJ: " + jsonTempDbObj);

        // 2. Lägg till den nya filmen i JSON-objektet
        JSONArray tempMovieArray = (JSONArray) jsonTempDbObj.get("genre");
        if (tempMovieArray == null) {
            tempMovieArray = new JSONArray();
        }

        for ( int i = 0; i < tempMovieArray.size(); i++ ) {
            System.out.println("rad 134: " + tempMovieArray.get(i));
           }

        jsonTempDbObj.put("genre", tempMovieArray);
        // 3. Skriv det uppdaterade JSON-objektet tillbaka till filen
        try (FileWriter fileWriter = new FileWriter(jsonDb)) {
            fileWriter.write(jsonTempDbObj.toJSONString());
            fileWriter.flush();
        }
    }

}
