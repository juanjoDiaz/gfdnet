package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.view.configurationDialogs.SetOntologyView;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class SetOntologyAction extends AbstractGFDnetAction {   
    public SetOntologyAction(CoreController core){
        super(core, "Set Ontology", "/images/ontology.gif");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        CySwing.displayDialog(new SetOntologyView(core));
    }
}