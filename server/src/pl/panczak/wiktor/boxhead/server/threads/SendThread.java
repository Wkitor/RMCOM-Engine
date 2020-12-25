package pl.panczak.wiktor.boxhead.server.threads;

import org.json.JSONObject;
import pl.panczak.wiktor.boxhead.server.Launcher;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class SendThread extends Thread{
    public volatile JSONObject update = new JSONObject();
    private final LinkedList<DataOutputStream> toBeRemoved = new LinkedList<>();

    public void run(){
        long nextRun = 0;
        boolean remove = false;
        while (Launcher.running){
            if (System.currentTimeMillis() - nextRun >= 0){
                nextRun = System.currentTimeMillis() + 1000 / Launcher.sendRate;

                for(DataOutputStream output: Launcher.acceptThread.outputs){
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
                        Launcher.acceptThread.outputs.remove(output);
                    }
                    toBeRemoved.clear();
                    remove = false;
                }
            }
        }
    }
}
