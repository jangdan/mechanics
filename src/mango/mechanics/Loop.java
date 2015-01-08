package mango.mechanics;

import java.util.logging.*;
import static mango.mechanics.MechanicsSimulator.s;
import static mango.mechanics.Simulation.*;

public class Loop implements Runnable {
    @Override
    public void run(){
        while(!isPaused){
            refreshnullc();
            for(int b = 0; b < index; ++b){
                blocks[b].move();
            }
            s.repaint();
        
            try { Thread.sleep(sleept); }
            catch(InterruptedException e){ Logger.getLogger(Loop.class.getName()).log(Level.SEVERE, null, e); }
        }
    }
}
