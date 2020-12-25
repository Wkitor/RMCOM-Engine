package pl.panczak.wiktor.boxhead.server.world;

import org.json.JSONObject;
import pl.panczak.wiktor.boxhead.server.Launcher;
import pl.panczak.wiktor.boxhead.server.threads.ReceiveThread;
import java.util.LinkedList;

public class WorldLogic {
    private final JSONObject world;
    private volatile LinkedList<ReceiveThread> receiveThreads;
    private int idCounter;

    public WorldLogic(JSONObject world, LinkedList<ReceiveThread> receiveThreads){
        this.world = world;
        this.receiveThreads = receiveThreads;
    }

    private void update(Object value, String... path){
        JSONObject object = world;
        JSONObject update = Launcher.sendThread.update;

        int i;
        for(i = 0; i < path.length - 1; i++){
            if(!object.has(path[i])){
                object.put(path[i], new JSONObject());
            }
            object = object.getJSONObject(path[i]);
        }

        boolean sendUpdate = false;
        if(!object.has(path[i])){
            object.put(path[i], value);
            sendUpdate = true;
        }else if(!object.get(path[i]).equals(value)){
            object.put(path[i], value);
            sendUpdate = true;
        }

        if(sendUpdate){
            i = 0;
            for(i = 0; i < path.length - 1; i++){
                if(!update.has(path[i])){
                    update.put(path[i], new JSONObject());
                }
                update = update.getJSONObject(path[i]);
            }

            update.put(path[i], value);
        }
    }

    private void delete(String... path){
        JSONObject object = world;
        JSONObject update = Launcher.sendThread.update;

        int i;
        for(i = 0; i < path.length - 1; i++){
            if(!object.has(path[i])){
                object.put(path[i], new JSONObject());
            }
            object = object.getJSONObject(path[i]);

            if(!update.has(path[i])){
                update.put(path[i], new JSONObject());
            }
            update = update.getJSONObject(path[i]);
        }

        object.remove(path[i]);
        update.put(path[i], JSONObject.NULL);
    }

    private Object get(String... path){
        JSONObject object = world;

        int i;
        for(i = 0; i < path.length - 1; i++){
            object = object.getJSONObject(path[i]);
        }

        return object.get(path[i]);
    }

    private int nextId(){
        return idCounter++;
    }

    // --- game code ---

    private static final int pixelsPerSecond = 100;

    public void tick(long tickNumber, double dt){
        update((int) (1 / dt), "tps");

        double pixels = pixelsPerSecond * dt;
        for(ReceiveThread client: receiveThreads){
            double dx = 0;
            double dy = 0;
            if(client.up){
                dy -= pixels;
            }
            if(client.down){
                dy += pixels;
            }
            if(client.right){
                dx += pixels;
            }
            if(client.left){
                dx -= pixels;
            }

            double newX = (Double) get("players", String.valueOf(client.id), "x") + dx;
            double newY = (Double) get("players", String.valueOf(client.id), "y") + dy;

            update(newX, "players", String.valueOf(client.id), "x");
            update(newY, "players", String.valueOf(client.id), "y");
        }
    }

    public synchronized int connect(){
        int id = nextId();

        update(100.0, "players", String.valueOf(id), "x");
        update(100.0, "players", String.valueOf(id), "y");

        update(true, "players", String.valueOf(id), "x_int");
        update(true, "players", String.valueOf(id), "y_int");

        return id;
    }

    public synchronized void disconnect(int id){
        delete("players", String.valueOf(id));
    }
}
