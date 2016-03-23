package org.cytoscape.gfdnet.controller.listener;

import java.util.Map.Entry;
import org.cytoscape.gfdnet.controller.ResultPanelController;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ClickOnViewListener implements RowsSetListener{
    private static boolean enable = true;

    @Override
    public void handleEvent(RowsSetEvent rse) {
        if(enable) {
            CyTable table = rse.getSource();
            for (Entry<CyNetwork, ResultPanelController> result : ResultPanelController.panels.entrySet()) {
                CyNetwork network = result.getKey();
                if (table.equals(network.getDefaultNodeTable()) || table.equals(network.getDefaultEdgeTable())) {
                    result.getValue().showSelectedElementInfo();
                    return;
                }
            }
        }
    }
    
    public static void enable() {
        enable = true;
    }
    
    public static void disable() {
        enable = false;
    }
}