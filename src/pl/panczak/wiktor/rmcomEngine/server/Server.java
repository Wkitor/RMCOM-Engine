package pl.panczak.wiktor.rmcomEngine.server;

import pl.panczak.wiktor.rmcomEngine.server.threads.AcceptThread;
import pl.panczak.wiktor.rmcomEngine.server.threads.SendThread;
import pl.panczak.wiktor.rmcomEngine.server.threads.TickThread;
import org.json.JSONObject;
import pl.panczak.wiktor.rmcomEngine.server.world.WorldLogic;

public class Server {
    public static int tickRate = 64;
    public static int sendRate = 20;

    public static volatile boolean running = false;
    public static AcceptThread acceptThread;
    public static SendThread sendThread;
    public static TickThread tickThread;
    public static JSONObject world;
    public static WorldLogic worldLogic;

    public static void start(WorldLogic gameLogic){
        if(!running){
            running = true;

            acceptThread = new AcceptThread();
            sendThread = new SendThread();
            world = new JSONObject();
            worldLogic = gameLogic;
            tickThread = new TickThread(worldLogic);

            acceptThread.start();
            sendThread.start();
            tickThread.start();
        }
    }
}
