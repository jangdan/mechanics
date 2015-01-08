package mango.mechanics;

import mango.mechanics.simulation.environment.Ground;
import mango.mechanics.simulation.Block;
import mango.mechanics.simulation.Force;
import mango.mechanics.controls.PositiveRealNumberFormat;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import static mango.mechanics.MechanicsSimulator.*;
import static mango.mechanics.Simulation.*;
import static mango.mechanics.simulation.Block.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Controls extends JPanel implements ActionListener {
    JButton load, save; String loadtext = RB.getString("load.text"), savetext = RB.getString("save.text");
    
    JCheckBox addgravity; String addgravitytext = RB.getString("addgravity.text");
    JCheckBox gravitybounce; String gravitybouncetext = RB.getString("gravitybounce.text");
    
    JButton pause; String pausetext = RB.getString("pause.text"); String play = RB.getString("pause.play");
    JButton defaultspeed; String defaultspeedtext = RB.getString("defaultspeed.text");
    JSlider sspeed;
    
    JLabel xyJL; String xyJLtext = RB.getString("xyJL.text"); JFormattedTextField xJTF, yJTF;
    //JTextField wJTF, hJTF;
    JLabel aJL; String aJLtext = RB.getString("aJL.text"); JTextField aJTF;
    JLabel athetaJL; String athetaJLtext = RB.getString("athetaJL.text"); JTextField athetaJTF;
    JLabel mJL; String mJLtext = RB.getString("mJL.text"); JFormattedTextField mJTF;
    
    JButton addBlock; String addBlocktext = RB.getString("addBlock.text");
    //JToggleButton removeBlock; String removeBlockBlocktext = RB.getString("removeBlock.text"); //Click on Block to remove it
    JButton clear; String cleartext = RB.getString("clear.text");
    
    JCheckBox showF; String showFtext = RB.getString("showF.text"); //Show Force(s)
    JCheckBox RCl; String RCltext = RB.getString("RCl.text"); //Random Color line(Forces)
    JCheckBox shownF; String shownFtext = RB.getString("shownF.text"); //show net force
    JCheckBox showarrows; String showarrowstext = RB.getString("showarrows.text"); //Show arrows
    JCheckBox showparellelogram; String showparellelogramtext = RB.getString("showparellelogram.text"); //Show parellelogram
    JCheckBox showmass; String showmasstext = RB.getString("showmass.text"); //Show Block mass
    JCheckBox showlabels; String showlabelstext = RB.getString("showlabels.text"); //Show Block mass
    /*JCheckBox shownumbers; String shownumberstext = RB.getString("shownumbers.text"); //Show Force(s) in numbers
    //JLabel uptodigitsJL; String uptodigitsJLtext = RB.getString("uptodigits.text");
    JSlider uptodigits; //Show Force(s) in numbers up to specified digits
    int lastuptodigitsvalue = 16;
    */
    JCheckBox a; String atext = RB.getString("a.text"); //allow acceleration
    JCheckBox drawa; String drawatext = RB.getString("drawa.text");
    
    JLabel onedgea; String onedgeatext = RB.getString("ahm.text");
    JComboBox ahm; String[] ahmitems = { //Block acceleration handling mode items
        RB.getString("ahm.keep"), RB.getString("ahm.reverse"), RB.getString("ahm.reset")
    };
    
    JLabel onedge; String onedgetext = RB.getString("ehm.text");
    JComboBox edgehandlingmode; String[] ehmitems = { //edgehandlingmode items
        RB.getString("ehm.unlimited"), RB.getString("ehm.bounce")/*, RB.getString("ehm.wrap"),*/
    };
    
    JLabel onBlock; String onBlocktext = RB.getString("chm.text");
    JComboBox collisionhandlingmode; String[] chmitems = { //collisionhandlingmode items
        RB.getString("chm.ignore")/*, RB.getString("chm.bounce")*/
    };
    
    JTabbedPane concepts; 
    JPanel fo; JPanel nefo; JPanel ac; JPanel gr;
    
    
    public Controls() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = -1;
        
        JPanel sp = new JPanel(new GridBagLayout());
        String sptext = RB.getString("sp.text");{
        pause = newJButton(pause,pausetext, "p");
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            sp.add(pause,c);
            defaultspeed = newJButton(defaultspeed,defaultspeedtext,"dspeed");
            c.gridx = 1; c.gridy = 0;
            sp.add(defaultspeed,c);

            sspeed = new JSlider(JSlider.HORIZONTAL, 10, 115, sleept);
            sspeed.setMajorTickSpacing(11);
            sspeed.setMinorTickSpacing(1);
            sspeed.setPaintTicks(true);
            sspeed.setSnapToTicks(true);
            sspeed.addChangeListener((ChangeEvent CE) -> {
                System.out.println(sspeed.getValue());
                sleept = 120-sspeed.getValue();
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
        String statetext = RB.getString("state.text"); {
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
        xJTF = newJFormattedTextField(xJTF, "xJTF", Block.dx-Block.dw/2, 0d, (double)MechanicsSimulator.screenw - Block.dw, new PositiveRealNumberFormat());
        yJTF = newJFormattedTextField(yJTF, "yJTF", Block.dy-Block.dh/2, 0d, (double)MechanicsSimulator.screenh - Block.dh, new PositiveRealNumberFormat());
        //wJTF = newJTextField(wJTF, "wJTF", Integer.toString(Block.dw), 2);
        //hJTF = newJTextField(hJTF, "hJTF", Integer.toString(Block.dh), 2);
        aJL = new JLabel(aJLtext); aJL.setHorizontalAlignment(SwingConstants.RIGHT); aJTF = newJTextField(aJTF, "aJTF", Double.toString(nba), 3);
        athetaJL = new JLabel(athetaJLtext); athetaJL.setHorizontalAlignment(SwingConstants.RIGHT); athetaJTF = newJTextField(athetaJTF, "athetaJTF", Double.toString(nbatheta), 3);
        mJL = new JLabel(mJLtext); mJL.setHorizontalAlignment(SwingConstants.RIGHT);
        mJTF = newJFormattedTextField(mJTF, "mJTF", nbm, 0d, 99999d, new PositiveRealNumberFormat());
        
        JPanel Blockp = new JPanel(new GridBagLayout()); //Block panel
        String Blockptext = RB.getString("Blockp.Border.text");{
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
            String accelerationptext = RB.getString("accelerationp.Border.text");{
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
            
            addBlock = newJButton(addBlock,addBlocktext, "New Block");
            c.gridx = 0; c.gridy = 3;
            c.gridwidth = 2;
            Blockp.add(addBlock,c);
            
            clear = newJButton(clear,cleartext, "clear");
            c.gridx = 0; c.gridy = 4;
            c.gridwidth = 2;
            Blockp.add(clear,c);
        }
        Blockp.setBorder(BorderFactory.createTitledBorder(Blockptext));
        c.gridx = 0; c.gridy = 2;
        add(Blockp,c);
        
        JPanel Fp = new JPanel(new GridBagLayout());
        String Fptext = RB.getString("Fp.text");{

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
                            Block.digits = uptodigits.getValue();
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
        String Grptext = RB.getString("Grp.text"); {
            addgravity = newJCheckBox(addgravity,addgravitytext, "Gravity", false);
            c.gridx = 0; c.gridy = 0;
            c.gridwidth = 1;
            Grp.add(addgravity);
            gravitybounce = newJCheckBox(gravitybounce,gravitybouncetext, "Bounce in Gravity", true);
            gravitybounce.setEnabled(false);
            Grp.add(gravitybounce);
        }
        c.gridx = 0; c.gridy = 4;
        c.gridwidth = 2;
        Grp.setBorder(BorderFactory.createTitledBorder(Grptext));
        add(Grp,c);
        
        
        JPanel ap = new JPanel(new GridBagLayout());
        String aptext = RB.getString("ap.text");{
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
        fo.add(new JLabel(RB.getString("concepts.fo.text")));
        concepts.addTab(RB.getString("concepts.fo.title"),fo);
        
        nefo = new JPanel();
        nefo.add(new JLabel(RB.getString("concepts.nefo.text")));
        concepts.addTab(RB.getString("concepts.nefo.title"),nefo);
        
        ac = new JPanel();
        ac.add(new JLabel(RB.getString("concepts.ac.text")));
        concepts.addTab(RB.getString("concepts.ac.title"),ac);
        
        gr = new JPanel();
        gr.add(new JLabel(RB.getString("concepts.gr.text")));
        concepts.addTab(RB.getString("concepts.gr.title"),gr);
        
        c.gridx = 0; c.gridy = 8;
        c.gridwidth = 2;

        concepts.setBorder(BorderFactory.createTitledBorder(RB.getString("concepts.title")));
        add(concepts,c);*/
        
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK), new EmptyBorder(0, 10, 0, 10)));
    }
    
    private JButton newJButton(JButton JB, String text, String ActionCommand) {
        JB = new JButton(text);
        JB.setActionCommand(ActionCommand);
        JB.addActionListener(this);
        return JB;
    }
    private JTextField newJTextField(JTextField JTF, String name, String initial, int length) {
        JTF = new JTextField(initial, length);
        JTF.setActionCommand(name);
        JTF.addActionListener(this);
        return JTF;
    }
    private JFormattedTextField newJFormattedTextField(JFormattedTextField JFTF, String name, double initial, double Minimum, double Maximum, Object Format) {
        JFTF = new JFormattedTextField(Format);
        JFTF.setActionCommand(name);
        JFTF.addActionListener(this);
        JFTF.setValue(initial);
        JFTF.setColumns(Double.toString(Maximum).length());
        return JFTF;
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
    
    private double realpositivenumber(int digits, double r) {
        String rString = Double.toString(r);
        rString = rString.replaceAll("[^0-9]","");
        return Double.parseDouble(rString);
    } private double realnumber(double r) {
        String rString = Double.toString(r);
        rString = rString.replaceAll("[^0-9]","");
        return Double.parseDouble(rString);
    }
    
    private void setText(JFormattedTextField JFTF, String text){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {  
                if (JFTF != null) JFTF.setText(text);  
            }  
        });  
    }
    
    public double nbx = Block.dx-Block.dw/2, nby = Block.dy-Block.dh/2;
    private int nbw = Block.dw, nbh = Block.dh;
    public double nba = 0d;
    public double nbatheta = 0d;
    public double nbm = Block.dm;
    
    private double pnbx, pnby;
    private int pnbw, pnbh;
    
    private boolean paused = false;
    @Override
    public void actionPerformed(ActionEvent AE) {
        if("Gravity".equals(AE.getActionCommand())){
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
            }
        } else if("Bounce in Gravity".equals(AE.getActionCommand())){
            Simulation.bounceingravity = gravitybounce.isSelected();
        }
        else if("save".equals(AE.getActionCommand())){
            save();
        } else if("load".equals(AE.getActionCommand())){
            load();
        }
        else if("dspeed".equals(AE.getActionCommand())){
            sspeed.setValue(30);
            sleept = 30;
        } else if("xJTF".equals(AE.getActionCommand())){
            if(Double.parseDouble(xJTF.getText()) >= 0d){
                nbx = Double.parseDouble(xJTF.getText());
                pnbx = nbx;
            }
        } else if("yJTF".equals(AE.getActionCommand())){
            if(Double.parseDouble(yJTF.getText()) >= 0d){
                nby = Double.parseDouble(yJTF.getText());
                pnby = nby;
            }
        } /*else if("wJTF".equals(AE.getActionCommand())){
            nbw = Integer.parseInt(wJTF.getText());
            pnbw = nbw;
        } else if("hJTF".equals(AE.getActionCommand())){
            nbh = Integer.parseInt(hJTF.getText());
            pnbh = nbh;
        }*/ else if("aJTF".equals(AE.getActionCommand())){
            nba = Double.parseDouble(aJTF.getText());
        } else if("athetaJTF".equals(AE.getActionCommand())){
            nbatheta = Double.parseDouble(athetaJTF.getText());
            nbatheta = nbatheta*Math.PI/180;
        } else if("mJTF".equals(AE.getActionCommand())){
            if(Double.parseDouble(mJTF.getText()) > 0d){
                nbm = Double.parseDouble(mJTF.getText());
            }
        }
        else if("New Block".equals(AE.getActionCommand())){
            s.addBlock(nbx,nby, nbw,nbh, new Force(nba, nbatheta), nbm);
        }
        else if("clear".equals(AE.getActionCommand())){
            s.clear();
        }
        else if("p".equals(AE.getActionCommand())){
            if(paused){
                paused = false;
                s.play();
                pause.setText(pausetext);
            } else {
                paused = true;
                s.pause();
                pause.setText(play);
            }
        }
        else if("a".equals(AE.getActionCommand())){
            if(a.isSelected()){
                if(showF.isSelected()){
                    drawa.setEnabled(true);
                }
                ahm.setEnabled(true);
                Block.allowacceleration = true;
            } else {
                drawa.setEnabled(false);
                ahm.setEnabled(false);
                Block.allowacceleration = false;
            }
        } else if("drawa".equals(AE.getActionCommand())){
            Block.drawacceleration = drawa.isSelected();
        } else if("ahm".equals(AE.getActionCommand())){
            Block.Bahandlingmode = ahm.getSelectedIndex();
        }
        else if("Show force(s)".equals(AE.getActionCommand())){
            if(showF.isSelected()){
                drawLine = true;
                RCl.setEnabled(true);
                shownF.setEnabled(true);
                showarrows.setEnabled(true);
                showlabels.setEnabled(true);
                if(a.isSelected()){
                    drawa.setEnabled(true);
                }
                
                Block.RandomColorline = RCl.isSelected();
                drawnF = shownF.isSelected();
                drawarrow = showarrows.isSelected();
                //drawnumbers = showlabels.isSelected();
                Block.drawacceleration = drawa.isSelected();
            } else {
                drawLine = false;
                RCl.setEnabled(false);
                shownF.setEnabled(false);
                showarrows.setEnabled(false);
                showlabels.setEnabled(false);
                //uptodigits.setEnabled(false);
                drawa.setEnabled(false);
                
                Block.RandomColorline = RCl.isSelected();
                drawnF = false;
                drawarrow = false;
                //drawnumbers = false;
                Block.drawacceleration = false;
            }
        } else if("Color force(s)".equals(AE.getActionCommand())){
            Block.RandomColorline = RCl.isSelected();
        } else if("Show net force".equals(AE.getActionCommand())){
            drawnF = shownF.isSelected();
        }
        else if("Show arrows".equals(AE.getActionCommand())){
            drawarrow = showarrows.isSelected();
        }
        else if("Show parellelogram".equals(AE.getActionCommand())){
            drawParellelogram = showparellelogram.isSelected();
        }
        else if("Show labels".equals(AE.getActionCommand())){
            Block.drawlabels = showlabels.isSelected();
        }
        else if("Show mass".equals(AE.getActionCommand())){
            Block.drawmass = showmass.isSelected();
        }
        else if("ehm".equals(AE.getActionCommand())){
            Block.Bedgehandlingmode = edgehandlingmode.getSelectedIndex();
            switch(edgehandlingmode.getSelectedIndex()){
                case 0:
                    ahm.setEnabled(false);
                    break;
                case 1:
                    ahm.setEnabled(true);
                    break;
            }
        }
        else if("chm".equals(AE.getActionCommand())){
            Block.Bcollisionhandlingmode = collisionhandlingmode.getSelectedIndex();
        }
        s.repaint();
    }
    
    
    private static FileDialog FD;
    
    private static File F;
    public static boolean pisPaused;
    
    public void save() {
        pisPaused = isPaused;
        if(!pisPaused) s.pause();
        
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
        
        if(!pisPaused) s.play();
    }
    public void load() {
        if(!isPaused) s.pause();
        
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
                    JSONObject JSONBlock = blocksl.next();
                    s.addBlock((double)JSONBlock.get("x"), (double)JSONBlock.get("y"), Block.dw, Block.dh, new Force(0, 0), (double)JSONBlock.get("mass"));
                    Simulation.blocks[Simulation.index-1].setinitialmovement((double)JSONBlock.get("nx"), (double)JSONBlock.get("ny"),
                                                                             (double)JSONBlock.get("ax"), (double)JSONBlock.get("ay"),
                                                                             (double)JSONBlock.get("vx"), (double)JSONBlock.get("vy"));
                    JSONArray JSONforces = (JSONArray)JSONBlock.get("forces");
                    Iterator<JSONObject> forcesl = JSONforces.iterator();

                    JSONArray JSONforcesColor = (JSONArray)JSONBlock.get("forcesColor");
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
                
                refreshControls();
            } catch (FileNotFoundException FNFE) {
                Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, FNFE);
            } catch (IOException | ParseException E) {
                Logger.getLogger(Controls.class.getName()).log(Level.SEVERE, null, E);
            }

            SwingUtilities.invokeLater(() -> {
                s.repaint();
                paused = true;
                s.pause();
                pause.setText(play);
            });
        }
    }
    public void refreshControls() {
        actionPerformed(new ActionEvent(showarrows,0,showarrows.getActionCommand()));
        actionPerformed(new ActionEvent(addgravity,0,addgravity.getActionCommand()));
        actionPerformed(new ActionEvent(gravitybounce,0,gravitybounce.getActionCommand()));
        
        actionPerformed(new ActionEvent(RCl,0,RCl.getActionCommand()));
        actionPerformed(new ActionEvent(a,0,a.getActionCommand()));
        actionPerformed(new ActionEvent(drawa,0,drawa.getActionCommand()));
        actionPerformed(new ActionEvent(showF,0,showF.getActionCommand()));
        actionPerformed(new ActionEvent(showmass,0,showmass.getActionCommand()));
        actionPerformed(new ActionEvent(showlabels,0,showlabels.getActionCommand()));
        actionPerformed(new ActionEvent(showparellelogram,0,showparellelogram.getActionCommand()));
        
        actionPerformed(new ActionEvent(edgehandlingmode,0,edgehandlingmode.getActionCommand()));
        actionPerformed(new ActionEvent(collisionhandlingmode,0,collisionhandlingmode.getActionCommand()));
        actionPerformed(new ActionEvent(ahm,0,ahm.getActionCommand()));
    }
}