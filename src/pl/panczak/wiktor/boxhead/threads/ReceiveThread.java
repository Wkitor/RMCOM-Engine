package pl.panczak.wiktor.boxhead.threads;

import org.json.JSONException;
import pl.panczak.wiktor.boxhead.Launcher;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.math.BigDecimal;

public class ReceiveThread extends Thread{
    private final DataInputStream input;
    private final JSONObject world;

    public long receivePeriod;

    public ReceiveThread(DataInputStream input, JSONObject world){
        this.input = input;
        this.world = world;
    }

    public void run(){
        try {
            long lastReceived;
            receivePeriod = 50;

            String data = input.readUTF();
            JSONObject update = new JSONObject(data);
            updateJSON(world, update);

            while (Launcher.drawThread.running) {
                lastReceived = System.currentTimeMillis();

                data = input.readUTF();
                updateJSON(world, update);
                update = new JSONObject(data);
                createPartialUpdate(update);

                receivePeriod = System.currentTimeMillis() - lastReceived;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createPartialUpdate(JSONObject update){
        Launcher.partialUpdate = new JSONObject();
        diffDivJSON(Launcher.partialUpdate, update, world);
    }

    private boolean diffDivJSON(JSONObject object, JSONObject update, JSONObject world){
        boolean changed = false;
        for(String key: update.keySet()){
            if((update.get(key).getClass() == Integer.class || update.get(key).getClass() == BigDecimal.class) && world.has(key + "_int")){
                if(world.getBoolean(key + "_int")) {
                    object.put(key, (update.getDouble(key) - world.getDouble(key)) / ((double) receivePeriod / 1000 * Launcher.frameRate));
                    changed = true;
                }
            }else if(update.get(key).getClass() == JSONObject.class){
                object.put(key, new JSONObject());
                try {
                    if (diffDivJSON(object.getJSONObject(key), update.getJSONObject(key), world.getJSONObject(key))) {
                        changed = true;
                    } else {
                        object.remove(key);
                    }
                }catch (JSONException e){
                    object.remove(key);
                }
            }
        }
        return changed;
    }

    private void updateJSON(JSONObject object, JSONObject update){
        for(String key: update.keySet()){
            if(update.get(key).getClass() == JSONObject.class) {
                if (object.has(key)) {
                    updateJSON(object.getJSONObject(key), update.getJSONObject(key));
                } else {
                    object.put(key, update.getJSONObject(key));
                }
            }else if(update.get(key) == JSONObject.NULL){
                object.remove(key);
            }else{
                object.put(key, update.get(key));
            }
        }
    }
}
