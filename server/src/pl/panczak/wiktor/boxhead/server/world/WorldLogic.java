package pl.panczak.wiktor.boxhead.server.world;

import org.json.JSONObject;
import pl.panczak.wiktor.boxhead.server.Server;
import pl.panczak.wiktor.boxhead.server.threads.ReceiveThread;
import java.util.LinkedList;

public abstract class WorldLogic {
    private JSONObject world;
    private volatile LinkedList<ReceiveThread> receiveThreads;
    private int idCounter;

    public void setWorld(JSONObject world){
        this.world = world;
    }

    public void setReceiveThreads(LinkedList<ReceiveThread> receiveThreads){
        this.receiveThreads = receiveThreads;
    }

    private void update(Object value, String... path){
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

    private void delete(String... path){
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

    public abstract void tick(long tickNumber, double dt);

    public abstract int connect();

    public abstract void disconnect(int id);
}
