package org.cytoscape.gfdnet.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.gfdnet.view.configurationDialogs.ConfigurationDialog;
import org.cytoscape.work.TaskManager;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class MenuAction extends AbstractCyAction {
    private final ConfigurationDialog dialog;
    

    public MenuAction(TaskManager taskManager) {
        super("GFD-Net");
        setPreferredMenu("Apps");
        dialog = new ConfigurationDialog(taskManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(true);
    }
}