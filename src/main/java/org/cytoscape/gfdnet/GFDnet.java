package org.cytoscape.gfdnet;

import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.gfdnet.controller.actions.MenuAction;
import org.cytoscape.gfdnet.controller.utils.OSGiManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnet extends AbstractCyActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        OSGiManager.setup(getService(context, CyApplicationManager.class),
                getService(context, CySwingApplication.class),
                getService(context, CyServiceRegistrar.class),
                getService(context, TaskManager.class),
                getService(context, CyNetworkManager.class),
                getService(context, CyNetworkViewManager.class),
                getService(context, CyNetworkViewFactory.class));
        MenuAction menuAction = new MenuAction();
        registerService(context,menuAction, CyAction.class, new Properties());
    }
}