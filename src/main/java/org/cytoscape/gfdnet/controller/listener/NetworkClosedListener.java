package org.cytoscape.gfdnet.controller.listener;

import java.util.Map.Entry;
import org.cytoscape.gfdnet.controller.ResultPanelController;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class NetworkClosedListener implements NetworkAboutToBeDestroyedListener{

    @Override
    public void handleEvent(NetworkAboutToBeDestroyedEvent nvde) {
        CyNetwork network = nvde.getNetwork();
        for (Entry<CyNetwork, ResultPanelController> en : ResultPanelController.panels.entrySet()) {
            if (en.getKey().equals(network)) {
                en.getValue().dispose();
                break;
            }
        }
    }
}
