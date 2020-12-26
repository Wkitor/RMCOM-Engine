package pl.panczak.wiktor.rmcomEngine.server.threads;

import pl.panczak.wiktor.rmcomEngine.server.Server;
import pl.panczak.wiktor.rmcomEngine.server.world.WorldLogic;

public class TickThread extends Thread{
    private final WorldLogic worldLogic;
    private long tickNumber;

    public TickThread(WorldLogic worldLogic){
        this.worldLogic = worldLogic;
        tickNumber = 0;
    }

    public void run(){
        long nextRun = 0;
        long lastRun = System.currentTimeMillis();
        while (Server.running){
            if (System.currentTimeMillis() - nextRun >= 0){
                nextRun = System.currentTimeMillis() + 1000 / Server.tickRate;
                worldLogic.tick(tickNumber, (double)(System.currentTimeMillis() - lastRun) / 1000);
                lastRun = System.currentTimeMillis();
                tickNumber++;
            }
        }
    }
}
