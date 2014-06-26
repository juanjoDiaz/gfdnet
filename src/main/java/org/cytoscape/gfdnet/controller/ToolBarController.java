package org.cytoscape.gfdnet.controller;

import java.util.Properties;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.gfdnet.controller.actions.ConfigDBAction;
import org.cytoscape.gfdnet.controller.actions.ExecuteAction;
import org.cytoscape.gfdnet.controller.actions.RefreshAction;
import org.cytoscape.gfdnet.controller.actions.SetOntologyAction;
import org.cytoscape.gfdnet.controller.actions.SetOrganismAction;
import org.cytoscape.gfdnet.controller.utils.OSGiManager;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ToolBarController {
    private static ConfigDBAction configDBButton;
    private static SetOntologyAction setOntologyButton;
    private static SetOrganismAction setOrganismButton;
    private static ExecuteAction executeButton;
    private static RefreshAction refreshButton;
    
    public ToolBarController(CoreController core){
        configDBButton = new ConfigDBAction(core);
        setOntologyButton = new SetOntologyAction(core);
        setOrganismButton = new SetOrganismAction(core);
        executeButton = new ExecuteAction(core);
        refreshButton = new RefreshAction(core);
        
	OSGiManager.registerService(configDBButton, CyAction.class, new Properties());
	OSGiManager.registerService(setOntologyButton, CyAction.class, new Properties());
	OSGiManager.registerService(setOrganismButton, CyAction.class, new Properties());
        OSGiManager.registerService(executeButton, CyAction.class, new Properties());
        OSGiManager.registerService(refreshButton, CyAction.class, new Properties());
        
        setOrganismButton.setEnabled(false);
        executeButton.setEnabled(false);
    }
    
    public void dispose(){
	OSGiManager.unregisterService(configDBButton, CyAction.class);
	OSGiManager.unregisterService(setOntologyButton, CyAction.class);
	OSGiManager.unregisterService(setOrganismButton, CyAction.class);
        OSGiManager.unregisterService(executeButton, CyAction.class);
        OSGiManager.unregisterService(refreshButton, CyAction.class);  
    }
    
    public void configDBButtonEnabled(boolean enabled){
        configDBButton.setEnabled(enabled);
    }
    
    public void setOntologyButtonEnabled(boolean enabled){
        setOntologyButton.setEnabled(enabled);
    }
    
    public void setOrganismButtonEnabled(boolean enabled){
        setOrganismButton.setEnabled(enabled);
    }
    
    public void executeButtonEnabled(boolean enabled){
        if(!enabled || setOrganismButton.isEnabled()){
            executeButton.setEnabled(enabled);
        }
    }  
    
    public void refreshButtonEnabled(boolean enabled){
        refreshButton.setEnabled(enabled);
    }
}