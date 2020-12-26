package pl.panczak.wiktor.boxhead.window;

import pl.panczak.wiktor.boxhead.Client;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
    private final boolean[] keys;
    public volatile boolean w, s, a, d;

    public KeyManager(){
        keys = new boolean[256];
    }

    public void update(){
        w = keys[KeyEvent.VK_W];
        s = keys[KeyEvent.VK_S];
        a = keys[KeyEvent.VK_A];
        d = keys[KeyEvent.VK_D];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        update();
        Client.sender.sendUpdate(w, s, a, d);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        update();
        Client.sender.sendUpdate(w, s, a, d);
    }

    @Override
    public void keyTyped(KeyEvent e){}
}
