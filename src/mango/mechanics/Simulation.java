package mango.mechanics;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import mango.mechanics.simulation.environment.Ground;
import mango.mechanics.simulation.Block;
import mango.mechanics.simulation.Force;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;
import static mango.mechanics.MechanicsSimulator.*;
import static mango.mechanics.simulation.Block.*;
import org.json.simple.*;

public class Simulation extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ComponentListener, JSONAware {
    //Essentials 
    static Dimension size = new Dimension(720,480);
    
    static Loop l;
    static Loop.LogicLoop ll;
    static Thread llth;
    static Loop.GraphicsLoop gl;
    static Thread glth;
    public static int lsleept = 30;
    public static boolean isPaused = false;
    
    public static final double multiplier = 100d;
    
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
    
    public static int Bedgehandlingmode = 0; //Block edge handling mode, 0: unlimited, 1: bounce, 2: wrap, -1: gravity
    public static int Bcollisionhandlingmode = 0; //Block collision handling mode, 0: ignore, 1: bounce
    public static int Bahandlingmode = 0; //Block acceleration handling mode, 0: Keep, 1: Reverse, 2: Reset
    
    public static boolean gravitymode = false;
    public static boolean bounceingravity = true;
    //</editor-fold>
    
    public static final int buffersize = 32;
    public static Block[] blocks = new Block[buffersize];
    public static int nullc = buffersize;
    public static int index = 0;
    
    public static final int mousextolerance = Block.dw, mouseytolerance = Block.dh;
    
    public static int currenttheme = 0; //0: default, 1: dark, 2: bright
    public Color backc, blockc, linec, accc, iaccc, textc;
    
    //For side-scrolling-like view
    public Ground Gr;
    
    public static final Font numbersFont = new Font(Font.MONOSPACED, Font.PLAIN, 8);
    
    public static boolean drawmass = true;
    public static final Font massFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    
    public static boolean drawlabels = true;
    public static final Font labelsFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    
    static Font cffont = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    
    public Simulation() {
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
    
    public static void refreshnullc() {
        nullc = 0;
        for(int a = 0; a < blocks.length; ++a){
            if(blocks[a] == null) ++nullc;
        }
    }
    
    public final void play() {
        llth = new Thread(ll);
        glth = new Thread(gl);
        llth.start();
        glth.start();
        isPaused = false;
    } public final void pause() {
        llth.interrupt();
        glth.interrupt();
        isPaused = true;
    }
    
    @Override
    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        Graphics2D g = (Graphics2D)G;
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        if(gravitymode && Gr != null) Gr.draw(g);
        
        refreshnullc();
        NumberFormat nf = new DecimalFormat("#0.000");
        for(int b = 0; b < index; ++b){
            blocks[b].draw(g);
            
            double box = blocks[b].getx()+mousextolerance/2, boy = blocks[b].gety()+mouseytolerance/2;
            if(hascablock && blocks[b].isactive()){
                g.setColor(linec);
                if(RandomColorline) g.setColor(cColor); //set the Force Color to the current Color
                
                Line2D.Double hbline = new Line2D.Double(box, boy, cx, cy);
                g.draw(hbline);
                
                if(drawarrow || previewForcemagnitude){
                    double theta = Math.atan2(hbline.y2-hbline.y1, hbline.x2-hbline.x1); //angle of slope
                    if(drawarrow){
                        drawFarrowhead(g, theta, cx, cy);
                    }
                    if(previewForcemagnitude){
                        AffineTransform at = new AffineTransform();
                        at.translate(cx, cy);
                        
                        AffineTransform backup = g.getTransform();
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
            g.drawString(rb.getString("f.distance")+": "+nf.format(blocks[cfblock].getdistance()), (int)blocks[cfblock].getx(), (int)blocks[cfblock].gety()-2);
            g.drawString(rb.getString("f.time")+": "+nf.format(blocks[cfblock].getelapsedt()), (int)blocks[cfblock].getx(), (int)blocks[cfblock].gety()-2-cffont.getSize()-2);
        }
        g.dispose();
    }
    
    public void addBlock(double ix,double iy, int iw,int ih, Force ia, double im, double bmoveford, double bplayford){
        blocks[index] = new Block(ix,iy, iw,ih, ia, im, bmoveford, bplayford);
        refreshnullc();
        if(isPaused) repaint();
        ++index;
        --nullc;
    }
    public void removeBlock(int iindex){
        if(blocks[iindex].focused){
            deinitializefocus();
        }
        blocks[iindex] = null;
        for(int b = iindex; b < buffersize-1; ++b){
            blocks[b] = blocks[b+1];
            blocks[b+1] = null;
        }
        --index;
        ++nullc;
    }
    public void clear() {
        for(int i = index-1; i >= 0; --i){
            removeBlock(i);
        }
    }
    
    
    double sx, sy; //start x, y
    double ex, ey; //end x, y
    double cx, cy; //current x, y
    
    double ddistance; //drag distance
    boolean duringd = false; //during drag
    
    int cablock; //currently active block
    boolean hascablock = false; //if there is a currently active block
    public static Color cColor; //current Color
    
    int cfblock; //currently focused block
    boolean hascfblock = false; //if there is a currently focused block
    
    double fdistance;
    double fdistancex, fdistancey;
    
    double Fv;
    
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
            for(int a = 0; a < index; ++a){
                if(blocks[a].getx() <= e.getX()&&e.getX() <= blocks[a].getx()+mousextolerance
                && blocks[a].gety() <= e.getY()&&e.getY() <= blocks[a].gety()+mouseytolerance){
                    sx = e.getX(); sy = e.getY();
                    cx = e.getX(); cy = e.getY();

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
                addBlock(e.getX()-Block.dw/2,e.getY()-Block.dh/2, Block.dw,Block.dh, new Force(c.nba, c.nbatheta), c.nbm, c.moveford, c.playford);
            }
        } else if(SwingUtilities.isRightMouseButton(e)){
            currentlypressed = false;
            requestFocusInWindow();
            for(int a = 0; a < blocks.length-nullc; ++a){
                if(blocks[a].getx() <= e.getX()&&e.getX() <= blocks[a].getx()+mousextolerance
                && blocks[a].gety() <= e.getY()&&e.getY() <= blocks[a].gety()+mouseytolerance){
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
                ex = e.getX(); ey = e.getY();
                if(round){
                    switch(roundd){
                        case -180: case 0: case 180:
                            ex = e.getX(); ey = blocks[cablock].gety()+mouseytolerance/2;
                            break;
                        case -90: case 90:
                            ex = blocks[cablock].getx()+mousextolerance/2; ey = e.getY();
                            break;
                    }
                }
                ddistance = Math.sqrt(Math.pow(ex-sx, 2) + Math.pow(ey-sy, 2));
                if(Math.abs(ex-(blocks[cablock].getx()+mousextolerance/2)) > mousextolerance/2 //Commenting this will allow very small forces to be added.
                || Math.abs(ey-(blocks[cablock].gety()+mouseytolerance/2)) > mouseytolerance/2){ 
                    fdistancex = ex-(blocks[cablock].getx()+mousextolerance/2);
                    fdistancey = ey-(blocks[cablock].gety()+mouseytolerance/2);
                    fdistance = Math.sqrt(Math.pow(fdistancex, 2) + Math.pow(fdistancey, 2));
                    blocks[cablock].addForce(new Force(fdistance/multiplier,
                                             Math.atan2(ey-(blocks[cablock].gety()+mouseytolerance/2),ex-(blocks[cablock].getx()+mousextolerance/2))),
                                             cColor);
                    deinitialize();
                } else {
                    cfblock = cablock;
                    deinitialize();
                    blocks[cfblock].active();
                    hascfblock = true;
                }
            } else {
                deinitialize();
            }
        } else {
            cancel();
        }
    }
    
    void cancel() { //cancel dragging and adding a Force
        deinitialize();
        deinitializefocus();
    }
    void deinitialize() { //reset most of the variables so the Simulation can add the next force
        sx = sy = ex = ey = 0;
        if(isPaused) repaint();
        for(int c = 0; c < index; ++c) blocks[c].unactive();
        cablock = -1;
        cColor = null;
        hascablock = false;
        round = false;
    }
    void deinitializefocus() {
        cfblock = -1;
        hascfblock = false;
    }
    
    @Override
    public void mouseDragged(MouseEvent ME) {
        if(hascablock){
            cx = ME.getX(); cy = ME.getY();
            if(round){
                //if(isPaused){
                double thetadegrees = (Math.atan2(cy-(blocks[cablock].gety()+mouseytolerance/2), cx-(blocks[cablock].getx()+mousextolerance/2))*180d/Math.PI);
                roundd = (int)Math.round(thetadegrees/90d)*90;
                switch(roundd){
                    case -180: case 0: case 180:
                        cx = ME.getX(); cy = blocks[cablock].gety()+mouseytolerance/2;
                        break;
                    case -90: case 90:
                        cx = blocks[cablock].getx()+mousextolerance/2; cy = ME.getY();
                        break;
                }
                //}
            }
            if(intforces){
                //intforce = (int)Math.round(3d); 278348dn894023890d829048230d489230n4d802n384d90nn80
            }
        }
        if(isPaused) repaint();
    }
    
    boolean round = false;
    int roundd; //round(ed) degrees
    
    boolean intforces = false;
    int intforce; //magnitude of the "integerized" force 
    
    @Override
    public void keyPressed(KeyEvent KE) {
        if(hascablock){
            switch(KE.getKeyCode()){
                case KeyEvent.VK_SHIFT:
                    //if(isPaused){
                    round = true;
                    //}
                    break;
                case KeyEvent.VK_Z:
                    intforces = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                    deinitialize();
                    break;
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent KE) {
        round = false;
        intforces = false;
    }
    
    @Override
    public void componentResized(ComponentEvent CE) {
        if(gravitymode){
            if(Gr != null){
                Gr.update();
            }
        }
        Block.setbounds(getWidth() - Block.dw, getHeight() - Block.dh);
    }
    
    
    public JSONObject jsons = new JSONObject();
    @Override
    public String toJSONString() {
        jsons.put("drawarrow", drawarrow);
        jsons.put("gravitymode", gravitymode);
        jsons.put("bounceingravity", bounceingravity);
        
        JSONArray JSONblocks = new JSONArray();
        for(int a = 0; a < blocks.length-nullc; ++a){
            JSONblocks.add(blocks[a]);
        }
        jsons.put("blocks", JSONblocks);
        
        jsons.put("RandomColorline", RandomColorline);
        jsons.put("allowacceleration", allowacceleration);
        jsons.put("drawacceleration", drawacceleration);
        jsons.put("drawLine", drawLine);
        jsons.put("drawnF", drawnF);
        //JSONS.put("drawnumbers", drawnumbers);
        jsons.put("drawmass", drawmass);
        jsons.put("drawlabels", drawlabels);
        jsons.put("drawParellelogram", drawParellelogram);
        
        jsons.put("Bedgehandlingmode", Bedgehandlingmode);
        jsons.put("Bcollisionhandlingmode", Bcollisionhandlingmode);
        jsons.put("Bahandlingmode", Bahandlingmode);
        jsons.put("cfblock", cfblock);
        return jsons.toString();
    }
    
    @Override
    public void componentMoved(ComponentEvent CE) {} @Override
    public void componentShown(ComponentEvent CE) {} @Override
    public void componentHidden(ComponentEvent CE) {} @Override
    public void keyTyped(KeyEvent KE) {} @Override
    public void mouseClicked(MouseEvent ME) {} @Override
    public void mouseMoved(MouseEvent ME) {} @Override
    public void mouseEntered(MouseEvent ME) {} @Override
    public void mouseExited(MouseEvent ME) {}
}
