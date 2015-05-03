package mechanics;

import static mechanics.MechanicsSimulator.*;
import static mechanics.Simulation.*;

public class Loop {
    public class LogicLoop implements Runnable {
        @Override
        public void run(){
            while(!simulation.isPaused){
                for(int b = 0; b < simulation.bindex; ++b) simulation.blocks[b].move();
                
                try { Thread.sleep(lsleept); }
                catch(InterruptedException e){}
            }
        }
    }
    public class GraphicsLoop implements Runnable {
        @Override
        public void run(){
            while(!simulation.isPaused){
                simulation.repaint();
                try { Thread.sleep(lsleept); }
                catch(InterruptedException e){}
            }
        }
    }
}
