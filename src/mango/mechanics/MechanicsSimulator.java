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
    public static ResourceBundle RB = ResourceBundle.getBundle("leocarbon.mechanics.language.locale", Locale.getDefault());
    
    public static double screenw, screenh;
    
    public MechanicsSimulator() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException E) {
            Logger.getLogger(MechanicsSimulator.class.getName()).log(Level.SEVERE, null, E);
        }
        
        setTitle(RB.getString("MS.title"));
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        s = new Simulation();
        Block.dx = getSize().width/2; Block.dy = getSize().height/2;
        add(s, BorderLayout.CENTER);

        c = new Controls();
        add(c, BorderLayout.EAST);
        screenw = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
        screenh = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
        
        pack();
        setMinimumSize(new Dimension(getWidth(),getHeight()));
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        MS = new MechanicsSimulator();
        s.play();
    } public static void remain(String[] args) {
        MS.dispose();
        MS = null;
        
        main(null);
    }
}
