package mango.mechanics.simulation.environment;

import mango.mechanics.simulation.Block;
import java.awt.*;
import static mango.mechanics.MechanicsSimulator.s;

public class Ground {
    Rectangle R = new Rectangle();
    int x, y;
    int w ;final int h = Block.dh/2;
    private final static Color GrColor = new Color(150,100,50);
    
    public Ground() {
        w = 3*s.getWidth(); 
    }
    public void draw(Graphics2D G2D){
        R.setRect(x, y, w, h);
        G2D.setColor(GrColor);
        G2D.fill(R);
        G2D.setColor(Block.defaultForceColor);
    }
    public void update() {
        w = 3*s.getWidth();
        x = -s.getWidth(); y = s.getHeight() - h;
    }
    
    public Rectangle getRectangle(){
        return R;
    }
}
