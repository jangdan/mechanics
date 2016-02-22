package mechanics.simulation;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import static mechanics.MechanicsSimulator.simulation;

public class Ground extends SObject {
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
    
    @Override
    public void handleCollision(SObject iso){
        if(iso instanceof Block){
            Block cBlock = (Block)iso;
            if(cBlock.moving){
                Rectangle2D.Double minkowskid = new Rectangle2D.Double(x-cBlock.x-cBlock.w, y-cBlock.y-cBlock.h, w+cBlock.w, h+cBlock.h);
                if(minkowskid.contains(0,0)){
                    double minDist = Math.abs(minkowskid.getMinX());
                    double penx = minkowskid.getMinX(); double peny = 0;

                    if(Math.abs(minkowskid.getMaxX()) < minDist){
                            minDist = Math.abs(minkowskid.getMaxX());
                            penx = minkowskid.getMaxX(); peny = 0;
                    }
                    if(Math.abs(minkowskid.getMinY()) < minDist){
                            minDist = Math.abs(minkowskid.getMinY());
                            penx = 0; peny = minkowskid.getMinY();
                    }
                    if(Math.abs(minkowskid.getMaxY()) < minDist){
                            minDist = Math.abs(minkowskid.getMaxY());
                            penx = 0; peny = minkowskid.getMaxY();
                    }

                    cBlock.x += penx; cBlock.y += peny;
                    cBlock.onGround = true;

                    if(penx == 0){
                        //for(int j = 0; j < cBlock.findex; ++j) cBlock.forces[j].multiply(1, -1);
                        cBlock.v.multiply(1, -simulation.cor);
                        cBlock.calculaten();
                    }
                    if(peny == 0){
                        //for(int j = 0; j < cBlock.findex; ++j) cBlock.forces[j].multiply(-1, 1);
                        cBlock.v.multiply(-simulation.cor, 1);
                        cBlock.calculaten();
                    }
                } else {
                    cBlock.onGround = false;
                }
            }
        }
    }
}
