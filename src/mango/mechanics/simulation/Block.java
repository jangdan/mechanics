package mango.mechanics.simulation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import static mango.mechanics.MechanicsSimulator.*;
import static mango.mechanics.Simulation.*;
import static mango.mechanics.simulation.Force.G;
import org.json.simple.*;

public class Block implements JSONAware {
    //Essentials
    public static int dx, dy;
    public static int dh = 40, dw = 50;
    public static double dm = 100d;
    public static int bx, by;
    
    int h, w;
    
    public int findex = 0; //forces index
    public int fnullc = 360;
    public Force[] forces = new Force[360];
    
    public Color[] forcesColor = new Color[forces.length];
    
    Rectangle2D.Double R2D = new Rectangle2D.Double();
            
    //double px, py;
    double x, y;
    //double[] xv = new double[forces.length], yv = new double[forces.length];
    
    double nx, ny; //net( force) x, y
    double ax, ay; //acceleration x, y
    double vx, vy; //velocity x, y;
    double mass;
    
    public boolean active = false;
    public boolean focused = false;
    
    //For side-scrolling-like view
    public static Force gravity;
    public static Color gravityColor = new Color(255,5,5);
    static int gravityindex = -1;
    
    double moveford = 0;
    boolean shouldmovefor = false;
    double moved = 0;
    
    double playford = 0;
    boolean shouldplayfor = false;
    long starttime = 0l;
    long elapsedt = 0l;
    
    public Block(double ix,double iy, int iw,int ih, Force ia, double im, double imoveford, double iplayford) {
        w = iw; h = ih;
        x = ix; y = iy;
        mass = im;
        gravity = new Force(G*mass, Math.PI/2);
        /*
        switch(environment){
            case 1:
                addForce(gravity, gravityColor);
                calculaten();
                break;
        }*/
        if(ia.getFt() != 0){
            addForce(ia, s.iaccc);
            calculaten();
        }
        if(imoveford > 0){
            moveford = imoveford;
            shouldmovefor = true;
        }
        if(iplayford > 0){
            playford = iplayford;
            shouldplayfor = true;
        }
    }
    public static void setbounds(int ibx,int iby) {
        bx = ibx; by = iby;
    }
    
    public double getdistance(){ return moved; }
    public double getelapsedt(){ return elapsedt; }
    
    public void move(){
        if(nx == 0 && ny == 0 && !gravitymode) return;
        else {
            if(shouldmovefor){
                if(moved >= moveford){
                    c.actionPerformed(new ActionEvent(this, -1, "p"));
                    moveford = 0;
                }
            }
            if(starttime == 0) starttime = System.currentTimeMillis();
            if(shouldplayfor){
                //if((System.currentTimeMillis() - starttime) >= playford*1000){ //absolute timing
                if(elapsedt >= playford*1000){ //relative timing
                    c.actionPerformed(new ActionEvent(this, -1, "p"));
                    starttime = 0;
                }
            }
            
            //px = x; py = y;q
            x += vx; y += vy; //the acutal moving
            
            moved += Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
            //elapsedt = System.currentTimeMillis() - starttime; //absolute timing
            elapsedt += lsleept; //relative timing
            
            if(gravitymode){
                System.out.println("DAS");
                if(gravityindex == -1){
                    gravityindex = findex;
                    addForce(gravity, gravityColor);
                    calculaten();
                }
            } else {
                if(gravityindex != -1){
                    removeForce(gravityindex);
                    gravityindex = -1;
                    ax = ay = 0;
                    vx = vy = 0;
                    calculaten();
                }
            }

            switch(Bedgehandlingmode){
                case -1:
                    if (y > by - h/2) {
                        y = by - h/2;
                        if(bounceingravity){
                            ny = -ny;
                            vy = -vy;
                        }
                        calculaten();
                    }
                    break;
                case 0: break; //don't do anything if edgehandlemode == 0 (unlimited mode)
                case 1:
                    if (x < 0) {
                        x = 0;
                        for(int a = 0; a < forces.length-fnullc; ++a){
                            forces[a].Fx = -forces[a].getFx();
                        }
                        switch(Bahandlingmode){
                            case 0:
                                vx = -vx;
                                break;
                            case 1:
                                ax = -ax;
                                vx = -vx;
                                break;
                            case 2:
                                ax = ay = 0;
                                vx = vy = 0;
                                break;
                        }
                        calculaten();
                    } else if (x > bx) {
                        x = bx;

                        for(int a = 0; a < forces.length-fnullc; ++a){
                            forces[a].Fx = -forces[a].getFx();
                        }
                        switch(Bahandlingmode){
                            case 0:
                                vx = -vx;
                                break;
                            case 1:
                                ax = -ax;
                                vx = -vx;
                                break;
                            case 2:
                                ax = ay = 0;
                                vx = vy = 0;
                                break;
                        }
                        calculaten();
                    } if (y < 0) {
                        y = 0;
                        for(int a = 0; a < forces.length-fnullc; ++a){
                            forces[a].Fy = -forces[a].getFy();
                        }
                        switch(Bahandlingmode){
                            case 0:
                                vy = -vy;
                                break;
                            case 1:
                                ay = -ay;
                                vy = -vy;
                                break;
                            case 2:
                                ax = ay = 0;
                                vx = vy = 0;
                                break;
                        }
                        calculaten();
                    } else if (y > by) {
                        y = by;
                        for(int a = 0; a < forces.length-fnullc; ++a){
                            forces[a].Fy = -forces[a].getFy();
                        }
                        switch(Bahandlingmode){
                            case 0:
                                vy = -vy;
                                break;
                            case 1:
                                ay = -ay;
                                vy = -vy;
                                break;
                            case 2:
                                ax = ay = 0;
                                vx = vy = 0;
                                break;
                        }
                        calculaten();
                    }
                break;
                case 2:

                break;
            }
            switch(Bcollisionhandlingmode){
                case 0: break; //don't do anything if collisionhandlemode == 0 (ignore mode)
                //<editor-fold defaultstate="collapsed" desc="Bounce on Block collision">
                /*case 1:
                for(int d = 0; d < blocks.length-publicgetnullc(); ++d){
                    if(blocks[d] != this){
                        if (getRectangle2D().intersects(blocks[d].getRectangle2D())) {
                            double thetadegrees = (Math.atan2(ny-(gety()+mouseytolerance/2), nx-(getx()+mousextolerance/2))*180d/Math.PI); //Probably problem here
                            int roundd = (int)Math.round(thetadegrees/90d)*90;
                            switch(roundd){

                                case -180:
                                    x = blocks[d].x-1;
                                    for(int e = 0; e < forces.length-fnullc; ++e){
                                        xv[e] = -xv[e];
                                        forces[e].Fx = -forces[e].getFx();
                                    } for(int e = 0; e < blocks[d].forces.length-blocks[d].fnullc; ++e){
                                        blocks[d].xv[e] = -blocks[d].xv[e];
                                        blocks[d].forces[e].Fx = -blocks[d].forces[e].getFx();
                                    }
                                    break;
                                case 180:
                                    x = blocks[d].x-1;
                                    for(int e = 0; e < forces.length-fnullc; ++e){
                                        xv[e] = -xv[e];
                                        forces[e].Fx = -forces[e].getFx();
                                    } for(int e = 0; e < blocks[d].forces.length-blocks[d].fnullc; ++e){
                                        blocks[d].xv[e] = -blocks[d].xv[e];
                                        blocks[d].forces[e].Fx = -blocks[d].forces[e].getFx();
                                    }
                                    break;
                                case 0:
                                    x = blocks[d].x+blocks[d].w+1;
                                    for(int e = 0; e < forces.length-fnullc; ++e){
                                        xv[e] = -xv[e];
                                        forces[e].Fx = -forces[e].getFx();
                                    } for(int e = 0; e < blocks[d].forces.length-blocks[d].fnullc; ++e){
                                        blocks[d].xv[e] = -blocks[d].xv[e];
                                        blocks[d].forces[e].Fx = -blocks[d].forces[e].getFx();
                                    }
                                    break;

                                case -90:
                                    y = blocks[d].y+blocks[d].h+1;
                                    for(int e = 0; e < forces.length-fnullc; ++e){
                                        yv[e] = -yv[e];
                                        forces[e].Fy = -forces[e].getFy();
                                    } for(int e = 0; e < blocks[d].forces.length-blocks[d].fnullc; ++e){
                                        blocks[d].yv[e] = -blocks[d].yv[e];
                                        blocks[d].forces[e].Fy = -blocks[d].forces[e].getFy();
                                    }
                                    break;
                                case 90:
                                    y = blocks[d].y-1;
                                    for(int e = 0; e < forces.length-fnullc; ++e){
                                        yv[e] = -yv[e];
                                        blocks[d].forces[e].Fy = -blocks[d].forces[e].getFy();
                                    } for(int e = 0; e < blocks[d].forces.length-blocks[d].fnullc; ++e){
                                        blocks[d].yv[e] = -blocks[d].yv[e];
                                        blocks[d].forces[e].Fy = -blocks[d].forces[e].getFy();
                                    }
                                    break;
                            }
                            calculaten();
                        }
                    }
                }
                break;*///</editor-fold>
            }
            accelerate();
        }
    }
    private void calculaten() { //(add up the Forces and calculate the net force
        nx = ny = 0;
        for(int a = 0; a < forces.length-fnullc; ++a){
            nx += forces[a].getFx(); ny += forces[a].getFy();
        }
    } public void accelerate() {
            if(allowacceleration){
                ax = nx/mass; ay = ny/mass;
                vx += ax; vy += ay;
            } else {
                ax = ay = 0;
                vx = nx; vy = ny;
            }
    }
    
    FontMetrics FM;
    int charWidth;
    float[] gravitydashdash = {9}; Stroke gravitydash = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, gravitydashdash, 0);
    float[] pdashdash = {6}; Stroke pdash = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, pdashdash, 0);
    
    Stroke defaultStroke = new BasicStroke(1);
    
    DecimalFormat massDF = new DecimalFormat("#0.0");
    
    public void draw(Graphics2D g) {
        FM = g.getFontMetrics(numbersFont);
        charWidth = FM.charWidth('0');
        
        R2D.setRect(x, y, w, h);
        g.setColor(s.blockc);
        g.draw(R2D);
        
        if(drawLine||drawnF||drawlabels||drawParellelogram||drawacceleration||drawmass){
            double box = getx()+mousextolerance/2; double boy = gety()+mouseytolerance/2;
            for(int c = 0; c < forces.length-fnullc; ++c){
                double theta = Math.atan2(boy+forces[c].getFy()*multiplier-boy, box+forces[c].getFx()*multiplier-box); //angle of slope
                if(drawLine){
                    g.setColor(s.linec);
                    if(RandomColorline) g.setColor(forcesColor[c]);
                    if(forces[c].Ft == G){
                        g.setStroke(gravitydash);
                        g.setColor(forcesColor[c]);
                    }

                    Line2D.Double line = new Line2D.Double(box, boy, box+forces[c].getFx()*multiplier, boy+forces[c].getFy()*multiplier);
                    g.draw(line);

                    if(drawarrow){
                        drawFarrowhead(g, theta, box+forces[c].getFx()*multiplier, boy+forces[c].getFy()*multiplier);
                    }
                    if(forces[c].Ft == G){
                        g.setStroke(defaultStroke);
                        g.setColor(s.linec);
                    }
                    if(RandomColorline) g.setColor(s.linec);
                    
                }
                //<editor-fold defaultstate="collapsed" desc="Draw numbers">
                /*if(drawnumbers){
                    g.setFont(numbersFont);
                    String digitsformatbase = "#0.0000000000000000";
                    for(int i = 0; i <= 16-digits; ++i) digitsformatbase = digitsformatbase.substring(0, digitsformatbase.length()-1);

                    if(RandomColorline) g.setColor(new Color(forcesColor[c].getRed(), forcesColor[c].getGreen(), forcesColor[c].getBlue(), 192));

                    String lformatbase = "#0.0000000000000000"; //lengthformatbase
                    double theta = Math.atan2(boy+forces[c].getFy()*multiplier-boy, box+forces[c].getFx()*multiplier-box); //angle of slope

                    AffineTransform AT = new AffineTransform();

                    String formatbase = "";
                    for(int i = 0; i < Math.min(digitsformatbase.length(), lformatbase.length()); ++i){
                        if(lformatbase.charAt(i) == digitsformatbase.charAt(i)){
                            formatbase = formatbase + lformatbase.charAt(i);
                        }
                    }
                    DecimalFormat DF = new DecimalFormat(formatbase);

                    if(-Math.PI/2 > theta || Math.PI/2 < theta){
                        AT.translate(box+forces[c].getFx()*multiplier, boy+forces[c].getFy()*multiplier);
                        AT.scale(-1, -1);
                        AT.rotate(theta);

                        g.setTransform(AT);
                        g.drawString(DF.format(forces[c].Ft), -formatbase.length()*charWidth, numbersFontsize/2);
                    } else {
                        AT.translate(box+forces[c].getFx()*multiplier, boy+forces[c].getFy()*multiplier);
                        AT.rotate(theta);

                        g.setTransform(AT);
                        g.drawString(DF.format(forces[c].Ft), numbersFontsize/2, numbersFontsize/2);
                    }
                    g.setTransform(new AffineTransform());

                    if(RandomColorline) g.setColor(Block.defaultForceColor);
                }*///</editor-fold>
                if(drawlabels){
                    g.setFont(labelsFont);

                    AffineTransform AT = new AffineTransform();
                    if(drawLine){
                        AttributedString F = new AttributedString("F"+c);
                        F.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, Integer.toString(c).length()+1);

                        AT.translate(box+forces[c].getFx()*multiplier, boy+forces[c].getFy()*multiplier);

                        g.setTransform(AT);
                        if(-Math.PI/2 > theta || Math.PI/2 < theta){
                            g.drawString(F.getIterator(), -15, -6);
                        } else {
                            g.drawString(F.getIterator(), 6,6);
                        }
                    }

                    g.setTransform(new AffineTransform());
                }
            } if(drawnF || (360-fnullc == 2)){
                if(fnullc != 360){
                    Line2D.Double nF = new Line2D.Double(box, boy, box+nx*multiplier, boy+ny*multiplier); //net Force
                    g.setStroke(new BasicStroke(2));
                    g.draw(nF);

                    if(drawarrow){
                        double theta = Math.atan2(nF.y2-nF.y1, nF.x2-nF.x1); //angle of slope
                        drawFarrowhead(g, theta, box+nx*multiplier, boy+ny*multiplier);
                    }

                    g.setStroke(new BasicStroke(1));
                }
            } if(drawParellelogram){
                if(360-fnullc == 2){
                    AffineTransform AT = new AffineTransform();
                    g.setStroke(pdash);
                    Line2D.Double pF1 = new Line2D.Double(0, 0, forces[1].getFx()*multiplier, forces[1].getFy()*multiplier);
                    AT.translate(box+forces[0].getFx()*multiplier, boy+forces[0].getFy()*multiplier);
                    AT.scale(1, 1);
                    g.setTransform(AT);
                    g.draw(pF1);
                    
                    AT = new AffineTransform();
                    g.setTransform(AT);
                    
                    Line2D.Double pF0 = new Line2D.Double(0, 0, forces[0].getFx()*multiplier, forces[0].getFy()*multiplier);
                    AT.translate(box+forces[1].getFx()*multiplier, boy+forces[1].getFy()*multiplier);
                    AT.scale(1, 1);
                    g.setTransform(AT);
                    g.draw(pF0);
                    g.setTransform(new AffineTransform());
                    g.setStroke(defaultStroke);
                }
            } if(drawmass){
                g.setFont(massFont);
                g.drawString(massDF.format(mass), (float)x+2, (float)boy-h/3+8);
            } if(drawacceleration){
                if(ax != 0 || ay != 0){
                    Line2D.Double ac = new Line2D.Double(box, boy, box+ax*multiplier, boy+ay*multiplier); //acceleration
                    g.setColor(s.accc);
                    g.setStroke(new BasicStroke(2));
                    g.draw(ac);

                    if(drawarrow){
                        double theta = Math.atan2(ac.y2-ac.y1, ac.x2-ac.x1); //angle of slope
                        drawFarrowhead(g, theta, box+ax*multiplier, boy+ay*multiplier);
                    }
                    g.setColor(s.linec);
                    g.setStroke(new BasicStroke(1));
                }
            } if(drawlabels){
                g.setFont(labelsFont);

                AffineTransform AT = new AffineTransform();
                if(drawnF || (360-fnullc == 2)){
                    if(nx != 0 && ny != 0){
                        double theta = Math.atan2(boy+ny*multiplier-boy, box+nx*multiplier-box); //angle of slope
                        AttributedString Fn = new AttributedString("Fn");
                        Fn.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);

                        AT.translate(box+nx*multiplier, boy+ny*multiplier);

                        g.setTransform(AT);
                        if(-Math.PI/2 > theta || Math.PI/2 < theta){
                            g.drawString(Fn.getIterator(), -15, -6);
                        } else {
                            g.drawString(Fn.getIterator(), 6,6);
                        }
                        g.setTransform(new AffineTransform());
                    }
                }
                AT = new AffineTransform();
                if(drawacceleration && allowacceleration && (ax != 0 || ay != 0)){
                    double theta = Math.atan2(boy+ay*multiplier-boy, box+ax*multiplier-box); //angle of slope
                    AT.translate(box+ax*multiplier, boy+ay*multiplier);

                    g.setTransform(AT);
                    if(-Math.PI/2 > theta || Math.PI/2 < theta){
                        g.drawString("a", -15, -6);
                    } else {
                        g.drawString("a", 6,6);
                    }
                    g.setTransform(new AffineTransform());
                }
            }
        } 
    }
    public static void drawFarrowhead(Graphics2D G2D, double theta, double ox, double oy){ //draw Force arrowhead
        Line2D.Double arrowr = new Line2D.Double(0, 0, 6, -6); //right arrowhead
        Line2D.Double arrowl = new Line2D.Double(0, 0, -6, -6); //left arrowhead

        AffineTransform AT = new AffineTransform();
        AT.translate(ox, oy);
        AT.rotate(theta - Math.PI/2);
        G2D.setTransform(AT);

        G2D.draw(arrowr);
        G2D.draw(arrowl);
        G2D.setTransform(new AffineTransform());
    }
    
    public void addForce(Force F, Color FC) {
        forces[findex] = F;
        forcesColor[findex] = FC;
        ++findex;
        --fnullc;
        
        calculaten();
    } public void removeForce(int ifindex) {
        forces[ifindex] = null;
        --findex;
        ++fnullc;
        
        calculaten();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Old moving code">
    /*
    public void updateAcceleration() {
        if(allowacceleration){
            acceleration.Fx += getnetx()/getmass();
            acceleration.Fy += getnety()/getmass();
        } else {
            acceleration = new Force(0,0);
        }
    }
    private void updatenetForce() {
        nx = ny = 0;
        for(int a = 0; a < forces.length-fnullc; ++a){
            nx += xv[a]; ny += yv[a];
        }
        updateAcceleration();
    } private void applyAcceleration() {
        nx += acceleration.getFx(); ny += acceleration.getFy();
    }
    */
    //</editor-fold>
    public double getx() {
        return x;
    } public double gety() {
        return y;
    } public int getwidth() {
        return w;
    } public int getheight() {
        return h;
    }
    public double getmass() {
        return mass;
    }
    public void active() {
        active = true;
    } public void unactive() {
        active = false;
    } public boolean isactive() {
        return active;
    }
    public void focus() {
        focused = true;
    } public void unfocus() {
        focused = false;
    } public boolean isfocused() {
        return focused;
    }

    public Rectangle2D.Double getRectangle2D() {
        return R2D;
    }
    
    public void setinitialmovement(double inx, double iny, double iax, double iay, double ivx, double ivy) {
        nx = inx; ny = iny;
        ax = iax; ay = iay;
        vx = ivx; vy = ivy;
    }
    
    private final JSONObject JSONBlock = new JSONObject();
    @Override
    public String toJSONString() {
        JSONArray JSONforces = new JSONArray();
        for(int b = 0; b < forces.length-fnullc; ++b){
            JSONforces.add(forces[b]);
        }
        JSONBlock.put("forces", JSONforces);
        
        JSONArray JSONforcesColor = new JSONArray();
        for(int c = 0; c < forces.length-fnullc; ++c){
            JSONforcesColor.add(Integer.toString(forcesColor[c].getRGB()));
        }
        JSONBlock.put("forcesColor", JSONforcesColor);
        
        JSONBlock.put("x", x);
        JSONBlock.put("y", y);
        JSONBlock.put("nx", nx);
        JSONBlock.put("ny", ny);
        JSONBlock.put("ax", ax);
        JSONBlock.put("ay", ay);
        JSONBlock.put("vx", vx);
        JSONBlock.put("vy", vy);
        JSONBlock.put("mass", mass);
        JSONBlock.put("moveford", moveford);
        JSONBlock.put("playford", playford);
        
        return JSONBlock.toString();
    }
}