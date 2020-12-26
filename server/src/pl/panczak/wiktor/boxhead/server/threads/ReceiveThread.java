package pl.panczak.wiktor.boxhead.server.threads;

import pl.panczak.wiktor.boxhead.server.Server;
import java.net.Socket;
import java.io.*;

public class ReceiveThread extends Thread{
    private DataInputStream input;
    public int id;

    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;

    public ReceiveThread(Socket socket){
        try {
            input = new DataInputStream(socket.getInputStream());
        }catch (Exception e){
            Server.running = false;
            e.printStackTrace();
        }
    }

    private void updateKeys(String data){
        up = false;
        down = false;
        left = false;
        right = false;

        int len = data.length();
        for (int i = 0; i < len; i++) {
            char c = data.charAt(i);
            if(c == 'w'){
                up = true;
            }else if(c == 's'){
                down = true;
            }else if(c == 'a'){
                left = true;
            }else if(c == 'd'){
                right = true;
            }
        }
    }

    public void run(){
        id = Server.worldLogic.connect();

        try {
            while (Server.running) {
                String data = input.readUTF();
                updateKeys(data);
            }
        }catch (IOException e){
            Server.acceptThread.receiveThreads.remove(this);
            Server.worldLogic.disconnect(id);
            System.out.println("client disconnected");
        }
    }
}
