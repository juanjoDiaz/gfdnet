package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.view.configurationDialogs.ConfigurateDBView;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ConfigDBAction extends AbstractGFDnetAction {

    public ConfigDBAction(CoreController core){
        super(core, "Config DB", "/images/DB.png");
    }

    //TODO for version 3.1 @Override
    public boolean insertSeparatorBefore() {
        return true;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        CySwing.displayDialog(new ConfigurateDBView(core));
    }
}