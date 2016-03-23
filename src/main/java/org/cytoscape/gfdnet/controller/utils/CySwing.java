package org.cytoscape.gfdnet.controller.utils;

import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class CySwing {
    private static CySwingApplication cySwingApplication;
    private static CyServiceRegistrar serviceRegistrar;
    
    private static JFrame CytoscapeJFrame;
    private static CytoPanel eastPanel;
    
    public static void init(CySwingApplication cySwingApplication, CyServiceRegistrar serviceRegistrar) {
        CySwing.cySwingApplication = cySwingApplication;
        CySwing.serviceRegistrar = serviceRegistrar;
    }

    public static JFrame getDesktopJFrame() {
        if (CytoscapeJFrame == null) {
            CytoscapeJFrame = cySwingApplication.getJFrame();
        }
        return CytoscapeJFrame;
    }
    
    private static CytoPanel getEastPanel() {
        if (eastPanel == null) {
            eastPanel = cySwingApplication.getCytoPanel(CytoPanelName.EAST);
        }
        return eastPanel;
    }
    
    public static void displayPopUpMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(getDesktopJFrame(), message);
            }
        });
    }
    
    public static void displayDialog(final JDialog dialog) {
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
    
    public static void addPanel(CytoPanelComponent panel) {
        serviceRegistrar.registerService(panel, CytoPanelComponent.class, new Properties());
        selectPanel(panel);
    }
    
    public static void removePanel(CytoPanelComponent panel) {
        serviceRegistrar.unregisterService(panel, CytoPanelComponent.class);
        getEastPanel().setState(CytoPanelState.HIDE);
    }
    
    public static void selectPanel(CytoPanelComponent panel) {
        int panelIndex = getEastPanel().indexOfComponent(panel.getComponent());
        getEastPanel().setSelectedIndex(panelIndex);
        getEastPanel().setState(CytoPanelState.DOCK);
    }
}