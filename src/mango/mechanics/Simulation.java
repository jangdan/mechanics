package mango.mechanics;

import mango.mechanics.simulation.environment.Ground;
import mango.mechanics.simulation.Block;
import mango.mechanics.simulation.Force;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import static mango.mechanics.MechanicsSimulator.c;
import static mango.mechanics.simulation.Block.*;
import org.json.simple.*;

public class Simulation extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ComponentListener, JSONAware {
    //Essentials 
    static Dimension size = new Dimension(500,600);
    static Loop l;
    static Thread lth;
    public static int sleept = 30;
    public static boolean isPaused = false;
        
    public static boolean drawarrow = true;
    
    public static boolean gravitymode = false;
    public static boolean bounceingravity = true;
    
    public static final int buffersize = 32;
    public static Block[] blocks = new Block[buffersize];
    private static int nullc = buffersize;
    public static int index = 0;
    
    public static final int mousextolerance = Block.dw, mouseytolerance = Block.dh;
    
    //For side-scrolling-like view
    public static Ground Gr;
    
    public Simulation() {
        setLayout(new FlowLayout());
        
        setPreferredSize(size);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addComponentListener(this);
        
        setFocusable(true);
        requestFocusInWindow();
        
        l = new Loop();
        lth = new Thread(l);
    } public static void refreshnullc() {
        nullc = 0;
        for(int a = 0; a < blocks.length; ++a){
            if(blocks[a] == null) ++nullc;
        }
    } public static int publicgetnullc() {
        return nullc;
    } public final void pause() {
        lth.interrupt();
        lth = null;
        isPaused = true;
    } public final void play() {
        lth = new Thread(l);
        lth.start();
        isPaused = false;
    }
    
    @Override
    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        Graphics2D g = (Graphics2D)G;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        if(gravitymode){
            if(Gr != null) Gr.draw(g);
        }
        refreshnullc();
        for(int b = 0; b < index; ++b){
            blocks[b].draw(g);
            
            double box = blocks[b].getx()+mousextolerance/2, boy = blocks[b].gety()+mouseytolerance/2;
            if(hascablock && blocks[b].isactive()){
                if(RandomColorline) g.setColor(cColor); //set the Force Color to the current Color
                
                Line2D.Double hbline = new Line2D.Double(box, boy, cx, cy);
                g.draw(hbline);
                
                if(drawarrow){
                    AffineTransform AT = new AffineTransform();
                    double theta = Math.atan2(hbline.y2-hbline.y1, hbline.x2-hbline.x1); //angle of slope
                    drawFarrowhead(g, theta, cx, cy);
                }
                
                if(RandomColorline) g.setColor(Block.defaultForceColor);
            }
        }
        g.dispose();
    }
    
    public void addBlock(double ix,double iy, int iw,int ih, Force ia, double im) {
        blocks[index] = new Block(ix,iy, iw,ih, ia, im);
        refreshnullc();
        if(isPaused){
            repaint();
        }
        ++index;
        --nullc;
    } public void removeBlock(int iindex) {
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
    
    //final double multiplier = ; //
    double fdistance;
    double fdistancex, fdistancey;
    
    double Fv;
    
    boolean isLeftMouseButton;
    @Override
    public void mousePressed(MouseEvent ME) {
        if(isLeftMouseButton){ //If the LeftMouseButton is already pressed and held
            cancel();
        }
        if(SwingUtilities.isLeftMouseButton(ME)){
            isLeftMouseButton = true;
            int r, g, b;
            requestFocusInWindow();
            for(int a = 0; a < index; ++a){
                if(blocks[a].getx() <= ME.getX()&&ME.getX() <= blocks[a].getx()+mousextolerance
                && blocks[a].gety() <= ME.getY()&&ME.getY() <= blocks[a].gety()+mouseytolerance){
                    sx = ME.getX(); sy = ME.getY();
                    cx = ME.getX(); cy = ME.getY();

                    cablock = a;
                    blocks[cablock].active();
                    do {
                       r = RC.nextInt(255);
                       g = RC.nextInt(255);
                       b = RC.nextInt(255);
                    } while((r == 0 && g == 0 && b == 0)
                            ||(r <= 64 && g == 0 && b == 0)
                            ||(g <= 64 && b == 0 && r == 0)
                            ||(b <= 64 && r == 0 && g == 0)
                            ||(r <= 64 && g <= 64 && b == 0)
                            ||(g <= 64 && b <= 64 && r == 0)
                            ||(b <= 64 && r <= 64 && g == 0)
                            ||(r <= 32 && b <=32 && g <= 32));
                    cColor = new Color(r, g, b);

                    hascablock = true;
                }
            }
            if(!hascablock){
                addBlock(ME.getX()-Block.dw/2,ME.getY()-Block.dh/2, Block.dw,Block.dh, new Force(c.nba, c.nbatheta), c.nbm);
            }
        } else if(SwingUtilities.isRightMouseButton(ME)){
            isLeftMouseButton = false;
            requestFocusInWindow();
            for(int a = 0; a < blocks.length-nullc; ++a){
                if(blocks[a].getx() <= ME.getX()&&ME.getX() <= blocks[a].getx()+mousextolerance
                && blocks[a].gety() <= ME.getY()&&ME.getY() <= blocks[a].gety()+mouseytolerance){
                    removeBlock(a);
                }
            }
        } else {
            isLeftMouseButton = false;
        }
    } @Override
    public void mouseReleased(MouseEvent ME) {
        if(SwingUtilities.isLeftMouseButton(ME)){
            if(hascablock){
                if(round){
                    switch(roundd){
                        case -180:
                            ex = ME.getX(); ey = blocks[cablock].gety()+mouseytolerance/2;
                            break;
                        case 180:
                            ex = ME.getX(); ey = blocks[cablock].gety()+mouseytolerance/2;
                            break;
                        case 0:
                            ex = ME.getX(); ey = blocks[cablock].gety()+mouseytolerance/2;
                            break;

                        case -90:
                            ex = blocks[cablock].getx()+mousextolerance/2; ey = ME.getY();
                            break;
                        case 90:
                            ex = blocks[cablock].getx()+mousextolerance/2; ey = ME.getY();
                            break;
                    }
                } else {
                    ex = ME.getX(); ey = ME.getY();
                }
                ddistance = Math.sqrt(Math.pow(ex-sx, 2) + Math.pow(ey-sy, 2));
                if(Math.abs(ex-(blocks[cablock].getx()+mousextolerance/2)) > mousextolerance/2
                || Math.abs(ey-(blocks[cablock].gety()+mouseytolerance/2)) > mouseytolerance/2){
                    fdistancex = ex-(blocks[cablock].getx()+mousextolerance/2);
                    fdistancey = ey-(blocks[cablock].gety()+mouseytolerance/2);
                    fdistance = Math.sqrt(Math.pow(fdistancex, 2) + Math.pow(fdistancey, 2));
                    blocks[cablock].addForce(new Force(fdistance/divisor,
                                             Math.atan2(ey-(blocks[cablock].gety()+mouseytolerance/2),ex-(blocks[cablock].getx()+mousextolerance/2))),
                                             cColor);
                    cancel();
                } else {
                    cfblock = cablock;
                    deinitialize();
                    blocks[cfblock].active();
                    hascfblock = true;
                    //System.out.println("CF"+cfblock);
                }
            } else {
                cancel();
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
            if(round){
                if(isPaused){
                    double thetadegrees = (Math.atan2(cy-(blocks[cablock].gety()+mouseytolerance/2), cx-(blocks[cablock].getx()+mousextolerance/2))*180d/Math.PI);
                    roundd = (int)Math.round(thetadegrees/90d)*90;
                    System.out.println(roundd);
                    switch(roundd){
                        case -180:
                            cx = ME.getX(); cy = blocks[cablock].gety()+mouseytolerance/2;
                            break;
                        case 180:
                            cx = ME.getX(); cy = blocks[cablock].gety()+mouseytolerance/2;
                            break;
                        case 0:
                            cx = ME.getX(); cy = blocks[cablock].gety()+mouseytolerance/2;
                            break;

                        case -90:
                            cx = blocks[cablock].getx()+mousextolerance/2; cy = ME.getY();
                            break;
                        case 90:
                            cx = blocks[cablock].getx()+mousextolerance/2; cy = ME.getY();
                            break;
                    }
                }
            } else {
                cx = ME.getX(); cy = ME.getY();
            }
        }
        if(isPaused) repaint();
    }
    @Override
    public void mouseClicked(MouseEvent ME) {} @Override
    public void mouseMoved(MouseEvent ME) {} @Override
    public void mouseEntered(MouseEvent ME) {} @Override
    public void mouseExited(MouseEvent ME) {}
    
    boolean round = false;
    int roundd; //round(ed) degrees
    @Override
    public void keyPressed(KeyEvent KE) {
        if(hascablock){
            if(KE.getKeyCode() == KeyEvent.VK_SHIFT){
                if(isPaused){
                    round = true;
                }
            } else if(KE.getKeyCode() == KeyEvent.VK_ESCAPE){
                cancel();
            }
        }
    } @Override
    public void keyReleased(KeyEvent KE) {
        round = false;
    }
    @Override
    public void keyTyped(KeyEvent KE) {}

    
    @Override
    public void componentResized(ComponentEvent CE) {
        if(gravitymode){
            if(Gr != null){
                Gr.update();
            }
        }
        Block.setbounds(getWidth() - Block.dw, getHeight() - Block.dh);
    }
    @Override
    public void componentMoved(ComponentEvent CE) {} @Override
    public void componentShown(ComponentEvent CE) {} @Override
    public void componentHidden(ComponentEvent CE) {}

    public JSONObject JSONS = new JSONObject();
    @Override
    public String toJSONString() {
        JSONS.put("drawarrow", drawarrow);
        JSONS.put("gravitymode", gravitymode);
        JSONS.put("bounceingravity", bounceingravity);
        
        JSONArray JSONblocks = new JSONArray();
        for(int a = 0; a < blocks.length-publicgetnullc(); ++a){
            JSONblocks.add(blocks[a]);
        }
        JSONS.put("blocks", JSONblocks);
        
        JSONS.put("RandomColorline", RandomColorline);
        JSONS.put("allowacceleration", allowacceleration);
        JSONS.put("drawacceleration", drawacceleration);
        JSONS.put("drawLine", drawLine);
        JSONS.put("drawnF", drawnF);
        //JSONS.put("drawnumbers", drawnumbers);
        JSONS.put("drawmass", drawmass);
        JSONS.put("drawlabels", drawlabels);
        JSONS.put("drawParellelogram", drawParellelogram);
        
        JSONS.put("Bedgehandlingmode", Bedgehandlingmode);
        JSONS.put("Bcollisionhandlingmode", Bcollisionhandlingmode);
        JSONS.put("Bahandlingmode", Bahandlingmode);
        
        return JSONS.toString();
    }
}
