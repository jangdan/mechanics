package mango.mechanics;

import mango.mechanics.simulation.environment.Ground;
import mango.mechanics.simulation.Block;
import mango.mechanics.simulation.Force;
import mango.mechanics.controls.PositiveRealNumberFormat;
import mango.mechanics.controls.RealNumberFormat;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import static mango.mechanics.MechanicsSimulator.*;
import static mango.mechanics.Simulation.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Controls extends JPanel implements ActionListener {
    JButton load, save; String loadtext = rb.getString("load.text"), savetext = rb.getString("save.text");
    
    JCheckBox addgravity; String addgravitytext = rb.getString("addgravity.text");
    JCheckBox gravitybounce; String gravitybouncetext = rb.getString("gravitybounce.text");
    
    JButton pause; String pausetext = rb.getString("pause.text"); String play = rb.getString("pause.play");
    JButton defaultspeed; String defaultspeedtext = rb.getString("defaultspeed.text");
    JSlider sspeed;
    
    JLabel xyJL; String xyJLtext = rb.getString("xyJL.text");
    JFormattedTextField xJTF, yJTF;
    //JTextField wJTF, hJTF;
    JLabel aJL; String aJLtext = rb.getString("aJL.text");
    JFormattedTextField aJTF;
    JLabel athetaJL; String athetaJLtext = rb.getString("athetaJL.text");
    JFormattedTextField athetaJTF;
    JLabel mJL; String mJLtext = rb.getString("mJL.text");
    JFormattedTextField mJTF;
    
    JButton addBlock; String addBlocktext = rb.getString("addBlock.text");
    //JToggleButton removeBlock; String removeBlockBlocktext = rb.getString("removeBlock.text"); //Click on Block to remove it
    JButton clear; String cleartext = rb.getString("clear.text");
    
    JCheckBox showF; String showFtext = rb.getString("showF.text"); //Show Force(s)
    JCheckBox RCl; String RCltext = rb.getString("RCl.text"); //Random Color line(Forces)
    JCheckBox shownF; String shownFtext = rb.getString("shownF.text"); //show net force
    JCheckBox showarrows; String showarrowstext = rb.getString("showarrows.text"); //Show arrows
    JCheckBox showparellelogram; String showparellelogramtext = rb.getString("showparellelogram.text"); //Show parellelogram
    JCheckBox showmass; String showmasstext = rb.getString("showmass.text"); //Show Block mass
    JCheckBox showlabels; String showlabelstext = rb.getString("showlabels.text"); //Show Block mass
    /*JCheckBox shownumbers; String shownumberstext = rb.getString("shownumbers.text"); //Show Force(s) in numbers
    //JLabel uptodigitsJL; String uptodigitsJLtext = rb.getString("uptodigits.text");
    JSlider uptodigits; //Show Force(s) in numbers up to specified digits
    int lastuptodigitsvalue = 16;
    */
    JCheckBox a; String atext = rb.getString("a.text"); //allow acceleration
    JCheckBox drawa; String drawatext = rb.getString("drawa.text");
    
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
        rb.getString("chm.ignore")/*, rb.getString("chm.bounce")*/
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
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = -1;
        
        
        JPanel sp = new JPanel(new GridBagLayout());
        String sptext = rb.getString("sp.text");{
            pause = newJButton(pause,pausetext, "p");
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            sp.add(pause,c);
            
            defaultspeed = newJButton(defaultspeed,defaultspeedtext,"dspeed");
            c.gridx = 1; c.gridy = 0;
            sp.add(defaultspeed,c);

            sspeed = new JSlider(JSlider.HORIZONTAL, 10, 115, lsleept);
            sspeed.setMajorTickSpacing(11);
            sspeed.setMinorTickSpacing(1);
            sspeed.setPaintTicks(true);
            sspeed.setSnapToTicks(true);
            sspeed.addChangeListener((ChangeEvent CE) -> {
                System.out.println(sspeed.getValue());
                lsleept = 120-sspeed.getValue();
            });
            c.gridx = 0; c.gridy = 1;
            c.gridwidth = 2;
            sp.add(sspeed,c);
            
            playforl = new JLabel(playfort); playforl.setHorizontalAlignment(SwingConstants.RIGHT);
            playfor = newJFormattedTextField(playfor, "playfor", 0d, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 3);
            c.gridx = 0; c.gridy = 2;
            c.gridwidth = 1;
            sp.add(playforl,c);
            c.gridx = 1; c.gridy = 2;
            sp.add(playfor,c);
        }
        sp.setBorder(BorderFactory.createTitledBorder(sptext));
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 2;
        add(sp,c);
        
        
        JPanel state = new JPanel(new GridBagLayout());
        String statetext = rb.getString("state.text"); {
            save = newJButton(save,savetext,"save");
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            state.add(save);
            load = newJButton(load,loadtext,"load");
            c.gridx = 1; c.gridy = 0;
            state.add(load);
        }
        state.setBorder(BorderFactory.createTitledBorder(statetext));
        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 2;
        add(state,c);
        
        
        xyJL = new JLabel(xyJLtext);
        xJTF = newJFormattedTextField(xJTF, "xJTF", nbx, Double.MIN_VALUE, Double.MAX_VALUE, new RealNumberFormat(), 4);
        yJTF = newJFormattedTextField(yJTF, "yJTF", nby, Double.MIN_VALUE, Double.MAX_VALUE, new RealNumberFormat(), 4);
        //wJTF = newJTextField(wJTF, "wJTF", Integer.toString(Block.dw), 2);
        //hJTF = newJTextField(hJTF, "hJTF", Integer.toString(Block.dh), 2);
        aJL = new JLabel(aJLtext); aJL.setHorizontalAlignment(SwingConstants.RIGHT);
        aJTF = newJFormattedTextField(aJTF, "aJTF", nba, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 4);
        
        athetaJL = new JLabel(athetaJLtext); athetaJL.setHorizontalAlignment(SwingConstants.RIGHT);
        athetaJTF = newJFormattedTextField(athetaJTF, "athetaJTF", nbatheta, Double.MIN_VALUE, Double.MAX_VALUE, new RealNumberFormat(), 4);
        
        mJL = new JLabel(mJLtext); mJL.setHorizontalAlignment(SwingConstants.RIGHT);
        mJTF = newJFormattedTextField(mJTF, "mJTF", nbm, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 5);
        
        moveforl = new JLabel(movefort); moveforl.setHorizontalAlignment(SwingConstants.RIGHT);
        movefor = newJFormattedTextField(movefor, "movefor", 0d, 0d, Double.MAX_VALUE, new PositiveRealNumberFormat(), 3);
        
        
        JPanel Blockp = new JPanel(new GridBagLayout()); //Block panel
        String Blockptext = rb.getString("Blockp.Border.text");{
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Blockp.add(xyJL,c);

            JPanel coordinates = new JPanel(new GridLayout(1,2));{
                coordinates.add(xJTF);
                coordinates.add(yJTF);
            }
            c.gridx = 1; c.gridy = 0;
            Blockp.add(coordinates,c);

            JPanel initialap = new JPanel(new GridBagLayout()); //acceleration panel
            String accelerationptext = rb.getString("accelerationp.Border.text");{
                c.gridx = 0; c.gridy = 0;
                initialap.add(aJL,c);
                c.gridx = 1; c.gridy = 0;
                initialap.add(aJTF,c);
                c.gridx = 0; c.gridy = 1;
                initialap.add(athetaJL,c);
                c.gridx = 1; c.gridy = 1;
                initialap.add(athetaJTF,c);
            }
            initialap.setBorder(BorderFactory.createTitledBorder(accelerationptext));
            c.gridx = 0; c.gridy = 1;
            c.ipady = 0;
            c.gridwidth = 2;
            Blockp.add(initialap,c);
            c.ipady = -1;

            c.gridx = 0; c.gridy = 2;
            c.gridwidth = 1;
            Blockp.add(mJL,c);
            c.gridx = 1; c.gridy = 2;
            Blockp.add(mJTF,c);
            
            c.gridx = 0; c.gridy = 3;
            Blockp.add(moveforl,c);
            c.gridx = 1; c.gridy = 3;
            Blockp.add(movefor,c);
            
            addBlock = newJButton(addBlock,addBlocktext, "New Block");
            c.gridx = 0; c.gridy = 4;
            c.gridwidth = 2;
            Blockp.add(addBlock,c);
            
            clear = newJButton(clear,cleartext, "clear");
            c.gridx = 0; c.gridy = 5;
            c.gridwidth = 2;
            Blockp.add(clear,c);
        }
        Blockp.setBorder(BorderFactory.createTitledBorder(Blockptext));
        c.gridx = 0; c.gridy = 2;
        add(Blockp,c);
        
        
        JPanel Fp = new JPanel(new GridBagLayout());
        String Fptext = rb.getString("Fp.text");{

            showF = newJCheckBox(showF,showFtext, "Show force(s)", true);
            RCl = newJCheckBox(RCl,RCltext, "Color force(s)", false);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Fp.add(showF,c);
            c.gridx = 1; c.gridy = 0;
            Fp.add(RCl,c);

            shownF = newJCheckBox(shownF,shownFtext, "Show net force", false);
            c.gridx = 0; c.gridy = 1;
            Fp.add(shownF,c);

            showarrows = newJCheckBox(showarrows,showarrowstext, "Show arrows", true);
            c.gridx = 1; c.gridy = 1;
            Fp.add(showarrows,c);

            showparellelogram = newJCheckBox(showparellelogram,showparellelogramtext, "Show parellelogram", true);
            c.gridx = 0; c.gridy = 2;
            c.gridwidth = 2;
            Fp.add(showparellelogram,c);
            
            showlabels = newJCheckBox(showlabels,showlabelstext, "Show labels", true);
            c.gridx = 0; c.gridy = 3;
            c.gridwidth = 1;
            Fp.add(showlabels,c);
            
            showmass = newJCheckBox(showmass,showmasstext, "Show mass", true);
            c.gridx = 1; c.gridy = 3;
            Fp.add(showmass,c);
            
            /*uptodigits = new JSlider(JSlider.HORIZONTAL, 0, 16, 0);
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
                    s.repaint();
                }
            });
            c.gridx = 0; c.gridy = 4;
            Fp.add(uptodigits,c);*/
        }
        Fp.setBorder(BorderFactory.createTitledBorder(Fptext));
        c.gridx = 0; c.gridy = 3;
        c.gridwidth = 2;
        add(Fp,c);
        
        
        JPanel Grp = new JPanel();
        String Grptext = rb.getString("Grp.text"); {
            addgravity = newJCheckBox(addgravity,addgravitytext, "Gravity", false);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Grp.add(addgravity);
            gravitybounce = newJCheckBox(gravitybounce,gravitybouncetext, "Bounce in Gravity", true);
            Grp.add(gravitybounce);
        }
        c.gridx = 0; c.gridy = 4;
        c.gridwidth = 2;
        Grp.setBorder(BorderFactory.createTitledBorder(Grptext));
        add(Grp,c);
        
        
        JPanel ap = new JPanel(new GridBagLayout());
        String aptext = rb.getString("ap.text");{
            a = newJCheckBox(a,atext, "a", true);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            ap.add(a,c);
            drawa = newJCheckBox(a,drawatext, "drawa", true);
            c.gridx = 1; c.gridy = 0;
            ap.add(drawa,c);

            onedgea = new JLabel(onedgeatext);
            onedgea.setHorizontalAlignment(SwingConstants.RIGHT);
            c.gridx = 0; c.gridy = 1;
            ap.add(onedgea,c);
            ahm = newJComboBox(ahm, ahmitems, "ahm");
            c.gridx = 1; c.gridy = 1;
            ap.add(ahm,c);
        }
        ap.setBorder(BorderFactory.createTitledBorder(aptext));
        c.gridx = 0; c.gridy = 5;
        c.gridwidth = 2;
        add(ap,c);
        
        
        onedge = new JLabel(onedgetext);
        c.gridx = 0; c.gridy = 6;
        c.gridwidth = 1;
        onedge.setHorizontalAlignment(SwingConstants.RIGHT);
        add(onedge,c);
        edgehandlingmode = newJComboBox(edgehandlingmode, ehmitems, "ehm");
        edgehandlingmode.setSelectedIndex(1);
        c.gridx = 1; c.gridy = 6;
        add(edgehandlingmode,c);
        
        
        c.gridx = 0; c.gridy = 7;
        onBlock = new JLabel(onBlocktext);
        onBlock.setHorizontalAlignment(SwingConstants.RIGHT);
        add(onBlock,c);
        collisionhandlingmode = newJComboBox(collisionhandlingmode, chmitems, "chm");
        c.gridx = 1; c.gridy = 7;
        add(collisionhandlingmode,c);
        
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
        
        c.gridx = 0; c.gridy = 8;
        c.gridwidth = 2;

        concepts.setBorder(BorderFactory.createTitledBorder(rb.getString("concepts.title")));
        add(concepts,c);*/
        
        themel = new JLabel(themelt);
        themel.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx = 0; c.gridy = 8;
        add(themel,c);
        themes = newJComboBox(themes, themenames, "theme");
        c.gridx = 1; c.gridy = 8;
        add(themes,c);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK), new EmptyBorder(0, 10, 0, 10)));
    }
    
    private JButton newJButton(JButton JB, String text, String ActionCommand) {
        JB = new JButton(text);
        JB.setActionCommand(ActionCommand);
        JB.addActionListener(this);
        return JB;
    }
    private JTextField newJTextField(JTextField jtf, String name, String initial, int length) {
        jtf = new JTextField(initial, length);
        jtf.setActionCommand(name);
        jtf.addActionListener(this);
        return jtf;
    }
    private JFormattedTextField newJFormattedTextField(JFormattedTextField jftf, String name, double initial, double Minimum, double Maximum, Object Format, int length) {
        jftf = new JFormattedTextField(Format);
        jftf.setActionCommand(name);
        jftf.addActionListener(this);
        jftf.setValue(initial);
        jftf.setColumns(length);
        jftf.addMouseListener(new MouseAdapter(){ @Override public void mouseClicked(MouseEvent e) {
            clearjtf(e);
        }});
        return jftf;
    }
    private JCheckBox newJCheckBox(JCheckBox JCB, String text, String ActionCommand, boolean selected) {
        JCB = new JCheckBox(text, selected);
        JCB.setActionCommand(ActionCommand);
        JCB.addActionListener(this);
        return JCB;
    }
    private JComboBox newJComboBox(JComboBox JCB, String[] items, String ActionCommand) {
        JCB = new JComboBox(items);
        JCB.setActionCommand(ActionCommand);
        JCB.addActionListener(this);
        return JCB;
    }
    
    private void clearjtf(MouseEvent e){
        ((JTextField)e.getSource()).setText("");
    }
    
    public double nbx = (screenw-Block.dw)/2, nby = (screenh-Block.dh)/2;
    int nbw = Block.dw, nbh = Block.dh;
    public double nba = 0d;
    public double nbatheta = 0d;
    public double nbm = Block.dm;
    public double moveford = 0d;
    public double playford = 0d;
    //private double pnbx, pnby;
    //private int pnbw, pnbh;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "Gravity":
                Simulation.gravitymode = addgravity.isSelected();
                if(addgravity.isSelected()){
                    Bedgehandlingmode = -1;
                    edgehandlingmode.setEnabled(false);
                    s.Gr = new Ground();
                    s.Gr.update();
                    gravitybounce.setEnabled(true);
                } else {
                    s.Gr = null;
                    Bedgehandlingmode = edgehandlingmode.getSelectedIndex();
                    edgehandlingmode.setEnabled(true);
                    gravitybounce.setEnabled(false);
                } break;
            case "Bounce in Gravity":
                Simulation.bounceingravity = gravitybounce.isSelected();
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
                    nbx = Double.parseDouble(xJTF.getText());
                //pnbx = nbx;
                } break;
            /*case "wJTF":
                nbw = Integer.parseInt(wJTF.getText());
                pnbw = nbw;
                break;
            case "hJTF":
                nbh = Integer.parseInt(hJTF.getText());
                pnbh = nbh;
                break;*/
            case "yJTF":
                if(Double.parseDouble(yJTF.getText()) >= 0d){
                    nby = Double.parseDouble(yJTF.getText());
                //pnby = nby;
                } break;
            case "aJTF":
                nba = Double.parseDouble(aJTF.getText());
                break;
            case "athetaJTF":
                nbatheta = Double.parseDouble(athetaJTF.getText());
                nbatheta = nbatheta*Math.PI/180;
                break;
            case "mJTF":
                if(Double.parseDouble(mJTF.getText()) > 0d){
                    nbm = Double.parseDouble(mJTF.getText());
                } break;
            case "New Block":
                s.addBlock(nbx,nby, nbw,nbh, new Force(nba, nbatheta), nbm, moveford, playford);
                break;
            case "clear":
                s.clear();
                break;
            case "p":
                if(isPaused){
                    isPaused = false;
                    s.play();
                    pause.setText(pausetext);
                } else {
                    isPaused = true;
                    s.pause();
                    pause.setText(play);
                } break;
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
                } break;
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
                } break;
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
            case "ehm":
                Bedgehandlingmode = edgehandlingmode.getSelectedIndex();
                switch(edgehandlingmode.getSelectedIndex()){
                    case 0:
                        ahm.setEnabled(false);
                        break;
                case 1:
                    ahm.setEnabled(true);
                    break;
                } break;
            case "chm":
                Bcollisionhandlingmode = collisionhandlingmode.getSelectedIndex();
                break;
            case "theme":
                currenttheme = themes.getSelectedIndex();
                changetheme(currenttheme);
                break;
            case "playfor":
                if(Double.parseDouble(playfor.getText()) > 0d){
                    playford = Double.parseDouble(playfor.getText());
                    /*actionPerformed(new ActionEvent(this, -1, "dspeed")); //Comment only for absolute timing
                    sspeed.setEnabled(false);
                } else {
                    sspeed.setEnabled(true);*/
                } break;
            case "movefor":
                if(Double.parseDouble(movefor.getText()) > 0d){
                    moveford = Double.parseDouble(movefor.getText());
                } break;
        }
        s.repaint();
    }
    
    public void changetheme(int i){
        JSONParser jp = new JSONParser();
        JSONObject jo = null;
        InputStream is = null;
        try {
            is = getClass().getResource("simulation/themes/"+themefilenames[i]).openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            jo = (JSONObject)jp.parse(br);
        } catch(IOException | ParseException e){
            Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, e);
        }
        s.backc = new Color(Integer.parseInt((String)jo.get("background")));
        s.blockc = new Color(Integer.parseInt((String)jo.get("blockc")));
        s.linec = new Color(Integer.parseInt((String)jo.get("linec")));
        s.accc = new Color(Integer.parseInt((String)jo.get("accc")));
        s.iaccc = new Color(Integer.parseInt((String)jo.get("iaccc")));
        s.textc = new Color(Integer.parseInt((String)jo.get("textc")));
        
        s.setBackground(s.backc);
        s.repaint();
    }
    
    
    private static FileDialog FD;
    private static File F;
    public static boolean pisPaused;
    
    public void save(){
        pisPaused = isPaused;
        if(!pisPaused) actionPerformed(new ActionEvent(this, -1, "p"));
        
        FD = new FileDialog(new JFrame(), "Save to file", FileDialog.SAVE);
        FD.setFile("*.json");
        FD.setVisible(true);
        
        F = new File(FD.getDirectory()+"/"+FD.getFile());
        
        try (FileWriter FW = new FileWriter(F.getAbsolutePath())) {
		FW.write(s.toJSONString());
		FW.flush();
        } catch (IOException IOE) {
            Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, IOE);
        }
        
        if(!pisPaused) actionPerformed(new ActionEvent(this, -1, "p"));
    }
    public void load(){
        if(!isPaused) actionPerformed(new ActionEvent(this, -1, "p"));
        
        FD = new FileDialog(new JFrame(), "Open file", FileDialog.LOAD);
        FD.setFile("*.json");
        FD.setVisible(true);
        
        F = new File(FD.getDirectory()+"/"+FD.getFile());
        
        if(FD.getFile() != null){
            s.clear();
            JSONParser JSONP = new JSONParser();
            
            try {
                JSONObject JSONL = (JSONObject)JSONP.parse(new FileReader(F));


                drawarrow = Boolean.parseBoolean(JSONL.get("drawarrow").toString());
                showarrows.setSelected(Boolean.parseBoolean(JSONL.get("drawarrow").toString()));
                
                gravitymode = Boolean.parseBoolean(JSONL.get("gravitymode").toString());
                addgravity.setSelected(Boolean.parseBoolean(JSONL.get("gravitymode").toString()));
                
                bounceingravity = Boolean.parseBoolean(JSONL.get("bounceingravity").toString());
                gravitybounce.setSelected(Boolean.parseBoolean(JSONL.get("bounceingravity").toString()));
                
                JSONArray JSONblocks = (JSONArray)JSONL.get("blocks");
                Iterator<JSONObject> blocksl = JSONblocks.iterator();

                while(blocksl.hasNext()){
                    JSONObject jsonb = blocksl.next();
                    s.addBlock((double)jsonb.get("x"), (double)jsonb.get("y"), Block.dw, Block.dh, new Force(0, 0), (double)jsonb.get("mass"), (double)jsonb.get("moveford"), (double)jsonb.get("playford"));
                    Simulation.blocks[Simulation.index-1].setinitialmovement((double)jsonb.get("nx"), (double)jsonb.get("ny"),
                                                                             (double)jsonb.get("ax"), (double)jsonb.get("ay"),
                                                                             (double)jsonb.get("vx"), (double)jsonb.get("vy"));
                    JSONArray JSONforces = (JSONArray)jsonb.get("forces");
                    Iterator<JSONObject> forcesl = JSONforces.iterator();

                    JSONArray JSONforcesColor = (JSONArray)jsonb.get("forcesColor");
                    Iterator<String> forcesColorl = JSONforcesColor.iterator();

                    while(forcesl.hasNext()){
                        JSONObject JSONForce = forcesl.next();
                        int JSONForceColor = Integer.parseInt(forcesColorl.next());
                        Simulation.blocks[Simulation.index-1].addForce(new Force((double)JSONForce.get("magnitude"), (double)JSONForce.get("theta")),
                                new Color(JSONForceColor));
                    }
                }
                
                RandomColorline = Boolean.parseBoolean(JSONL.get("RandomColorline").toString());
                RCl.setSelected(Boolean.parseBoolean(JSONL.get("RandomColorline").toString()));
                
                allowacceleration = Boolean.parseBoolean(JSONL.get("allowacceleration").toString());
                a.setSelected(Boolean.parseBoolean(JSONL.get("allowacceleration").toString()));
                
                drawacceleration = Boolean.parseBoolean(JSONL.get("drawacceleration").toString());
                drawa.setSelected(Boolean.parseBoolean(JSONL.get("drawacceleration").toString()));
                
                drawLine = Boolean.parseBoolean(JSONL.get("drawLine").toString());
                showF.setSelected(Boolean.parseBoolean(JSONL.get("drawLine").toString()));
                
                drawnF = Boolean.parseBoolean(JSONL.get("drawnF").toString());
                shownF.setSelected(Boolean.parseBoolean(JSONL.get("drawnF").toString()));
                
                drawLine = Boolean.parseBoolean(JSONL.get("drawLine").toString());
                showF.setSelected(Boolean.parseBoolean(JSONL.get("drawLine").toString()));
                
                drawmass = Boolean.parseBoolean(JSONL.get("drawmass").toString());
                showmass.setSelected(Boolean.parseBoolean(JSONL.get("drawmass").toString()));
                   
                drawlabels = Boolean.parseBoolean(JSONL.get("drawlabels").toString());
                showlabels.setSelected(Boolean.parseBoolean(JSONL.get("drawlabels").toString()));
                
                drawParellelogram = Boolean.parseBoolean(JSONL.get("drawParellelogram").toString());
                showparellelogram.setSelected(Boolean.parseBoolean(JSONL.get("drawParellelogram").toString()));
                
                Bedgehandlingmode = Integer.parseInt(JSONL.get("Bedgehandlingmode").toString());
                edgehandlingmode.setSelectedIndex(Integer.parseInt(JSONL.get("Bedgehandlingmode").toString()));
                
                Bcollisionhandlingmode = Integer.parseInt(JSONL.get("Bcollisionhandlingmode").toString());
                collisionhandlingmode.setSelectedIndex(Integer.parseInt(JSONL.get("Bcollisionhandlingmode").toString()));
                
                Bahandlingmode = Integer.parseInt(JSONL.get("Bahandlingmode").toString());
                ahm.setSelectedIndex(Integer.parseInt(JSONL.get("Bahandlingmode").toString()));
                
                if(Integer.parseInt(JSONL.get("cfblock").toString()) != -1){
                    s.hascfblock = true;
                    s.cfblock = Integer.parseInt(JSONL.get("cfblock").toString());
                    blocks[s.cfblock].focus();
                }
                
                refreshControls();
            } catch (FileNotFoundException FNFE) {
                Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, FNFE);
            } catch (IOException | ParseException E) {
                Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, E);
            }

            SwingUtilities.invokeLater(() -> { s.repaint();});
            actionPerformed(new ActionEvent(this, -1, "p"));
        }
    }
    public void refreshControls(){
        actionPerformed(new ActionEvent(showarrows,-1,showarrows.getActionCommand()));
        actionPerformed(new ActionEvent(addgravity,-1,addgravity.getActionCommand()));
        actionPerformed(new ActionEvent(gravitybounce,-1,gravitybounce.getActionCommand()));
        
        actionPerformed(new ActionEvent(RCl,-1,RCl.getActionCommand()));
        actionPerformed(new ActionEvent(a,-1,a.getActionCommand()));
        actionPerformed(new ActionEvent(drawa,-1,drawa.getActionCommand()));
        actionPerformed(new ActionEvent(showF,-1,showF.getActionCommand()));
        actionPerformed(new ActionEvent(showmass,-1,showmass.getActionCommand()));
        actionPerformed(new ActionEvent(showlabels,-1,showlabels.getActionCommand()));
        actionPerformed(new ActionEvent(showparellelogram,-1,showparellelogram.getActionCommand()));
        
        actionPerformed(new ActionEvent(edgehandlingmode,-1,edgehandlingmode.getActionCommand()));
        actionPerformed(new ActionEvent(collisionhandlingmode,-1,collisionhandlingmode.getActionCommand()));
        actionPerformed(new ActionEvent(ahm,-1,ahm.getActionCommand()));
        actionPerformed(new ActionEvent(themes,-1,themes.getActionCommand()));
    }
}