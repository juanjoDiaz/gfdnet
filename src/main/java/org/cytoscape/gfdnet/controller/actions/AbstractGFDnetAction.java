package org.cytoscape.gfdnet.controller.actions;

import javax.swing.ImageIcon;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.gfdnet.controller.CoreController;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public abstract class AbstractGFDnetAction extends AbstractCyAction{
    protected final CoreController core;
    
    public AbstractGFDnetAction(CoreController core, String name, String iconName) {
        super(name);
        putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource(iconName)));
        putValue(SHORT_DESCRIPTION, name);
        this.inToolBar = true;
        this.inMenuBar = false;
        this.toolbarGravity = 100.0f;
        
        this.core = core;
    }
    
    @Override
    public void updateEnableState() {
        //DO Nothing
    } 
}