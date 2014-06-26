package org.cytoscape.gfdnet.controller.utils;

import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class CySwing {
    private static JFrame CytoscapeJFrame;
    private static CytoPanel eastPanel;

    public static JFrame getDesktopJFrame(){
        if (CytoscapeJFrame == null){
            CytoscapeJFrame = OSGiManager.getCySwingApplication().getJFrame();
        }
        return CytoscapeJFrame;
    }
    
    private static CytoPanel getEastPanel(){
        if (eastPanel == null){
            eastPanel = OSGiManager.getCySwingApplication().getCytoPanel(CytoPanelName.EAST);
        }
        return eastPanel;
    }
    
    public static void displayPopUpMessage(final String message){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(getDesktopJFrame(), message);
            }
        });
    }
    
    public static void displayDialog(final JDialog dialog){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        dialog.dispose();
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    public static void addPanel(CytoPanelComponent panel){
        OSGiManager.registerService(panel, CytoPanelComponent.class, new Properties());
        int panelIndex = getEastPanel().indexOfComponent(panel.getComponent());
        getEastPanel().setSelectedIndex(panelIndex);
        getEastPanel().setState(CytoPanelState.DOCK);
    }
    
    public static void removePanel(CytoPanelComponent panel){
        OSGiManager.unregisterService(panel, CytoPanelComponent.class);
        getEastPanel().setState(CytoPanelState.HIDE);
    }
}