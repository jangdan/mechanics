package mango.mechanics;

import static mango.mechanics.MechanicsSimulator.s;
import static mango.mechanics.Simulation.*;

public class Loop {
    public class LogicLoop implements Runnable {
        @Override
        public void run(){
            while(!isPaused){
                refreshnullc();
                for(int b = 0; b < index; ++b){
                    blocks[b].move();
                }

                try { Thread.sleep(lsleept); }
                catch(InterruptedException e){}
            }
        }
    }
    public class GraphicsLoop implements Runnable {
        @Override
        public void run(){
            while(!isPaused){
                s.repaint();

                try { Thread.sleep(lsleept); }
                catch(InterruptedException e){}
            }
        }
    }
}
