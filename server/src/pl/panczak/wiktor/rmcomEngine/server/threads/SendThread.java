package pl.panczak.wiktor.rmcomEngine.server.threads;

import org.json.JSONObject;
import pl.panczak.wiktor.rmcomEngine.server.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class SendThread extends Thread{
    public volatile JSONObject update = new JSONObject();
    private final LinkedList<DataOutputStream> toBeRemoved = new LinkedList<>();

    public void run(){
        long nextRun = 0;
        boolean remove = false;
        while (Server.running){
            if (System.currentTimeMillis() - nextRun >= 0){
                nextRun = System.currentTimeMillis() + 1000 / Server.sendRate;

                for(DataOutputStream output: Server.acceptThread.outputs){
                    try {
                        output.writeUTF(update.toString());
                    } catch (IOException e) {
                        toBeRemoved.add(output);
                        remove = true;
                    }
                }
                update = new JSONObject();
                if(remove) {
                    for (DataOutputStream output : toBeRemoved) {
                        Server.acceptThread.outputs.remove(output);
                    }
                    toBeRemoved.clear();
                    remove = false;
                }
            }
        }
    }
}
