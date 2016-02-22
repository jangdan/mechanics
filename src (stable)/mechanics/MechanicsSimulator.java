package mechanics;

import mechanics.simulation.Block;
import java.awt.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

public class MechanicsSimulator extends JFrame {
    public static MechanicsSimulator mechanics;
    public static Simulation simulation;
    public static Controls controls;
    public static ResourceBundle rb = ResourceBundle.getBundle("mechanics.language.locale", Locale.getDefault());
    public static Random rand = new Random();
    
    public static void main(String[] args) {
        mechanics = new MechanicsSimulator();
        simulation.play();
    }
    public static void remain(String[] args) {
        mechanics.dispose();
        mechanics = null;
        
        main(new String[0]);
    }
    
    public MechanicsSimulator(){
        super(rb.getString("MS.title"));
        
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException E){
            Logger.getLogger(MechanicsSimulator.class.getName()).log(Level.SEVERE, null, E);
        }
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        simulation = new Simulation();
        add(simulation, BorderLayout.CENTER);
        
        
        controls = new Controls();
        add(controls, BorderLayout.EAST);
        controls.changetheme(0);
        
        pack();
        setMinimumSize(new Dimension(getWidth(),getHeight()));
        setSize(getWidth(),getHeight());
        setLocationRelativeTo(null);
        setVisible(true);
        
        setBlockdxy();
    }
    final void setBlockdxy(){
        Block.dx = (simulation.getSize().width-Block.dx)/2; Block.dy = (simulation.getSize().height-Block.dy)/2;
        controls.blockx = Block.dx; controls.blocky = Block.dy;
        controls.xJTF.setText(Double.toString(controls.blockx)); controls.yJTF.setText(Double.toString(controls.blocky));
    }
}