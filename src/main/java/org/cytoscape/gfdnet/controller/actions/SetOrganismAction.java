package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.view.configurationDialogs.SetOrganismMainView;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class SetOrganismAction extends AbstractGFDnetAction {
    public SetOrganismAction(CoreController core){
        super(core, "Set Organism", "/images/organism.png");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        CySwing.displayDialog(new SetOrganismMainView(core));
    }
}