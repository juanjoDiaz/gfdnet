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
    
    public AbstractGFDnetAction(CoreController core, String name, String menuOrIcon) {
        super(name);
        if(menuOrIcon.startsWith("/images/")){
            ImageIcon icon = new ImageIcon(getClass().getResource(menuOrIcon));
            putValue(LARGE_ICON_KEY, icon);
            this.inToolBar = true;
            this.inMenuBar = false;
        }else{
            setPreferredMenu(menuOrIcon); 
            this.inToolBar = false;
            this.inMenuBar = true;
        }
        this.core = core;
    }
}