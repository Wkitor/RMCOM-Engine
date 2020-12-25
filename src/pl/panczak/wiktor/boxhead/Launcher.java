package pl.panczak.wiktor.boxhead;

import org.json.JSONObject;
import pl.panczak.wiktor.boxhead.threads.*;
import java.io.*;
import java.net.Socket;

public class Launcher {
    public static final int frameRate = 60;

    public static DrawThread drawThread;
    public static ReceiveThread receiveThread;
    public static Sender sender;
    public static Socket server;
    public static JSONObject world;
    public static JSONObject partialUpdate;

    public static void main(String[] args){
        world = new JSONObject();
        partialUpdate = new JSONObject();
        drawThread = new DrawThread("RED MOVING CIRCLES ONLINE MULTIPLAYER!!!", 640, 480, world);
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
