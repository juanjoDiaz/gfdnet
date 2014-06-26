package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.gfdnet.controller.CoreController;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ExecuteAction extends AbstractGFDnetAction {
    public ExecuteAction(CoreController core){
        super(core, "Execute GFD-Net", "/images/exec.gif");
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        core.executeGFDnet();
    }
}