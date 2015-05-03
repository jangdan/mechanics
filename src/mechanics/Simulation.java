package mechanics;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import mechanics.simulation.Ground;
import mechanics.simulation.Block;
import mechanics.simulation.Vector;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.ArrayList;
import javax.swing.*;
import static mechanics.MechanicsSimulator.*;
import static mechanics.simulation.Block.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class Simulation extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, ComponentListener, JSONString {
    //Essentials 
    static Dimension size = new Dimension(840,480);
    
    static Loop l;
    static Loop.LogicLoop ll;
    static Thread llth;
    static Loop.GraphicsLoop gl;
    static Thread glth;
    public static int lsleept = 30;
    public boolean isPaused = false;
    
    public static final double multiplier = 50d;
    
    //<editor-fold defaultstate="collapsed" desc="Settings">
    public static boolean drawarrow = true;
    public static boolean drawLine = true;
    public static boolean drawnF = false;
    
    //public static boolean drawnumbers = true;
    public static int digits = 16;
    
    public static boolean drawParellelogram = true;
    public static boolean RandomColorline = false;
    public static boolean previewForcemagnitude = true;
    public static boolean allowacceleration = true;
    public static boolean drawacceleration = true;
    public static boolean drawvelocity = true;
    
    public static int Bedgehandlingmode = 0; //Block edge handling mode, 0: unlimited, 1: bounce, 2: wrap, -1: gravity
    public static int Bcollisionhandlingmode = 1; //Block collision handling mode, 0: ignore, 1: bounce
    public static int Bahandlingmode = 0; //Block acceleration handling mode, 0: Keep, 1: Reverse, 2: Reset
    
    public static boolean gravitymode = false;
    public static boolean bounceingravity = false;
    
    public static boolean addfriction = true;
    //</editor-fold>
    
    public static final int buffersize = 320;
    public Block[] blocks = new Block[buffersize];
    public int bnullc = buffersize;
    public int bindex = 0;
    
    public ArrayList<Block> movingclub = new ArrayList<>();
    
    public static int currenttheme = 0; //0: default, 1: dark, 2: bright
    public Color backc, blockc, linec, accc, iaccc, textc, gc, fc, nc, vc;
    
    public double cosf = 0.2d, cokf = 0.1d; //coefficients of friction
    public double cor = 1d; //coefficient of restitution
    
    //For side-scrolling-like view
    public Ground Gr;
    
    public static final Font numbersFont = new Font(Font.MONOSPACED, Font.PLAIN, 8);
    
    public static boolean drawmass = true;
    public static final Font massFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    
    public static boolean drawlabels = true;
    public static final Font labelsFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    
    static Font cffont = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    
    public static AffineTransform camera = new AffineTransform();
    public static boolean usecamera = false;
    
    public Simulation(){
        setPreferredSize(size);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addComponentListener(this);
        setFocusable(true);
        requestFocusInWindow();
        
        l = new Loop();
        
        ll = l.new LogicLoop();
        llth = new Thread(ll);
        
        gl = l.new GraphicsLoop();
        glth = new Thread(gl);
    }
    
    
    public final void play(){
        isPaused = false;
        llth = new Thread(ll);
        llth.setName("logic");
        glth = new Thread(gl);
        glth.setName("graphics");
        llth.start();
        glth.start();
    }
    public final void pause(){
        isPaused = true;
        llth.interrupt();
        glth.interrupt();
    }
    
    
    public void addBlock(double ix, double iy, int iw, int ih, Vector ia, double im, double bmoveford, double bplayford){
        blocks[bindex] = new Block(ix,iy, iw,ih, ia, im, bmoveford, bplayford);
        if(isPaused) repaint();
        ++bindex;
    }
    public void removeBlock(int iindex){
        if(blocks[iindex].isfocused()){
            deinitializefocus();
        }
        blocks[iindex] = null;
        for(int b = iindex; b < buffersize-1; ++b){
            blocks[b] = blocks[b+1];
            blocks[b+1] = null;
        }
        --bindex;
    }
    public void clear(){
        for(int i = bindex-1; i >= 0; --i){
            removeBlock(i);
        }
    }
    
    
    boolean shouldusecamera(){ return hascfblock && blocks[cfblock] != null && usecamera; }
    public double getdmousex(){
        if(shouldusecamera()) return blocks[cfblock].x - getWidth()/2;
        return 0;
    }
    public double getdmousey(){
        if(shouldusecamera()) return blocks[cfblock].y - getHeight()/2;
        return 0;
    }
    
    
    public void calculaten(){
        for(int i = 0; i < bindex; ++i) blocks[i].calculaten();
    }
    public void bulldozegravity(){
        for(int i = 0; i < bindex; ++i) blocks[i].bulldozegravity();
    }
    
    @Override
    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        Graphics2D g = (Graphics2D)G;
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        
        camera = new AffineTransform();
        if(shouldusecamera()){
            camera.translate(-blocks[cfblock].x + getWidth()/2, -blocks[cfblock].y + getHeight()/2);
        }
        g.setTransform(camera);
        
        if(shouldusecamera()) g.drawRect(0, 0, getWidth(), getHeight());
        
        if(gravitymode && Gr != null) Gr.draw(g);
        
        
        NumberFormat nf = new DecimalFormat("#0.000");
        for(int b = 0; b < bindex; ++b){
            blocks[b].draw(g);
            
            double box = blocks[b].r.getCenterX(), boy = blocks[b].r.getCenterY();
            if(hascablock && blocks[b].isactive()){
                g.setColor(linec);
                if(RandomColorline) g.setColor(cColor); //set the Force Color to the current Color
                
                Line2D.Double hbline = new Line2D.Double(box, boy, cx, cy);
                g.draw(hbline);
                
                if(drawarrow || previewForcemagnitude){
                    double theta = Math.atan2(hbline.y2-hbline.y1, hbline.x2-hbline.x1); //angle of slop.
                    if(drawarrow){
                        drawarrowhead(g, theta, cx, cy);
                    }
                    if(previewForcemagnitude){
                        AffineTransform backup = g.getTransform();
                        
                        AffineTransform at = new AffineTransform();
                        at.concatenate(camera);
                        at.translate(cx, cy);
                        g.setTransform(at);
                        
                        if(theta > Math.PI/2 || theta < -Math.PI/2){
                            g.drawString(""+nf.format(Math.sqrt(Math.pow(cx-box, 2) + Math.pow(cy-boy, 2))), -60, 6);
                        } else {
                            g.drawString(""+nf.format(Math.sqrt(Math.pow(cx-box, 2) + Math.pow(cy-boy, 2))), 6, 6);
                        }
                        g.setTransform(backup);
                    }
                }
                if(RandomColorline) g.setColor(linec);
            }
        }
        if(hascfblock && blocks[cfblock] != null){
            g.setFont(cffont);
            g.drawString(rb.getString("f.totalpower")+": "+nf.format(blocks[cfblock].gettotalpower()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4);
            g.drawString(rb.getString("f.totalwork")+": "+nf.format(blocks[cfblock].gettotalwork()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4 -cffont.getSize()-4);
            g.drawString(rb.getString("f.power")+": "+nf.format(blocks[cfblock].getpower()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4 +2*(-cffont.getSize()-4));
            g.drawString(rb.getString("f.work")+": "+nf.format(blocks[cfblock].getwork()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4 +3*(-cffont.getSize()-4));
            g.drawString(rb.getString("f.time")+": "+nf.format(blocks[cfblock].getelapsedt()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4 +4*(-cffont.getSize()-4));
            g.drawString(rb.getString("f.distance")+": "+nf.format(blocks[cfblock].getdistance()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4 +5*(-cffont.getSize()-4));
            g.drawString(rb.getString("f.velocity")+": "+nf.format(blocks[cfblock].getvelocity()), (int)blocks[cfblock].x, (int)blocks[cfblock].y-4 +6*(-cffont.getSize()-4));
        }
        
        g.dispose();
    }
    
    
    double sx, sy; //start blockx, blocky
    double ex, ey; //end blockx, blocky
    double cx, cy; //current blockx, blocky
    
    double ddistance; //drag distance
    boolean duringd = false; //during drag
    
    int cablock = -1; //currently active block
    boolean hascablock = false; //if there is a currently active block
    public static Color cColor; //current Color
    
    public int cfblock = -1; //currently focused block
    boolean hascfblock = false; //if there is a currently focused block
    
    double fdistance;
    double fdistancex, fdistancey;
    
    boolean currentlypressed;
    
    @Override
    public void mousePressed(MouseEvent e) {
        if(currentlypressed){ //If the LeftMouseButton is already pressed and held
            deinitialize();
        }
        if(SwingUtilities.isLeftMouseButton(e)){
            currentlypressed = true;
            int r, g, b;
            requestFocusInWindow();
            for(int a = 0; a < bindex; ++a){
                if(blocks[a].x - getdmousex() <= e.getX()&&e.getX() <= blocks[a].x+blocks[a].w - getdmousex()
                && blocks[a].y - getdmousey() <= e.getY()&&e.getY() <= blocks[a].y+blocks[a].h - getdmousey()){
                    sx = e.getX() + getdmousex(); sy = e.getY() + getdmousey();
                    cx = e.getX() + getdmousex(); cy = e.getY() + getdmousey();

                    cablock = a;
                    blocks[cablock].active();
                    do {
                       r = rand.nextInt(255);
                       g = rand.nextInt(255);
                       b = rand.nextInt(255);
                    } while((r == 0 && g == 0 && b == 0)
                            ||(r <= 64 && g == 0 && b == 0)
                            ||(g <= 64 && b == 0 && r == 0)
                            ||(b <= 64 && r == 0 && g == 0)
                            ||(r <= 64 && g <= 64 && b == 0)
                            ||(g <= 64 && b <= 64 && r == 0)
                            ||(b <= 64 && r <= 64 && g == 0)
                            ||(r <= 32 && b <= 32 && g <= 32));
                    cColor = new Color(r, g, b);

                    hascablock = true;
                }
            }
            if(!hascablock){
                addBlock(e.getX()-controls.blockw/2 + getdmousex(), e.getY()-controls.blockh/2 + getdmousey(), controls.blockw, controls.blockh, new Vector(controls.initialv, controls.initialvtheta), controls.blockm, controls.moveford, controls.playford);
            }
        } else if(SwingUtilities.isRightMouseButton(e)){
            currentlypressed = false;
            requestFocusInWindow();
            for(int a = 0; a < bindex; ++a){
                if(blocks[a].x - getdmousex() <= e.getX()&&e.getX() <= blocks[a].x+blocks[a].w - getdmousex()
                && blocks[a].y - getdmousey() <= e.getY()&&e.getY() <= blocks[a].y+blocks[a].h - getdmousey()){
                    removeBlock(a);
                }
            }
        } else {
            currentlypressed = false;
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)){
            if(hascablock){
                ex = e.getX() + getdmousex(); ey = e.getY() + getdmousey();
                if(round){
                    switch(roundd){
                        case -180: case 0: case 180:
                            ex = e.getX() + getdmousex(); ey = blocks[cablock].r.getCenterY() + getdmousey();
                            break;
                        case -90: case 90:
                            ex = blocks[cablock].r.getCenterX() + getdmousex(); ey = e.getY() + getdmousey();
                            break;
                    }
                }
                ddistance = Math.sqrt(Math.pow(ex-sx, 2) + Math.pow(ey-sy, 2));
                if(Math.abs(ex-(blocks[cablock].r.getCenterX())) > blocks[cablock].w/2 //Commenting this will allow very small forces to be added.
                || Math.abs(ey-(blocks[cablock].r.getCenterY())) > blocks[cablock].h/2){ 
                    fdistancex = ex-(blocks[cablock].r.getCenterX());
                    fdistancey = ey-(blocks[cablock].r.getCenterY());
                    fdistance = Math.sqrt(Math.pow(fdistancex, 2) + Math.pow(fdistancey, 2));
                    blocks[cablock].addForce(new Vector(fdistance/multiplier,
                                             Math.atan2(ey-(blocks[cablock].r.getCenterY()),ex-(blocks[cablock].r.getCenterX()))),
                                             cColor);
                    blocks[cablock].calculaten();
                    deinitialize();
                } else {
                    cfblock = cablock;
                    deinitialize();
                    blocks[cfblock].focus();
                    hascfblock = true;
                }
            } else {
                deinitialize();
            }
        } else {
            cancel();
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if(hascablock){
            cx = e.getX() + getdmousex(); cy = e.getY() + getdmousey();
            if(round){
                double thetadegrees = (Math.atan2(cy-(blocks[cablock].r.getCenterY()), cx-(blocks[cablock].r.getCenterX()))*180d/Math.PI);
                roundd = (int)Math.round(thetadegrees/90d)*90;
                switch(roundd){
                    case -180: case 0: case 180:
                        cx = e.getX(); cy = blocks[cablock].r.getCenterY();
                        break;
                    case -90: case 90:
                        cx = blocks[cablock].r.getCenterX(); cy = e.getY();
                        break;
                }
            }
        }
        if(isPaused) repaint();
    }
    
    void cancel(){ //cancel dragging and adding a Vector
        deinitialize();
        deinitializefocus();
    }
    void deinitialize(){ //reset most of the variables so the Simulation can add the next force
        sx = sy = ex = ey = 0;
        if(isPaused) repaint();
        for(int c = 0; c < bindex; ++c) blocks[c].unactive();
        cablock = -1;
        cColor = null;
        hascablock = false;
        round = false;
    }
    void deinitializefocus(){
        if(cfblock != -1 && blocks[cfblock] != null){
            blocks[cfblock].unfocus();
            cfblock = -1;
        }
        hascfblock = false;
    }
    
    boolean round = false;
    int roundd; //round(ed) degrees
    
    public static boolean kup = false, kleft = false, kdown = false, kright = false;
    
    @Override
    public void keyPressed(KeyEvent e){
        if(hascablock){
            switch(e.getKeyCode()){
                case KeyEvent.VK_SHIFT:
                    round = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                    deinitialize();
                    break;
            }
        }
        if(hascfblock && blocks[cfblock] != null){
            switch(e.getKeyCode()){
                case KeyEvent.VK_W: case KeyEvent.VK_UP:
                    kup = true;
                    break;
                case KeyEvent.VK_A: case KeyEvent.VK_LEFT:
                    kleft = true;
                    break;
                case KeyEvent.VK_S: case KeyEvent.VK_DOWN:
                    kdown = true;
                    break;
                case KeyEvent.VK_D: case KeyEvent.VK_RIGHT:
                    kright = true;
                    break;
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e){
        round = false;
        if(hascfblock && blocks[cfblock] != null){
            switch(e.getKeyCode()){
                case KeyEvent.VK_W: case KeyEvent.VK_UP:
                    kup = false;
                    break;
                case KeyEvent.VK_A: case KeyEvent.VK_LEFT:
                    kleft = false;
                    break;
                case KeyEvent.VK_S: case KeyEvent.VK_DOWN:
                    kdown = false;
                    break;
                case KeyEvent.VK_D: case KeyEvent.VK_RIGHT:
                    kright = false;
                    break;
                case KeyEvent.VK_0:
                    blocks[cfblock].resetForces();
                    break;
                case KeyEvent.VK_C:
                    usecamera = !usecamera;
                    break;
            }
        }
    }
    
    
    @Override
    public void componentResized(ComponentEvent e){
        if(gravitymode && Gr != null) Gr.update();
        for(int i = 0; i < bindex; ++i) blocks[i].refreshBounds();
    }
    
    
    @Override
    public String toJSONString(){
        JSONObject jsons = new JSONObject();
        
        jsons.put("isPaused",isPaused);
        
        jsons.put("drawarrow",drawarrow);
        jsons.put("drawLine",drawLine);
        jsons.put("drawnF",drawnF);
        
        jsons.put("digits",digits);
        
        jsons.put("drawParellelogram",drawParellelogram);
        jsons.put("RandomColorline",RandomColorline);
        jsons.put("previewForcemagnitude",previewForcemagnitude);
        jsons.put("allowacceleration",allowacceleration);
        jsons.put("drawacceleration",drawacceleration);
        jsons.put("drawvelocity",drawvelocity);
        
        jsons.put("Bedgehandlingmode",Bedgehandlingmode);
        jsons.put("Bcollisionhandlingmode",Bcollisionhandlingmode);
        jsons.put("Bahandlingmode",Bahandlingmode);
        
        jsons.put("gravitymode",gravitymode);
        jsons.put("bounceingravity",bounceingravity);
        
        jsons.put("addfriction",addfriction);

        JSONArray jsonblocks = new JSONArray();
        for(int i = 0; i < bindex; ++i) jsonblocks.put(blocks[i]);
        jsons.put("blocks", jsonblocks);
        
        JSONArray jsonmovingclub = new JSONArray();
        for(int i = 0; i < movingclub.size(); ++i) jsonblocks.put(movingclub.get(i));
        jsons.put("movingclub", jsonmovingclub);
        
        jsons.put("currenttheme", currenttheme);
        
        jsons.put("usecamera", usecamera);
        
        jsons.put("hascablock", hascablock);
        if(hascablock){
            jsons.put("cablock",cablock);
        }
        
        jsons.put("hascfblock", hascfblock);
        if(hascfblock){
            jsons.put("cfblock",cfblock);
        }
        
        return jsons.toString();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){} @Override
    public void mouseMoved(MouseEvent e){} @Override
    public void componentMoved(ComponentEvent CE) {} @Override
    public void componentShown(ComponentEvent CE) {} @Override
    public void componentHidden(ComponentEvent CE) {} @Override
    public void keyTyped(KeyEvent KE) {} @Override
    public void mouseClicked(MouseEvent ME) {} @Override
    public void mouseEntered(MouseEvent ME) {} @Override
    public void mouseExited(MouseEvent ME) {}
}