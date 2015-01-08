package mango.mechanics.simulation;

import org.json.simple.*;

public class Force implements JSONAware {
    public double theta;
    
    double Ft;
    double Fx, Fy;
    
    public static final double G = 9.80665/9.80665*3;

    /**
     *
     * @param F The force in (relative)Newtons
     * @param itheta The angle in radians
     */
    public Force(double F, double itheta) {
        theta = itheta;
        
        Ft = F;
        Fx = F * Math.cos(theta);
        Fy = F * Math.sin(theta); //decompose F
    }
    
    public double getFx() {
        return this.Fx;
    } public double getFy() {
        return this.Fy;
    } public double getFt() {
        return Ft;
    } public double gettheta() {
        return Ft;
    }
    
    public static double toRadians(int idegrees) {
        return idegrees*Math.PI/180;
    }

    private final JSONObject JSONForce = new JSONObject();
    @Override
    public String toJSONString() {
        JSONForce.put("magnitude", Ft);
        JSONForce.put("theta", theta);
        
        return JSONForce.toString();
    }
}
