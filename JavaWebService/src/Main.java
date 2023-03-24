import org.json.simple.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    static Socket socket = null;


    public static void main(String[] args) throws IOException {
        runClient();
    }

    private static void runClient() throws IOException {
        Scanner userInput = new Scanner(System.in);

        boolean run = true;
        while (run) {
            System.out.println("Movie DB Menu\n\n1. List movies\n2. Add movie\n3. Shut down\nChoice: ");

            switch (userInput.nextLine()) {
                case "1":
                    System.out.println("Req sent: GET");
                    sendClientRequest("GET", "/listMovies", new JSONObject());
                    disconnectServer();
                    break;
                case "2":
                    sendClientRequest("POST", "/addMovie", setNewMovie(userInput));
                    System.out.println("Req sent: POST");
                    disconnectServer();
                    break;
                case "3":
                    System.out.println("Quit");
                    sendClientRequest("QUIT", "/", new JSONObject());
                    disconnectServer();
                    run = false;
                    System.out.println("Thank you, come again!");
                    break;
                default:
                    System.out.println("Something went wrong, please try again.");
            }
        }
    }

    private static JSONObject setNewMovie(Scanner userInput) {
        JSONObject movieDetails = new JSONObject();
        JSONObject movieToAddToDb = new JSONObject();
        String genre, title, length;

        System.out.println("What genre? : ");
        genre = userInput.nextLine();
        System.out.println("Name of movie : ");
        title = userInput.nextLine();
        System.out.println("Movie length in minutes : ");
        length = userInput.nextLine();

        movieDetails.put("title", title);
        movieDetails.put("length", length);
        movieToAddToDb.put(genre, movieDetails);

        return movieToAddToDb;
    }


    private static void disconnectServer() throws IOException {

        if (socket != null) socket.close();

        System.out.println("Request avslutad, kopplingar nedstängda");
    }

    private static void sendClientRequest(String httpMethod, String path, JSONObject object) throws IOException {

        JSONObject jsonRequest = new JSONObject();

        // Skapa JSON-objekt med begäransinformation
        jsonRequest.put("HTTPMethod", httpMethod);
        jsonRequest.put("URLParametrar", path);
        jsonRequest.put("ContentType", "application/json");
        jsonRequest.put("Body", object);

        //Skriva ut json req
        System.out.println("Req: " + jsonRequest);

        try (Socket socket = new Socket("localhost", 4321);
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {


            // Skicka JSON-objektet till servern
            outputStream.writeObject(jsonRequest);
            outputStream.flush();

            // Ta emot svaret från servern
            JSONObject jsonResponse = (JSONObject) inputStream.readObject();
            System.out.println("Server response: " + jsonResponse);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
