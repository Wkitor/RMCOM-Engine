package pl.panczak.wiktor.boxhead.server;

import pl.panczak.wiktor.boxhead.server.threads.AcceptThread;
import pl.panczak.wiktor.boxhead.server.threads.SendThread;
import pl.panczak.wiktor.boxhead.server.threads.TickThread;
import org.json.JSONObject;
import pl.panczak.wiktor.boxhead.server.world.WorldLogic;

public class Launcher {
    public static int tickRate = 64;
    public static int sendRate = 20;

    public static volatile boolean running = false;
    public static AcceptThread acceptThread;
    public static SendThread sendThread;
    public static TickThread tickThread;
    public static JSONObject world;
    public static WorldLogic worldLogic;

    public static void main(String[] args){
        start();
    }

    public static void start(){
        if(!running){
            running = true;

            acceptThread = new AcceptThread();
            sendThread = new SendThread();
            world = new JSONObject();
            worldLogic = new WorldLogic(world, acceptThread.receiveThreads);
            tickThread = new TickThread(worldLogic);

            acceptThread.start();
            sendThread.start();
            tickThread.start();
        }
    }

    public static void stop() throws InterruptedException{
        if(running){
            running = false;
            acceptThread.join();
        }
    }
}
