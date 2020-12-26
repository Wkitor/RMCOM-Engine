package pl.panczak.wiktor.rmcomEngine.client;

import org.json.JSONObject;
import pl.panczak.wiktor.rmcomEngine.client.threads.*;
import java.io.*;
import java.net.Socket;

public class Client {
    public static final int frameRate = 60;

    public static DrawThread drawThread;
    public static ReceiveThread receiveThread;
    public static Sender sender;
    public static Socket server;
    public static JSONObject world;
    public static JSONObject partialUpdate;

    public static void start(DrawThread gameDrawThread){
        world = new JSONObject();
        partialUpdate = new JSONObject();
        drawThread = gameDrawThread;
        drawThread.start();

        try {
            server = new Socket("localhost", 6432);
            sender = new Sender(new DataOutputStream(server.getOutputStream()));
            receiveThread = new ReceiveThread(new DataInputStream(server.getInputStream()), world);
            receiveThread.start();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
}
