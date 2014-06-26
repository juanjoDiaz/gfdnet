package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.gfdnet.controller.CoreController;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class RefreshAction extends AbstractGFDnetAction {
    public RefreshAction(CoreController core){
        super(core, "Refresh", "/images/refresh.png");
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        core.refresh();
    }
}