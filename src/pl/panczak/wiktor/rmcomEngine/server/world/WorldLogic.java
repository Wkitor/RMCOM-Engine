package pl.panczak.wiktor.rmcomEngine.server.world;

import org.json.JSONObject;
import pl.panczak.wiktor.rmcomEngine.server.Server;
import pl.panczak.wiktor.rmcomEngine.server.threads.ReceiveThread;

import java.util.LinkedList;

public abstract class WorldLogic {
    private JSONObject world;
    public volatile LinkedList<ReceiveThread> receiveThreads;
    private int idCounter;

    public void setWorld(JSONObject world){
        this.world = world;
    }

    public void setReceiveThreads(LinkedList<ReceiveThread> receiveThreads){
        this.receiveThreads = receiveThreads;
    }

    public void update(Object value, String... path){
        JSONObject object = world;
        JSONObject update = Server.sendThread.update;

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
            for(i = 0; i < path.length - 1; i++){
                if(!update.has(path[i])){
                    update.put(path[i], new JSONObject());
                }
                update = update.getJSONObject(path[i]);
            }

            update.put(path[i], value);
        }
    }

    public void delete(String... path){
        JSONObject object = world;
        JSONObject update = Server.sendThread.update;

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

    public Object get(String... path){
        JSONObject object = world;

        int i;
        for(i = 0; i < path.length - 1; i++){
            object = object.getJSONObject(path[i]);
        }

        return object.get(path[i]);
    }

    public int nextId(){
        return idCounter++;
    }

    public abstract void tick(long tickNumber, double dt);

    public abstract int connect();

    public abstract void disconnect(int id);
}
