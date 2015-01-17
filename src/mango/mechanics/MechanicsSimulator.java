package mango.mechanics;

import mango.mechanics.simulation.Block;
import java.awt.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

public class MechanicsSimulator extends JFrame {
    public static MechanicsSimulator MS;
    public static Simulation s;
    public static Controls c;
    public static ResourceBundle rb = ResourceBundle.getBundle("mango.mechanics.language.locale", new Locale("ko","KR"));
    public static Random rand = new Random();
    
    public static double screenw, screenh;
    
    public static void main(String[] args) {
        MS = new MechanicsSimulator();
        s.play();
    }
    public static void remain(String[] args) {
        MS.dispose();
        MS = null;
        
        main(null);
    }
    
    public MechanicsSimulator() {
        super(rb.getString("MS.title"));
        
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException E){
            Logger.getLogger(MechanicsSimulator.class.getName()).log(Level.SEVERE, null, E);
        }
        
        screenw = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
        screenh = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        s = new Simulation();
        Block.dx = getSize().width/2; Block.dy = getSize().height/2;
        add(s, BorderLayout.CENTER);

        c = new Controls();
        add(c, BorderLayout.EAST);
        c.changetheme(0);
        
        pack();
        setMinimumSize(new Dimension(getWidth(),getHeight()));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
