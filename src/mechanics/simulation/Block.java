package mechanics.simulation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import static mechanics.MechanicsSimulator.*;
import static mechanics.Simulation.*;
import static mechanics.simulation.Vector.G;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class Block extends Object implements JSONString {
    //Essentials
    public static int dx, dy;
    public static final int dh = 40, dw = 50;
    public static final double dm = 50d;
    
    public int borderx, bordery;
    
    
    public int index;
    
    public int findex = 0; //forces index
    public Vector[] forces = new Vector[360];
    public Color[] forcesColor = new Color[forces.length];
    
    public Rectangle2D.Double r = new Rectangle2D.Double();
    
    
    public int h, w;
    
    double px, py;
    public double x, y;
    double mass;
    
    Vector n = new Vector(0,0);
    
    double pv, ppv;
    Vector v = new Vector(0,0);
    
    Vector a = new Vector(0,0);
    
    double work;
    double power;
    double totalwork;
    double totalpower;
    
    public boolean active = false;
    public boolean focused = false;
    
    //For side-scrolling-like view
    public Vector gravity;
    
    Vector friction;
    boolean iskineticf = false;
    
    Vector normal;
    
    boolean onGround = false;
    
    double moveford = 0;
    boolean shouldmovefor = false;
    double moved = 0;
    
    double playford = 0;
    boolean shouldplayfor = false;
    long starttime = 0l;
    long elapsedt = 0l;
    
    boolean moving = false;
    
    boolean keycontrolledForces = false;
    static double deviation = 0.1d;
    
    public Block(double ix, double iy, int iw, int ih, Vector ia, double im, double imoveford, double iplayford){
        w = iw; h = ih;
        x = ix; y = iy;
        mass = im;
        
        refreshBounds();
        
        gravity = new Vector(G*mass, Math.PI/2);
        
        if(ia.gett() != 0){
            v.set(ia.x,ia.y);
            iskineticf = true;
        }
        calculaten();
        
        if(imoveford > 0){
            moveford = imoveford;
            shouldmovefor = true;
        }
        if(iplayford > 0){
            playford = iplayford;
            shouldplayfor = true;
        }
        
        index = simulation.bindex;
    }
    public void refreshBounds(){
        borderx = simulation.getWidth() - w; bordery = simulation.getHeight() - h;
    }
    
    public void load(Vector in, Vector ia, Vector iv, long istarttime, long ielapsedt, double imoved,
                     double iw, double itw, double ip, double itp, boolean ikcF,
                     double ipx, double ipy, double ipv, double ippv, boolean ikineticf){
        n = in;
        a = ia;
        v = iv;
        
        moved = imoved;
        starttime = istarttime;
        elapsedt = ielapsedt;
        
        work = iw;
        totalwork = itw;
        power = ip;
        totalpower = itp;
        keycontrolledForces = ikcF;
        
        px = ipx; py = ipy;
        pv = ipv; ppv = ippv;
        
        iskineticf = ikineticf;
    }
    
    public double getvelocity(){ return v.t; }
    public double getdistance(){ return moved; }
    public double getelapsedt(){ return elapsedt; }
    public double getwork(){ return work; }
    public double getpower(){ return power; }
    public double gettotalwork(){ return totalwork; }
    public double gettotalpower(){ return totalpower; }
    
    public void active(){ active = true; }
    public void unactive(){ active = false; }
    public boolean isactive(){ return active; }
    public void focus(){ focused = true; }
    public void unfocus(){ focused = false; }
    public boolean isfocused(){ return focused;}

    
    public void addForce(Vector F, Color FC){
        forces[findex] = F;
        forcesColor[findex] = FC;
        ++findex;
        
        calculaten();
    } public void removeForce(int ifindex){
        forces[ifindex] = null;
        --findex;
        
        calculaten();
    }
    public void resetForces(){
        forces = new Vector[360];
        findex = 0;
        forcesColor = new Color[360];
        calculaten();
    }
    
    
    long timer;
    
    public void move(){
        if((kup || kdown || kleft || kright) && findex == 0 && focused){
            keycontrolledForces = true;
            addForce(new Vector(0,0), new Color(0,0,0));
        }
        if(keycontrolledForces){
            if(kup){
                forces[0].add(0, -deviation);
            } if(kdown){
                forces[0].add(0, deviation);
            } if(kleft){
                forces[0].add(-deviation, 0);
            } if(kright){
                forces[0].add(deviation, 0);
            }
            calculaten();
        }
        
        if(n.x != 0 || n.y != 0 || gravitymode){
            moving = true;
            if(System.currentTimeMillis()-timer > 1000){
                timer = System.currentTimeMillis();
            }
            if(shouldmovefor){
                if(moved >= moveford){
                    controls.actionPerformed(new ActionEvent(this, -1, "p"));
                    moveford = 0;
                }
            }
            if(starttime == 0) starttime = System.currentTimeMillis();
            if(shouldplayfor){
                //if((System.currentTimeMillis() - starttime) >= playford*1000){ //absolute timing
                if(elapsedt >= playford*1000){ //relative timing
                    controls.actionPerformed(new ActionEvent(this, -1, "p"));
                    elapsedt = 0;
                }
            }
            
            if((!gravitymode && ppv - 0.000001 < v.t&& v.t < ppv + 0.000001 && findex == 0)){
                v.set(0,0);
            }
            calculaten();
            ppv = pv;
            pv = v.t;
            px = x; py = y;
            x += v.x; y += v.y; //the acutal moving
            
            //<editor-fold defaultstate="collapsed" desc="collision with edge">
            switch(Bedgehandlingmode){
                case -1:
                    if(y > bordery - h/2){
                        y = bordery - h/2;
                        if(bounceingravity){
                            n.y = -n.y;
                            v.y = -v.y;
                        } else {
                            v.set(0,0);
                        }
                        onGround = true;
                        calculaten();
                    } else {
                        if(n.t != 0){
                            onGround = false;
                        }
                    }
                    break;
                case 1:
                    if (x < 0) {
                        x = 0;
                        for(int j = 0; j < findex; ++j) forces[j].multiply(-simulation.cor, 1);
                        switch(Bahandlingmode){
                            case 0:
                                v.multiply(-simulation.cor, 1);
                                break;
                            case 1:
                                a.multiply(-1, 1);
                                v.multiply(-simulation.cor, 1);
                                break;
                            case 2:
                                a.set(0,0);
                                v.set(0,0);
                                break;
                        }
                        calculaten();
                    } else if (x > borderx) {
                        x = borderx;
                        for(int j = 0; j < findex; ++j) forces[j].multiply(-simulation.cor, 1);
                        switch(Bahandlingmode){
                            case 0:
                                v.multiply(-simulation.cor, 1);
                                break;
                            case 1:
                                a.multiply(-1, 1);
                                v.multiply(-simulation.cor, 1);
                                break;
                            case 2:
                                a.set(0,0);
                                v.set(0,0);
                                break;
                        }
                        calculaten();
                    } if (y < 0) {
                        y = 0;
                        for(int j = 0; j < findex; ++j) forces[j].multiply(1, -simulation.cor);
                        switch(Bahandlingmode){
                            case 0:
                                v.multiply(1, -simulation.cor);
                                break;
                            case 1:
                                a.multiply(1, -1);
                                v.multiply(1, -simulation.cor);
                                break;
                            case 2:
                                a.x = a.y = 0;
                                v.x = v.y = 0;
                                break;
                        }
                        calculaten();
                    } else if (y > bordery) {
                        y = bordery;
                        for(int j = 0; j < findex; ++j) forces[j].multiply(1, -simulation.cor);
                        switch(Bahandlingmode){
                            case 0:
                                v.multiply(1, -simulation.cor);
                                break;
                            case 1:
                                a.y = -a.y;
                                v.y = -v.y;
                                break;
                            case 2:
                                a.x = a.y = 0;
                                v.x = v.y = 0;
                                break;
                        }
                        calculaten();
                    }
                    break;
                case 2:
                    break;
            }
            //</editor-fold>
            
            switch(Bcollisionhandlingmode){
                //don't do anything if collisionhandlemode == 0 (ignore mode)
                case 1:
                    for(int i = 0; i < simulation.bindex; ++i){
                        if(index == i || (i <= index && simulation.blocks[i].moving)) continue;
                        Rectangle2D.Double minkowskid = new Rectangle2D.Double(x-simulation.blocks[i].x-simulation.blocks[i].w, y-simulation.blocks[i].y-simulation.blocks[i].h, w+simulation.blocks[i].w, h+simulation.blocks[i].h);
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
                            
                            x -= penx; y -= peny;
                            
                            
                            if(penx == 0){
                                for(int j = 0; j < findex; ++j) forces[j].y = -forces[j].gety();
                                v.multiply(1, -1);
                                calculaten();
                                if(simulation.blocks[i].moving){
                                    for(int j = 0; j < simulation.blocks[i].findex; ++j) simulation.blocks[i].forces[j].y = -simulation.blocks[i].forces[j].gety();
                                    simulation.blocks[i].v.multiply(1, -simulation.cor);
                                    simulation.blocks[i].calculaten();
                                }
                            }
                            if(peny == 0){
                                for(int j = 0; j < findex; ++j) forces[j].x = -forces[j].getx();
                                v.multiply(-1, 1);
                                calculaten();
                                if(simulation.blocks[i].moving){
                                    for(int j = 0; j < simulation.blocks[i].findex; ++j) simulation.blocks[i].forces[j].x = -simulation.blocks[i].forces[j].getx();
                                    simulation.blocks[i].v.multiply(-simulation.cor, 1);
                                    simulation.blocks[i].calculaten();
                                }
                            }
                        }
                    }
                    break;
            }
            
            accelerate();
            
            moved += v.t;
            //elapsedt = System.currentTimeMillis() - starttime; //absolute timing
            elapsedt += lsleept; //relative timing
            work = n.t*v.t;
            power = work/((double)lsleept/1000);
            totalwork += n.t*moved;
            totalpower += power;
        } else {
            moving = false;
        }
    }
    
    public void accelerate() {
        if(allowacceleration){
            a.set(n.x/mass, n.y/mass);
            v.add(a.x,a.y);
        } else {
            a.set(0,0);
            v.set(n.x,n.y);
        }
    }
    
    public final void calculaten(){ //(add up the Forces and calculate the net force
        n.set(0,0);
        
        for(int i = 0; i < findex; ++i){
            n.x += forces[i].getx();
            n.y += forces[i].gety();
        }
        if(gravitymode){
            n.x += gravity.x;
            n.y += gravity.y;
        }
        
        n.calculatet();
        if(addfriction){
            if(!gravitymode || (gravitymode && iskineticf)){
                headsupfriction();
                n.x += friction.x; n.y += friction.y;
            }
        }
        if(onGround){
            normal = new Vector(gravity.t,gravity.theta-Math.PI);
            n.x += normal.x; n.y += normal.y;
        }
        
        n.calculatet();
    }
    public void bulldozegravity(){
        n.set(0,0);
        v.set(0,0);
        a.set(0,0);
    }
    
    public void headsupfriction(){
        if(!iskineticf){ //is static friction
            friction = new Vector(n.t,n.theta-Math.PI);
            if(friction.t > G*mass*simulation.cosf){
                iskineticf = true;
            }
        } else {
            v.calculatet();
            friction = new Vector(G*mass*simulation.cokf, v.theta-Math.PI);
            if(v.t <= 0){ //supposed to be v.blocky <= 0 but the computer doesnt allow that.
                iskineticf = false;
            }
        }
    }
    
    FontMetrics FM;
    int charWidth;
    float[] gravitydashdash = {9}; Stroke gravitydash = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, gravitydashdash, 0);
    float[] pdashdash = {6}; Stroke pdash = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, pdashdash, 0);
    
    Stroke defaultStroke = new BasicStroke(1);
    
    DecimalFormat massDF = new DecimalFormat("#0.0");
    
    public void draw(Graphics2D g){
        FM = g.getFontMetrics(numbersFont);
        charWidth = FM.charWidth('0');
        
        r.setRect(x, y, w, h);
        
        g.setColor(simulation.blockc);
        g.draw(r);
        
        if(drawLine||drawnF||drawlabels||drawParellelogram||drawacceleration||drawmass||drawvelocity){
            double box = r.getCenterX(); double boy = r.getCenterY();
            for(int c = 0; c < findex; ++c){
                if(drawLine){
                    g.setColor(simulation.linec);
                    if(RandomColorline) g.setColor(forcesColor[c]);
                    forces[c].draw(g, box, boy);
                    if(RandomColorline) g.setColor(simulation.linec);
                }
                //<editor-fold defaultstate="collapsed" desc="Draw numbers">
                /*if(drawnumbers){
                    g.setFont(numbersFont);
                    String digitsformatbase = "#0.0000000000000000";
                    for(int i = 0; i <= 16-digits; ++i) digitsformatbase = digitsformatbase.substring(0, digitsformatbase.length()-1);

                    if(RandomColorline) g.setColor(new Color(forcesColor[controls].getRed(), forcesColor[controls].getGreen(), forcesColor[controls].getBlue(), 192));

                    String lformatbase = "#0.0000000000000000"; //lengthformatbase
                    double theta = Math.atan2(boy+forces[controls].gety()*multiplier-boy, box+forces[controls].getx()*multiplier-box); //angle of slope

                    AffineTransform AT = new AffineTransform();

                    String formatbase = "";
                    for(int i = 0; i < Math.min(digitsformatbase.length(), lformatbase.length()); ++i){
                        if(lformatbase.charAt(i) == digitsformatbase.charAt(i)){
                            formatbase = formatbase + lformatbase.charAt(i);
                        }
                    }
                    DecimalFormat DF = new DecimalFormat(formatbase);

                    if(-Math.PI/2 > theta || Math.PI/2 < theta){
                        AT.translate(box+forces[controls].getx()*multiplier, boy+forces[controls].gety()*multiplier);
                        AT.scale(-1, -1);
                        AT.rotate(theta);

                        g.setTransform(AT);
                        g.drawString(DF.format(forces[controls].timer), -formatbase.length()*charWidth, numbersFontsize/2);
                    } else {
                        AT.translate(box+forces[controls].getx()*multiplier, boy+forces[controls].gety()*multiplier);
                        AT.rotate(theta);

                        g.setTransform(AT);
                        g.drawString(DF.format(forces[controls].timer), numbersFontsize/2, numbersFontsize/2);
                    }
                    g.setTransform(new AffineTransform());
                    g.setTransform(camera);

                    if(RandomColorline) g.setColor(Block.defaultForceColor);
                }*///</editor-fold>
                if(drawlabels){
                    g.setFont(labelsFont);

                    AffineTransform AT = new AffineTransform();
                    if(drawLine){
                        AttributedString F = new AttributedString("F"+c);
                        F.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, Integer.toString(c).length()+1);

                        AT.concatenate(camera);
                        AT.translate(box+forces[c].getx()*multiplier, boy+forces[c].gety()*multiplier);

                        g.setTransform(AT);
                        if(-Math.PI/2 > forces[c].theta || Math.PI/2 < forces[c].theta){
                            g.drawString(F.getIterator(), -15, -6);
                        } else {
                            g.drawString(F.getIterator(), 6,6);
                        }
                    }
                    g.setTransform(camera);
                }
            }
            
            if(drawLine){
                if(gravitymode){
                    g.setColor(simulation.gc);
                    gravity.draw(g, box, boy);
                    g.setColor(simulation.linec);
                    
                    if(drawlabels){
                        AffineTransform at = new AffineTransform();
                        g.setFont(labelsFont);
                        
                        AttributedString gra = new AttributedString("G");

                        at.concatenate(camera);
                        at.translate(box+gravity.x*multiplier, boy+gravity.y*multiplier);

                        g.setTransform(at);
                        if(-Math.PI/2 > gravity.theta || Math.PI/2 < gravity.theta){
                            g.drawString(gra.getIterator(), -15, -6);
                        } else {
                            g.drawString(gra.getIterator(), 6,6);
                        }
                        g.setTransform(camera);
                    }
                }
                if(addfriction){
                    if(!gravitymode || (gravitymode && iskineticf)){
                        g.setColor(simulation.fc);
                        friction.draw(g, box, boy);
                        g.setColor(simulation.linec);
                        
                        if(drawlabels){
                            AffineTransform at = new AffineTransform();
                            g.setFont(labelsFont);

                            if(friction.x != 0 && friction.y != 0){
                                AttributedString Ff = new AttributedString("Ff");
                                Ff.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);

                                at.concatenate(camera);
                                at.translate(box+friction.x*multiplier, boy+friction.y*multiplier);

                                g.setTransform(at);
                                if(-Math.PI/2 > friction.theta || Math.PI/2 < friction.theta){
                                    g.drawString(Ff.getIterator(), -15, -6);
                                } else {
                                    g.drawString(Ff.getIterator(), 6,6);
                                }
                                g.setTransform(camera);
                            }
                        }
                    }
                }
                if(onGround && normal != null){
                    g.setColor(simulation.nc);
                    normal.draw(g, box, boy);
                    g.setColor(simulation.linec);
                    
                    if(drawlabels){
                        AffineTransform at = new AffineTransform();
                        g.setFont(labelsFont);
                        
                        AttributedString no = new AttributedString("N");

                        at.concatenate(camera);
                        at.translate(box+normal.x*multiplier, boy+normal.y*multiplier);

                        g.setTransform(at);
                        if(-Math.PI/2 > normal.theta || Math.PI/2 < normal.theta){
                            g.drawString(no.getIterator(), -15, -6);
                        } else {
                            g.drawString(no.getIterator(), 6,6);
                        }
                        g.setTransform(camera);
                    }
                }
            }
            if(drawnF || (drawParellelogram && findex == 2)){
                g.setStroke(new BasicStroke(2));
                n.draw(g, box, boy);
                g.setStroke(new BasicStroke(1));
                
                if(drawlabels){
                    AffineTransform at = new AffineTransform();
                    g.setFont(labelsFont);
                    if(n.x != 0 && n.y != 0){
                        AttributedString Ff = new AttributedString("Fn");
                        Ff.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);

                        at.concatenate(camera);
                        at.translate(box+n.x*multiplier, boy+n.y*multiplier);

                        g.setTransform(at);
                        if(-Math.PI/2 > n.theta || Math.PI/2 < n.theta){
                            g.drawString(Ff.getIterator(), -15, -6);
                        } else {
                            g.drawString(Ff.getIterator(), 6,6);
                        }
                        g.setTransform(camera);
                    }
                }
            }
            if(drawParellelogram && findex == 2){
                AffineTransform AT = new AffineTransform();
                AT.concatenate(camera);
                
                g.setStroke(pdash);
                Line2D.Double pF1 = new Line2D.Double(0, 0, forces[1].getx()*multiplier, forces[1].gety()*multiplier);
                AT.translate(box+forces[0].getx()*multiplier, boy+forces[0].gety()*multiplier);
                AT.scale(1, 1);
                g.setTransform(AT);
                g.draw(pF1);

                AT = new AffineTransform();
                AT.concatenate(camera);
                g.setTransform(AT);

                Line2D.Double pF0 = new Line2D.Double(0, 0, forces[0].getx()*multiplier, forces[0].gety()*multiplier);
                AT.translate(box+forces[1].getx()*multiplier, boy+forces[1].gety()*multiplier);
                AT.scale(1, 1);
                g.setTransform(AT);
                g.draw(pF0);
                g.setTransform(camera);
                g.setStroke(defaultStroke);
            }
            if(drawmass){
                g.setFont(massFont);
                g.drawString(massDF.format(mass), (float)x+2, (float)y + massFont.getSize2D());
            }
            if(drawacceleration && (a.x != 0 || a.y != 0)){
                Line2D.Double ac = new Line2D.Double(box, boy, box+a.x*multiplier, boy+a.y*multiplier); //acceleration
                g.setColor(simulation.accc);
                g.setStroke(new BasicStroke(2));
                g.draw(ac);
                if(drawarrow){
                    drawarrowhead(g, a.theta, box+a.x*multiplier, boy+a.y*multiplier);
                }
                g.setColor(simulation.linec);
                g.setStroke(new BasicStroke(1));
                
                if(drawlabels){
                    AffineTransform at = new AffineTransform();
                    g.setFont(labelsFont);
                    
                    at.translate(box+a.x*multiplier, boy+a.y*multiplier);
                    at.concatenate(camera);
                    
                    g.setTransform(at);
                    if(-Math.PI/2 > a.theta || Math.PI/2 < a.theta){
                        g.drawString("a", -15, -6);
                    } else {
                        g.drawString("a", 6,6);
                    }
                    g.setTransform(camera);
                }
            }
            if(drawvelocity){
                g.setColor(simulation.vc);
                v.draw(g, box, boy);
                g.setColor(simulation.linec);
                
                if(drawlabels){
                    AffineTransform at = new AffineTransform();
                    g.setFont(labelsFont);
                    
                    at.translate(box+v.x*multiplier, boy+v.y*multiplier);
                    at.concatenate(camera);
                    
                    g.setTransform(at);
                    if(-Math.PI/2 > v.theta || Math.PI/2 < v.theta){
                        g.drawString("v", -15, -6);
                    } else {
                        g.drawString("v", 6,6);
                    }
                    g.setTransform(camera);
                }
            }
        } 
    }
    public static void drawarrowhead(Graphics2D g, double theta, double ox, double oy){ //draw Vector arrowhead
        Line2D.Double arrowr = new Line2D.Double(0, 0, 5, -5); //right arrowhead
        Line2D.Double arrowl = new Line2D.Double(0, 0, -5, -5); //left arrowhead

        AffineTransform AT = new AffineTransform();
        AT.concatenate(camera);
        AT.translate(ox, oy);
        AT.rotate(theta - Math.PI/2);
        g.setTransform(AT);

        g.draw(arrowr);
        g.draw(arrowl);
        
        g.setTransform(camera);
    }
    
    
    @Override
    public String toJSONString() {
        JSONObject jsonb = new JSONObject();
        
        JSONArray jsonforces = new JSONArray();
        for(int i = 0; i < findex; ++i) jsonforces.put(forces[i]);
        jsonb.put("forces", jsonforces);
        
        JSONArray jsonforcescolor = new JSONArray();
        for(int i = 0; i < findex; ++i) jsonforcescolor.put(Integer.toString(forcesColor[i].getRGB()));
        jsonb.put("forcesColor", jsonforcescolor);
        
        jsonb.put("h",h); jsonb.put("w",w);
        jsonb.put("px",px); jsonb.put("py",py);
        jsonb.put("x",x); jsonb.put("y",y);
        jsonb.put("mass",mass);
        jsonb.put("moveford",moveford);
        jsonb.put("playford",playford);
        
        jsonb.put("moved", moved);
        jsonb.put("starttime", starttime);
        jsonb.put("elapsedt", elapsedt);
        
        jsonb.put("n",n);
        jsonb.put("v",v);
        jsonb.put("pv",pv);
        jsonb.put("ppv",ppv);
        jsonb.put("a",a);
        
        jsonb.put("work",work);
        jsonb.put("totalwork",totalwork);
        jsonb.put("power",power);
        jsonb.put("totalpower",totalpower);
        
        jsonb.put("gravity",gravity);
        jsonb.put("normal", normal);
        jsonb.put("friction",friction);
        jsonb.put("iskineticf",iskineticf);
        
        jsonb.put("keycontrolledForces",keycontrolledForces);
        
        return jsonb.toString();
    }
}