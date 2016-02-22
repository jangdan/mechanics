package mechanics.simulation;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import static mechanics.Simulation.*;
import static mechanics.simulation.Block.drawarrowhead;
import org.json.JSONObject;
import org.json.JSONString;

public class Vector implements JSONString {
    public double theta;
    
    double t;
    double x, y;
    
    public static double G = 0.327; //0.327 = 9.8 / 0.033333333

    public Vector(double F, double itheta){
        theta = itheta;
        
        t = F;
        x = F * Math.cos(theta);
        y = F * Math.sin(theta); //decompose F
    }
    public Vector(JSONObject iv){
        theta = iv.getDouble("theta");
        
        t = iv.getDouble("magnitude");
        x = t * Math.cos(theta);
        y = t * Math.sin(theta); //decompose F
    }
    
    public void draw(Graphics2D g, double ox, double oy){
        Line2D.Double l2d = new Line2D.Double(ox, oy, ox+x*multiplier, oy+y*multiplier);
        g.draw(l2d);
        if(drawarrow) drawarrowhead(g, theta, ox+x*multiplier, oy+y*multiplier);
    }
    
    public void set(double ifx, double ify){
        theta = Math.atan2(ify, ifx);
        
        x = ifx;
        y = ify;
        t = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    public void calculatet(){
        t = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        theta = Math.atan2(y, x);
    }
    
    public double getx(){ return x; }
    public double gety(){ return y; }
    public double gett(){ return t; }
    
    public void add(double ix, double iy){
        x += ix;
        y += iy;
        t = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        theta = Math.atan2(y, x);
    }
    public void multiply(double ixm, double iym){
        x *= ixm;
        y *= iym;
        t = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        theta = Math.atan2(y, x);
    }
    
    @Override
    public String toJSONString() {
        JSONObject jsonf = new JSONObject();
        jsonf.put("magnitude", t);
        jsonf.put("theta", theta);
        
        return jsonf.toString();
    }
}