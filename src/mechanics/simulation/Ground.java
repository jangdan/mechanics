package mechanics.simulation;

import java.awt.*;
import static mechanics.MechanicsSimulator.simulation;

public class Ground {
    Rectangle R = new Rectangle();
    int x, y;
    int w ;final int h = Block.dh/2;
    private final static Color GrColor = new Color(150,100,50);
    
    public Ground(){
        w = 1000000;
    }
    public void draw(Graphics2D G2D){
        R.setRect(x, y, w, h);
        G2D.setColor(GrColor);
        G2D.fill(R);
        G2D.setColor(simulation.linec);
    }
    public void update(){
        w = 1000000;
        x = -w/2; y = simulation.getHeight() - h;
    }
    
    public Rectangle getRectangle(){
        return R;
    }
}
