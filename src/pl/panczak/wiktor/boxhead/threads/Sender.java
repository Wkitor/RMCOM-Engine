package pl.panczak.wiktor.boxhead.threads;

import java.io.DataOutputStream;
import java.io.IOException;

public class Sender {
    private final DataOutputStream output;

    public Sender(DataOutputStream output) {
        this.output = output;
    }

    public void sendUpdate(boolean w, boolean s, boolean a, boolean d){
        String data = "";
        if(w){
            data += "w";
        }
        if(s){
            data += "s";
        }
        if(a){
            data += "a";
        }
        if(d){
            data += "d";
        }

        try {
            output.writeUTF(data);
        }catch (IOException e){
            System.exit(0);
        }
    }
}
