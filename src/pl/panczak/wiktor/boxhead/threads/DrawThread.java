package pl.panczak.wiktor.boxhead.threads;

import org.json.JSONException;
import org.json.JSONObject;
import pl.panczak.wiktor.boxhead.Launcher;
import pl.panczak.wiktor.boxhead.window.Display;
import pl.panczak.wiktor.boxhead.window.KeyManager;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class DrawThread implements Runnable {
    private Display display;
    private final KeyManager keyManager;
    private final JSONObject world;

    private Thread thread;
    public volatile boolean running = false;

    public final int width, height;
    public final String title;

    private int interpolationCounter;

    public DrawThread(String title, int width, int height, JSONObject world){
        this.title = title;
        this.width = width;
        this.height = height;
        this.world = world;
        keyManager = new KeyManager();
    }

    private void init(){
        display = new Display(title, width, height);
        display.getFrame().addKeyListener(keyManager);
    }

    private void addJSON(JSONObject object, JSONObject update){
        for(String key: update.keySet()){
            if(update.get(key).getClass() == JSONObject.class){
                addJSON(object.getJSONObject(key), update.getJSONObject(key));
            }else{
                object.put(key, object.getDouble(key) + update.getDouble(key));
            }
        }
    }

    private void tick(){
        addJSON(world, Launcher.partialUpdate);
    }

    private void render(){
        BufferStrategy bufferStrategy = display.getCanvas().getBufferStrategy();
        if(bufferStrategy == null){
            display.getCanvas().createBufferStrategy(2);
            return;
        }
        Graphics graphics = bufferStrategy.getDrawGraphics();

        // start draw

        graphics.clearRect(0, 0, width, height);

        graphics.setColor(Color.RED);

        if(world.has("players")) {
            JSONObject players = world.getJSONObject("players");

            for (String key : players.keySet()) {
                graphics.fillOval((int) players.getJSONObject(key).getDouble("x"), (int) players.getJSONObject(key).getDouble("y"), 50, 50);
            }
        }

        // end draw

        bufferStrategy.show();
        graphics.dispose();
    }

    public void run(){
        init();

        long nextRun = 0;
        while (running){
            if (System.currentTimeMillis() - nextRun >= 0){
                nextRun = System.currentTimeMillis() + 1000 / Launcher.frameRate;

                render();
                tick();
            }
        }
    }

    public synchronized void start(){
        if(running){
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop(){
        if(!running){
            return;
        }
        try{
            running = false;
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
