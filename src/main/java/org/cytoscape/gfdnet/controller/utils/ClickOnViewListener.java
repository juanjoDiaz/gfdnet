package org.cytoscape.gfdnet.controller.utils;

import org.cytoscape.gfdnet.controller.ResultPanelsController;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class ClickOnViewListener implements RowsSetListener{

    private boolean enable;
    private final ResultPanelsController resultPanels;
    
    public ClickOnViewListener(ResultPanelsController resultPanels){
        this.resultPanels = resultPanels;
        enable = true;
    }

    @Override
    public void handleEvent(RowsSetEvent rse) {
        if(enable){
            resultPanels.showSelectedElementInfo();
        }
    }
    
    public void enable(){
        enable = true;
    }
    
    public void disable(){
        enable = false;
    }
}