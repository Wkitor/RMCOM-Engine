package pl.panczak.wiktor.rmcomEngine.client.threads;

import org.json.JSONObject;
import pl.panczak.wiktor.rmcomEngine.client.Client;
import pl.panczak.wiktor.rmcomEngine.client.window.Display;
import pl.panczak.wiktor.rmcomEngine.client.window.KeyManager;
import java.awt.*;
import java.awt.image.BufferStrategy;

public abstract class DrawThread implements Runnable {
    private Display display;
    private final KeyManager keyManager;
    private JSONObject world;

    private Thread thread;
    public volatile boolean running = false;

    public final int width, height;
    public final String title;

    private int interpolationCounter;

    public DrawThread(String title, int width, int height){
        this.title = title;
        this.width = width;
        this.height = height;
        keyManager = new KeyManager();
    }

    public void setWorld(JSONObject world){
        this.world = world;
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
        addJSON(world, Client.partialUpdate);
    }

    private void render(){
        BufferStrategy bufferStrategy = display.getCanvas().getBufferStrategy();
        if(bufferStrategy == null){
            display.getCanvas().createBufferStrategy(2);
            return;
        }
        Graphics graphics = bufferStrategy.getDrawGraphics();

        // start draw

        draw(graphics);

        // end draw

        bufferStrategy.show();
        graphics.dispose();
    }

    public abstract void draw(Graphics graphics);

    public void run(){
        init();

        long nextRun = 0;
        while (running){
            if (System.currentTimeMillis() - nextRun >= 0){
                nextRun = System.currentTimeMillis() + 1000 / Client.frameRate;

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
