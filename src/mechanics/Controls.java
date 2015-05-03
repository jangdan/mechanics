package mechanics;

import mechanics.simulation.Ground;
import mechanics.simulation.Block;
import mechanics.simulation.Vector;
import mechanics.utilities.PositiveRealNumberFormat;
import mechanics.utilities.RealNumberFormat;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import static mechanics.MechanicsSimulator.*;
import static mechanics.Simulation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Controls extends JPanel implements ActionListener, ChangeListener {
    JButton load, save; String loadtext = rb.getString("load.text"), savetext = rb.getString("save.text");
    
    JCheckBox dofriction; String dofrictiontext = rb.getString("friction.do.text");
    JLabel staticfclabel; JFormattedTextField staticfc; String staticfctext = rb.getString("friction.staticc.text");
    JLabel kineticfclabel; JFormattedTextField kineticfc; String kineticfctext = rb.getString("friction.kineticc.text");
    
    JLabel restitutionclabel; JSlider restitutionc; String restitutionctext = rb.getString("restitution.c.text");
    
    JCheckBox addgravity; String addgravitytext = rb.getString("addgravity.text");
    JCheckBox gravitybounce; String gravitybouncetext = rb.getString("gravitybounce.text");
    
    JButton pause; String pausetext = rb.getString("pause.text"); String play = rb.getString("pause.play");
    JButton defaultspeed; String defaultspeedtext = rb.getString("defaultspeed.text");
    JSlider sspeed;
    
    JLabel xyJL; String xyJLtext = rb.getString("xyJL.text");
    JFormattedTextField xJTF, yJTF;
    JLabel whlabel; String whlabeltext = rb.getString("wh.text");
    JFormattedTextField wJTF, hJTF;
    JLabel aJL; String aJLtext = rb.getString("aJL.text");
    JFormattedTextField aJTF;
    JLabel athetaJL; String athetaJLtext = rb.getString("athetaJL.text");
    JFormattedTextField athetaJTF;
    JLabel mJL; String mJLtext = rb.getString("mJL.text");
    JFormattedTextField mJTF;
    
    
    JButton addBlock; String addBlocktext = rb.getString("addBlock.text");
    //JToggleButton removeBlock; String removeBlockBlocktext = rb.getString("removeBlock.text"); //Click on Block to remove it
    JButton clear; String cleartext = rb.getString("clear.text");
    
    JCheckBox showF; String showFtext = rb.getString("showF.text"); //Show Vector(simulation)
    JCheckBox RCl; String RCltext = rb.getString("RCl.text"); //Random Color line(Forces)
    JCheckBox shownF; String shownFtext = rb.getString("shownF.text"); //show net force
    JCheckBox showarrows; String showarrowstext = rb.getString("showarrows.text"); //Show arrows
    JCheckBox showparellelogram; String showparellelogramtext = rb.getString("showparellelogram.text"); //Show parellelogram
    JCheckBox showmass; String showmasstext = rb.getString("showmass.text"); //Show Block mass
    JCheckBox showlabels; String showlabelstext = rb.getString("showlabels.text"); //Show Block mass
    JCheckBox previewForce; String previewForcetext = rb.getString("show.Forcepreview");
    /*JCheckBox shownumbers; String shownumberstext = rb.getString("shownumbers.text"); //Show Vector(simulation) in numbers
    //JLabel uptodigitsJL; String uptodigitsJLtext = rb.getString("uptodigits.text");
    JSlider uptodigits; //Show Vector(simulation) in numbers up to specified digits
    int lastuptodigitsvalue = 16;
    */
    JCheckBox a; String atext = rb.getString("a.text"); //allow acceleration
    JCheckBox drawa; String drawatext = rb.getString("drawa.text");
    
    JCheckBox drawv; String drawvtext = rb.getString("v.draw.text");
    
    JLabel onedgea; String onedgeatext = rb.getString("ahm.text");
    JComboBox ahm; String[] ahmitems = { //Block acceleration handling mode items
        rb.getString("ahm.keep"), rb.getString("ahm.reverse"), rb.getString("ahm.reset")
    };
    
    JLabel onedge; String onedgetext = rb.getString("ehm.text");
    JComboBox edgehandlingmode; String[] ehmitems = { //edgehandlingmode items
        rb.getString("ehm.unlimited"), rb.getString("ehm.bounce")/*, rb.getString("ehm.wrap"),*/
    };
    
    JLabel onBlock; String onBlocktext = rb.getString("chm.text");
    JComboBox collisionhandlingmode; String[] chmitems = { //collisionhandlingmode items
        rb.getString("chm.ignore"), rb.getString("chm.bounce")
    };
    
    JTabbedPane concepts; 
    JPanel fo; JPanel nefo; JPanel ac; JPanel gr;
    
    JLabel themel; String themelt = rb.getString("themes");
    JComboBox themes; String[] themenames = {
         rb.getString("themes.default"), rb.getString("themes.dark"), rb.getString("themes.brighter")
    }; String[] themefilenames = {
         "default.json", "dark.json", "brighter.json"
    };
    
    JLabel playforl; String playfort = rb.getString("playfor.t");
    JFormattedTextField playfor; //Simulate for a specific amount of seconds (very useful!)
    JLabel moveforl; String movefort = rb.getString("moveuntil.t");
    JFormattedTextField movefor; //Simulate until the block moves that much meters
    
    
    public Controls() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        
        c.ipady = 2;
        
        JPanel sp = new JPanel(new GridBagLayout());
        String sptext = rb.getString("sp.text");{
            pause = newJButton(pausetext, "p");
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            sp.add(pause,c);
            
            defaultspeed = newJButton(defaultspeedtext,"dspeed");
            c.gridx = 1; c.gridy = 0;
            sp.add(defaultspeed,c);

            sspeed = new JSlider(JSlider.HORIZONTAL, 1, 1000, lsleept);
            sspeed.setMajorTickSpacing(100);
            sspeed.setMinorTickSpacing(10);
            sspeed.setPaintTicks(true);
            sspeed.setSnapToTicks(true);
            sspeed.addChangeListener((ChangeEvent e) -> {
                lsleept = sspeed.getValue();
            });
            c.gridx = 0; c.gridy = 1;
            c.gridwidth = 2;
            sp.add(sspeed,c);
        }
        sp.setBorder(BorderFactory.createTitledBorder(sptext));
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 2;
        add(sp,c);
        
        
        JPanel state = new JPanel(new GridBagLayout());
        String statetext = rb.getString("state.text"); {
            save = newJButton(savetext,"save");
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            state.add(save);
            load = newJButton(loadtext,"load");
            c.gridx = 1; c.gridy = 0;
            state.add(load);
        }
        state.setBorder(BorderFactory.createTitledBorder(statetext));
        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 2;
        add(state,c);
        
        
        JPanel Blockp = new JPanel(new GridBagLayout()); //Block panel
        String Blockptext = rb.getString("Blockp.Border.text");{
            xyJL = new JLabel(xyJLtext); xyJL.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Blockp.add(xyJL,c);

            JPanel coordinates = new JPanel(new GridLayout(1,2));{
                xJTF = newJFormattedTextField("xJTF", blockx, Double.MIN_VALUE, Double.MAX_VALUE, new RealNumberFormat(), 6);
                yJTF = newJFormattedTextField("yJTF", blocky, Double.MIN_VALUE, Double.MAX_VALUE, new RealNumberFormat(), 6);
                coordinates.add(xJTF);
                coordinates.add(yJTF);
            }
            c.gridx = 1; c.gridy = 0;
            c.ipadx = 80;
            Blockp.add(coordinates,c);
            
            whlabel = new JLabel(whlabeltext); whlabel.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 1;
            c.ipadx = 0;
            Blockp.add(whlabel,c);
            
            JPanel dimensions = new JPanel(new GridLayout(1,2));{
                wJTF = newJFormattedTextField("wJTF", Block.dw, 0, Double.MAX_VALUE, new RealNumberFormat(), 6);
                hJTF = newJFormattedTextField("hJTF", Block.dh, 0, Double.MAX_VALUE, new RealNumberFormat(), 6);
                dimensions.add(wJTF);
                dimensions.add(hJTF);
            }
            c.gridx = 1; c.gridy = 1;
            Blockp.add(dimensions,c);

            JPanel initialap = new JPanel(new GridBagLayout()); //acceleration panel
            String accelerationptext = rb.getString("accelerationp.Border.text");{
                aJL = new JLabel(aJLtext); aJL.setHorizontalAlignment(SwingConstants.RIGHT);
                c.gridx = 0; c.gridy = 0;
                initialap.add(aJL,c);
                aJTF = newJFormattedTextField("aJTF", initialv, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 4);
                c.gridx = 1; c.gridy = 0;
                initialap.add(aJTF,c);
                
                athetaJL = new JLabel(athetaJLtext); athetaJL.setHorizontalAlignment(SwingConstants.RIGHT);
                c.gridx = 0; c.gridy = 1;
                initialap.add(athetaJL,c);
                athetaJTF = newJFormattedTextField("athetaJTF", initialvtheta, Double.MIN_VALUE, Double.MAX_VALUE, new RealNumberFormat(), 4);
                c.gridx = 1; c.gridy = 1;
                initialap.add(athetaJTF,c);
            }
            initialap.setBorder(BorderFactory.createTitledBorder(accelerationptext));
            c.gridx = 0; c.gridy = 2;
            c.ipady = 0;
            c.gridwidth = 2;
            Blockp.add(initialap,c);
            c.ipady = -1;
            
            mJL = new JLabel(mJLtext); mJL.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 3;
            c.gridwidth = 1;
            Blockp.add(mJL,c);
            mJTF = newJFormattedTextField("mJTF", blockm, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 5);
            c.gridx = 1; c.gridy = 3;
            Blockp.add(mJTF,c);
            
            moveforl = new JLabel(movefort); moveforl.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 4;
            Blockp.add(moveforl,c);
            movefor = newJFormattedTextField("movefor", 0d, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 3);
            c.gridx = 1; c.gridy = 4;
            Blockp.add(movefor,c);
            
            playforl = new JLabel(playfort); playforl.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 5;
            c.gridwidth = 1;
            Blockp.add(playforl,c);
            playfor = newJFormattedTextField("playfor", 0d, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 3);
            c.gridx = 1; c.gridy = 5;
            Blockp.add(playfor,c);
            
            addBlock = newJButton(addBlocktext, "New Block");
            c.gridx = 0; c.gridy = 6;
            c.gridwidth = 2;
            Blockp.add(addBlock,c);
            
            clear = newJButton(cleartext, "clear");
            c.gridx = 0; c.gridy = 7;
            c.gridwidth = 2;
            Blockp.add(clear,c);
        }
        Blockp.setBorder(BorderFactory.createTitledBorder(Blockptext));
        c.gridx = 0; c.gridy = 2;
        add(Blockp,c);
        
        
        JPanel Fp = new JPanel(new GridBagLayout());
        String Fptext = rb.getString("Fp.text");{

            showF = newJCheckBox(showFtext, "Show force(s)", true);
            RCl = newJCheckBox(RCltext, "Color force(s)", false);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Fp.add(showF,c);
            c.gridx = 1; c.gridy = 0;
            Fp.add(RCl,c);

            shownF = newJCheckBox(shownFtext, "Show net force", false);
            c.gridx = 0; c.gridy = 1;
            Fp.add(shownF,c);

            showarrows = newJCheckBox(showarrowstext, "Show arrows", true);
            c.gridx = 1; c.gridy = 1;
            Fp.add(showarrows,c);

            showparellelogram = newJCheckBox(showparellelogramtext, "Show parellelogram", true);
            c.gridx = 0; c.gridy = 2;
            c.gridwidth = 2;
            Fp.add(showparellelogram,c);
            
            showlabels = newJCheckBox(showlabelstext, "Show labels", true);
            c.gridx = 0; c.gridy = 3;
            c.gridwidth = 1;
            Fp.add(showlabels,c);
            
            showmass = newJCheckBox(showmasstext, "Show mass", true);
            c.gridx = 1; c.gridy = 3;
            Fp.add(showmass,c);
            
            previewForce = newJCheckBox(previewForcetext, "previewForce", true);
            c.gridx = 0; c.gridy = 4;
            c.gridwidth = 2;
            Fp.add(previewForce,c);
            
            /*
            uptodigits = new JSlider(JSlider.HORIZONTAL, 0, 16, 0);
            uptodigits.setMinorTickSpacing(1);
            uptodigits.setPaintTicks(true);
            uptodigits.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent CE) {
                    //if(!uptodigits.getValueIsAdjusting()){
                        if(uptodigits.getValue() == 0){
                            shownumbers.setSelected(false);
                            drawnumbers = false;
                        } else {
                            shownumbers.setSelected(true);
                            drawnumbers = true;
                            digits = uptodigits.getValue();
                            lastuptodigitsvalue = uptodigits.getValue();
                        }
                    //}
                    simulation.repaint();
                }
            });
            controls.gridx = 0; controls.gridy = 4;
            Fp.add(uptodigits,controls);
            */
        }
        Fp.setBorder(BorderFactory.createTitledBorder(Fptext));
        c.gridx = 0; c.gridy = 3;
        c.gridwidth = 2;
        add(Fp,c);
        
        JPanel ap = new JPanel(new GridBagLayout());
        String aptext = rb.getString("ap.text");{
            a = newJCheckBox(atext, "a", true);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            ap.add(a,c);
            drawa = newJCheckBox(drawatext, "drawa", true);
            c.gridx = 1; c.gridy = 0;
            ap.add(drawa,c);

            onedgea = new JLabel(onedgeatext);
            onedgea.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 1;
            ap.add(onedgea,c);
            ahm = newJComboBox(ahmitems, "ahm");
            c.gridx = 1; c.gridy = 1;
            ap.add(ahm,c);
        }
        ap.setBorder(BorderFactory.createTitledBorder(aptext));
        c.gridx = 0; c.gridy = 4;
        c.gridwidth = 2;
        add(ap,c);
        
        JPanel vp = new JPanel(new GridBagLayout());
        String vptext = rb.getString("v"); {
            drawv = newJCheckBox(drawvtext, "drawv", true);
            c.gridx = 0; c.gridy = 0;
            vp.add(drawv, c);
        }
        vp.setBorder(BorderFactory.createTitledBorder(vptext));
        c.gridwidth = 2;
        c.gridx = 0; c.gridy = 5;
        add(vp,c);
        
        JPanel frictionp = new JPanel(new GridBagLayout());
        String frictionptext = rb.getString("friction.text"); {
            dofriction = newJCheckBox(dofrictiontext, "friction", true);
            dofriction.setAlignmentY(JComponent.CENTER_ALIGNMENT);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 2;
            frictionp.add(dofriction,c);
            
            c.gridwidth = 1;
            
            staticfclabel = new JLabel(staticfctext);
            staticfclabel.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 1;
            frictionp.add(staticfclabel,c);
            staticfc = newJFormattedTextField("staticfc", simulation.cosf, 0d, 1d, new PositiveRealNumberFormat(), 6);
            c.gridx = 1; c.gridy = 1;
            c.ipadx = 40;
            frictionp.add(staticfc,c);
            
            kineticfclabel = new JLabel(kineticfctext);
            kineticfclabel.setHorizontalAlignment(SwingConstants.RIGHT);
            c.ipadx = 0;
            c.gridx = 0; c.gridy = 2;
            frictionp.add(kineticfclabel,c);
            kineticfc = newJFormattedTextField("kineticfc", simulation.cokf, 0d, 1d, new PositiveRealNumberFormat(), 6);
            c.gridx = 1; c.gridy = 2;
            c.ipadx = 40;
            frictionp.add(kineticfc,c);
        }
        c.gridx = 0; c.gridy = 6;
        c.gridwidth = 2;
        c.ipadx = 0;
        frictionp.setBorder(BorderFactory.createTitledBorder(frictionptext));
        add(frictionp,c);
        
        
        JPanel restitutionp = new JPanel(new GridBagLayout());
        String restitutionptext = rb.getString("restitution.text"); {
            restitutionclabel = new JLabel(restitutionctext);
            restitutionclabel.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 0;
            restitutionp.add(restitutionclabel,c);
            restitutionc = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
            restitutionc.setMinorTickSpacing(10);
            restitutionc.setPaintTicks(true);
            restitutionc.addChangeListener((ChangeEvent e) -> {
                simulation.cor = (double)restitutionc.getValue()/100;
            });
            c.gridx = 0; c.gridy = 1;
            restitutionp.add(restitutionc,c);
        }
        c.gridx = 0; c.gridy = 7;
        c.ipadx = 0;
        restitutionp.setBorder(BorderFactory.createTitledBorder(restitutionptext));
        add(restitutionp,c);
        
        JPanel Grp = new JPanel();
        String Grptext = rb.getString("Grp.text"); {
            addgravity = newJCheckBox(addgravitytext, "Gravity", false);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Grp.add(addgravity);
            gravitybounce = newJCheckBox(gravitybouncetext, "Bounce in Gravity", false);
            Grp.add(gravitybounce);
        }
        c.gridx = 0; c.gridy = 8;
        c.gridwidth = 2;
        Grp.setBorder(BorderFactory.createTitledBorder(Grptext));
        add(Grp,c);
        
        onedge = new JLabel(onedgetext);
        c.gridx = 0; c.gridy = 9;
        c.gridwidth = 1;
        onedge.setHorizontalAlignment(SwingConstants.RIGHT);
        add(onedge,c);
        edgehandlingmode = newJComboBox(ehmitems, "ehm");
        edgehandlingmode.setSelectedIndex(1);
        c.gridx = 1; c.gridy = 9;
        add(edgehandlingmode,c);
        
        
        c.gridx = 0; c.gridy = 10;
        onBlock = new JLabel(onBlocktext);
        onBlock.setHorizontalAlignment(SwingConstants.RIGHT);
        add(onBlock,c);
        collisionhandlingmode = newJComboBox(chmitems, "chm");
        collisionhandlingmode.setSelectedIndex(1);
        c.gridx = 1; c.gridy = 10;
        add(collisionhandlingmode,c);
        
        //<editor-fold defaultstate="collapsed" desc="concepts">
        /*
        concepts = new JTabbedPane();
        
        fo = new JPanel();
        fo.add(new JLabel(rb.getString("concepts.fo.text")));
        concepts.addTab(rb.getString("concepts.fo.title"),fo);
        
        nefo = new JPanel();
        nefo.add(new JLabel(rb.getString("concepts.nefo.text")));
        concepts.addTab(rb.getString("concepts.nefo.title"),nefo);
        
        ac = new JPanel();
        ac.add(new JLabel(rb.getString("concepts.ac.text")));
        concepts.addTab(rb.getString("concepts.ac.title"),ac);
        
        gr = new JPanel();
        gr.add(new JLabel(rb.getString("concepts.gr.text")));
        concepts.addTab(rb.getString("concepts.gr.title"),gr);
        
        controls.gridx = 0; controls.gridy = 8;
        controls.gridwidth = 2;
        
        concepts.setBorder(BorderFactory.createTitledBorder(rb.getString("concepts.title")));
        add(concepts,controls);*/
        //</editor-fold>
        
        themel = new JLabel(themelt);
        themel.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0; c.gridy = 11;
        add(themel,c);
        themes = newJComboBox(themenames, "theme");
        c.gridx = 1; c.gridy = 11;
        add(themes,c);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK), new EmptyBorder(0, 10, 0, 10)));
    }
    
    private JButton newJButton(String text, String ActionCommand) {
        JButton jb = new JButton(text);
        jb.setActionCommand(ActionCommand);
        jb.addActionListener(this);
        return jb;
    }
    private JTextField newJTextField(String name, String initial, int length) {
        JTextField jtf = new JTextField(initial, length);
        jtf.setActionCommand(name);
        jtf.addActionListener(this);
        return jtf;
    }
    private JFormattedTextField newJFormattedTextField(String name, double initial, double Minimum, double Maximum, Object Format, int length){
        JFormattedTextField jftf = new JFormattedTextField(Format);
        jftf.setActionCommand(name);
        jftf.addActionListener(this);
        jftf.setValue(initial);
        //jftf.setColumns(length);
        jftf.addMouseListener(new MouseAdapter(){ @Override public void mouseClicked(MouseEvent e) {
            clearjtf(e);
        }});
        return jftf;
    }
    private JCheckBox newJCheckBox(String text, String ActionCommand, boolean selected) {
        JCheckBox jcb = new JCheckBox(text, selected);
        jcb.setActionCommand(ActionCommand);
        jcb.addActionListener(this);
        return jcb;
    }
    private JComboBox newJComboBox(String[] items, String ActionCommand) {
        JComboBox jcb = new JComboBox(items);
        jcb.setActionCommand(ActionCommand);
        jcb.addActionListener(this);
        return jcb;
    }
    private void clearjtf(MouseEvent e){
        ((JTextField)e.getSource()).setText("");
    }
    
    public double blockx = Block.dx; public double blocky = Block.dy;
    int blockw = Block.dw, blockh = Block.dh;
    public double blockm = Block.dm;
    
    public double initialv = 0d;
    public double initialvtheta = 0d;
    public double moveford = 0d;
    public double playford = 0d;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "Gravity":
                gravitymode = addgravity.isSelected();
                simulation.calculaten();
                
                if(addgravity.isSelected()){
                    Bedgehandlingmode = -1;
                    edgehandlingmode.setEnabled(false);
                    simulation.Gr = new Ground();
                    simulation.Gr.update();
                    gravitybounce.setEnabled(true);
                } else {
                    simulation.bulldozegravity();
                    Bedgehandlingmode = edgehandlingmode.getSelectedIndex();
                    edgehandlingmode.setEnabled(true);
                    gravitybounce.setEnabled(false);
                }
                break;
            case "Bounce in Gravity":
                bounceingravity = gravitybounce.isSelected();
                break;
                
            case "friction":
                addfriction = dofriction.isSelected();
                simulation.calculaten();
                break;
            case "staticfc":
                if(Double.parseDouble(staticfc.getText()) >= 0d){
                    simulation.cosf = Double.parseDouble(staticfc.getText());
                    simulation.calculaten();
                }
                break;
            case "kineticfc":
                if(Double.parseDouble(kineticfc.getText()) >= 0d){
                    simulation.cokf = Double.parseDouble(kineticfc.getText());
                    simulation.calculaten();
                }
                break;
                
            case "save":
                save();
                break;
            case "load":
                load();
                break;
            case "dspeed":
                sspeed.setValue(30);
                lsleept = 30;
                
                break;
            case "xJTF":
                if(Double.parseDouble(xJTF.getText()) >= 0d){
                    blockx = Double.parseDouble(xJTF.getText());
                }
                break;
            case "wJTF":
                blockw = Integer.parseInt(wJTF.getText());
                break;
            case "hJTF":
                blockh = Integer.parseInt(hJTF.getText());
                break;
            case "yJTF":
                if(Double.parseDouble(yJTF.getText()) >= 0d){
                    blocky = Double.parseDouble(yJTF.getText());
                }
                break;
            case "aJTF":
                initialv = Double.parseDouble(aJTF.getText());
                break;
            case "athetaJTF":
                initialvtheta = Double.parseDouble(athetaJTF.getText());
                initialvtheta = initialvtheta*Math.PI/180;
                break;
            case "mJTF":
                if(Double.parseDouble(mJTF.getText()) > 0d){
                    blockm = Double.parseDouble(mJTF.getText());
                }
                break;
            case "New Block":
                simulation.addBlock(blockx,blocky, blockw,blockh, new Vector(initialv, initialvtheta), blockm, moveford, playford);
                break;
            case "clear":
                simulation.clear();
                break;
                
            case "p":
                if(simulation.isPaused){
                    simulation.isPaused = false;
                    simulation.play();
                    pause.setText(pausetext);
                } else {
                    simulation.isPaused = true;
                    simulation.pause();
                    pause.setText(play);
                }
                break;
                
            case "a":
                if(a.isSelected()){
                    if(showF.isSelected()){
                        drawa.setEnabled(true);
                    }
                    ahm.setEnabled(true);
                    allowacceleration = true;
                } else {
                    drawa.setEnabled(false);
                    ahm.setEnabled(false);
                    allowacceleration = false;
                }
                break;
            case "drawa":
                drawacceleration = drawa.isSelected();
                break;
            case "ahm":
                Bahandlingmode = ahm.getSelectedIndex();
                break;
                
            case "Show force(s)":
                if(showF.isSelected()){
                    drawLine = true;
                    RCl.setEnabled(true);
                    shownF.setEnabled(true);
                    showarrows.setEnabled(true);
                    showlabels.setEnabled(true);
                    if(a.isSelected()){
                        drawa.setEnabled(true);
                    }
                    
                    RandomColorline = RCl.isSelected();
                    drawnF = shownF.isSelected();
                    drawarrow = showarrows.isSelected();
                    //drawnumbers = showlabels.isSelected();
                    drawacceleration = drawa.isSelected();
                } else {
                    drawLine = false;
                    RCl.setEnabled(false);
                    shownF.setEnabled(false);
                    showarrows.setEnabled(false);
                    showlabels.setEnabled(false);
                    //uptodigits.setEnabled(false);
                    drawa.setEnabled(false);
                    
                    RandomColorline = RCl.isSelected();
                    drawnF = false;
                    drawarrow = false;
                    //drawnumbers = false;
                    drawacceleration = false;
                }
                break;
            case "Color force(s)":
                RandomColorline = RCl.isSelected();
                break;
            case "Show net force":
                drawnF = shownF.isSelected();
                break;
            case "Show arrows":
                drawarrow = showarrows.isSelected();
                break;
            case "Show parellelogram":
                drawParellelogram = showparellelogram.isSelected();
                break;
            case "Show labels":
                drawlabels = showlabels.isSelected();
                break;
            case "Show mass":
                drawmass = showmass.isSelected();
                break;
            case "previewForce":
                previewForcemagnitude = previewForce.isSelected();
                break;
                
            case "drawv":
                drawvelocity = drawv.isSelected();
                break;
                
            case "ehm":
                Bedgehandlingmode = edgehandlingmode.getSelectedIndex();
                switch(edgehandlingmode.getSelectedIndex()){
                    case 0:
                        ahm.setEnabled(false);
                        break;
                case 1:
                    ahm.setEnabled(true);
                    break;
                }
                break;
                
            case "chm":
                Bcollisionhandlingmode = collisionhandlingmode.getSelectedIndex();
                break;
                
            case "theme":
                currenttheme = themes.getSelectedIndex();
                changetheme(currenttheme);
                break;
                
            case "playfor":
                if(Double.parseDouble(playfor.getText()) >= 0d){
                    playford = Double.parseDouble(playfor.getText());
                    /*actionPerformed(new ActionEvent(this, -1, "dspeed")); //Comment only for absolute timing
                    sspeed.setEnabled(false);
                } else {
                    sspeed.setEnabled(true);*/
                }
                break;
                
            case "movefor":
                if(Double.parseDouble(movefor.getText()) >= 0d){
                    moveford = Double.parseDouble(movefor.getText());
                }
                break;
                
        }
        simulation.repaint();
    }
    
    public void changetheme(int i){
        try {
            InputStream is = getClass().getResource("simulation/themes/"+themefilenames[i]).openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            JSONObject jo = new JSONObject(new JSONTokener(br));
            
            simulation.backc = new Color(Integer.parseInt(jo.getString("background")));
            simulation.blockc = new Color(Integer.parseInt(jo.getString("blockc")));
            simulation.linec = new Color(Integer.parseInt(jo.getString("linec")));
            simulation.accc = new Color(Integer.parseInt(jo.getString("accc")));
            simulation.iaccc = new Color(Integer.parseInt(jo.getString("iaccc")));
            simulation.textc = new Color(Integer.parseInt(jo.getString("textc")));
            simulation.gc = new Color(Integer.parseInt(jo.getString("gc")));
            simulation.fc = new Color(Integer.parseInt(jo.getString("fc")));
            simulation.nc = new Color(Integer.parseInt(jo.getString("nc")));
            simulation.vc = new Color(Integer.parseInt(jo.getString("vc")));

            simulation.setBackground(simulation.backc);
            simulation.repaint();
        } catch(IOException e){
            Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    
    public void save(){
        boolean pisPaused = simulation.isPaused;
        if(!pisPaused) actionPerformed(new ActionEvent(this, -1, "p"));
        
        FileDialog FD = new FileDialog(new JFrame(), "Save to file", FileDialog.SAVE);
        FD.setFile("*.json");
        FD.setVisible(true);
        
        File F = new File(FD.getDirectory()+"/"+FD.getFile());
        
        try (FileWriter FW = new FileWriter(F.getAbsolutePath())) {
            FW.write(simulation.toJSONString());
            FW.flush();
        } catch (IOException IOE) {
            Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, IOE);
        }
        
        if(!pisPaused) actionPerformed(new ActionEvent(this, -1, "p"));
    }
    public void load(){
        boolean pisPaused = simulation.isPaused;
        if(!pisPaused) actionPerformed(new ActionEvent(this, -1, "p"));
        
        FileDialog FD = new FileDialog(new JFrame(), "Open file", FileDialog.LOAD);
        FD.setFile("*.json");
        FD.setVisible(true);
        
        File F = new File(FD.getDirectory()+"/"+FD.getFile());
        
        if(FD.getFile() != null){
            try {
                simulation.clear();
                JSONObject loads = new JSONObject(new JSONTokener(new FileReader(F)));
                
                simulation.isPaused = loads.getBoolean("isPaused");
                
                showarrows.setSelected(loads.getBoolean("drawarrow"));
                showF.setSelected(loads.getBoolean("drawLine"));
                shownF.setSelected(loads.getBoolean("drawnF"));
                
                //digits = loads.getInt("digits");
                
                showparellelogram.setSelected(loads.getBoolean("drawParellelogram"));
                RCl.setSelected(loads.getBoolean("RandomColorline"));
                previewForce.setSelected(loads.getBoolean("previewForcemagnitude"));
                a.setSelected(loads.getBoolean("allowacceleration"));
                drawa.setSelected(loads.getBoolean("drawacceleration"));
                drawv.setSelected(loads.getBoolean("drawvelocity"));
                
                edgehandlingmode.setSelectedIndex(loads.getInt("Bedgehandlingmode"));
                collisionhandlingmode.setSelectedIndex(loads.getInt("Bcollisionhandlingmode"));
                ahm.setSelectedIndex(loads.getInt("Bahandlingmode"));
                
                addgravity.setSelected(loads.getBoolean("gravitymode"));
                gravitybounce.setSelected(loads.getBoolean("bounceingravity"));
                
                dofriction.setSelected(loads.getBoolean("addfriction"));
                
                JSONArray jsonblocks = loads.getJSONArray("blocks");
                for(int i = 0; i < jsonblocks.length(); ++i){
                    JSONObject iBlock = jsonblocks.getJSONObject(i);
                    simulation.addBlock(iBlock.getDouble("x"), iBlock.getDouble("y"), iBlock.getInt("w"), iBlock.getInt("h"), new Vector(0,0), iBlock.getDouble("mass"), iBlock.getDouble("moveford"), iBlock.getDouble("playford"));
                    
                    int workingi = simulation.bindex-1;
                    simulation.blocks[workingi].load(new Vector(iBlock.getJSONObject("n")), new Vector(iBlock.getJSONObject("a")), new Vector(iBlock.getJSONObject("v")), iBlock.getLong("starttime"), iBlock.getLong("elapsedt"), iBlock.getDouble("moved"),
                                            iBlock.getDouble("work"), iBlock.getDouble("totalwork"), iBlock.getDouble("power"), iBlock.getDouble("totalpower"), iBlock.getBoolean("keycontrolledForces"),
                                            iBlock.getDouble("px"), iBlock.getDouble("py"), iBlock.getDouble("pv"), iBlock.getDouble("ppv"), iBlock.getBoolean("iskineticf"));
                                              
                                              
                    JSONArray jsonforces = iBlock.getJSONArray("forces");
                    JSONArray jsonforcesColor = iBlock.getJSONArray("forcesColor");
                    for(int j = 0; j < jsonforces.length(); ++j) simulation.blocks[workingi].addForce(new Vector(jsonforces.getJSONObject(j)), new Color(jsonforcesColor.getInt(j)));
                }
                
                JSONArray jsonmovingclub = loads.getJSONArray("movingclub");
                for(int i = 0; i < jsonmovingclub.length(); ++i){
                    simulation.movingclub.add((Block)jsonmovingclub.get(i));
                }
                
                if(simulation.hascablock = loads.getBoolean("hascablock")){
                    simulation.cablock = loads.getInt("cablock");
                } else simulation.cablock = -1;
                if(simulation.hascfblock = loads.getBoolean("hascfblock")){
                    simulation.cfblock = loads.getInt("cfblock");
                } else simulation.cfblock = -1;
                
                usecamera = loads.getBoolean("usecamera");
                
                SwingUtilities.invokeLater(() -> {
                    refreshControls();
                    simulation.repaint();
                });
            } catch(FileNotFoundException e){
                Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, e);
            }
            if(!pisPaused) actionPerformed(new ActionEvent(this, -1, "p"));
        }
    }
    public void refreshControls(){
        ArrayList<Component> components = new ArrayList<>(Arrays.asList(getComponents()));
        for(int i = 0; i < components.size(); ++i){
            System.out.println(components.get(i));
            if(components.get(i) instanceof JCheckBox) actionPerformed(new ActionEvent(this,-1,((JCheckBox)components.get(i)).getActionCommand()));
            else if(components.get(i) instanceof JComboBox) actionPerformed(new ActionEvent(this,-1,((JComboBox)components.get(i)).getActionCommand()));
            else if(components.get(i) instanceof JPanel) components.addAll(getComponentsof((JPanel)components.get(i)));
        }
        repaint();
        revalidate();
    }
    
    public static ArrayList<Component> getComponentsof(JPanel tp){
        ArrayList<Component> components = new ArrayList<>(Arrays.asList(tp.getComponents()));
        for(int i = 0; i < components.size(); ++i){
            if(components.get(i) instanceof JPanel) components.addAll(getComponentsof((JPanel)components.get(i)));
        }
        return components;
    }

    @Override
    public void stateChanged(ChangeEvent e){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}