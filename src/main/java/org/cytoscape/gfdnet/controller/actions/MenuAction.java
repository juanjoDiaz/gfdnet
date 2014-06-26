package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.utils.CySwing;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class MenuAction extends AbstractCyAction {
    public boolean running;

    public MenuAction() {
        super("GFD-Net");
        setPreferredMenu("Apps");
        running = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            //TODO this.enabled = false; not working. Get rid of running
            if (!running && super.isEnabled()){
                new CoreController();
                running = true;
                super.setEnabled(false);
            }
        } 
        catch(Exception ex){
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    }
}