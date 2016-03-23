package org.cytoscape.gfdnet;

import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.gfdnet.controller.NetworkController;
import org.cytoscape.gfdnet.controller.actions.MenuAction;
import org.cytoscape.gfdnet.controller.listener.ClickOnViewListener;
import org.cytoscape.gfdnet.controller.listener.NetworkClosedListener;
import org.cytoscape.gfdnet.controller.tasks.ImportGFDNetVisualStylesTask;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnet extends AbstractCyActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        CyApplicationManager applicationManager = getService(context, CyApplicationManager.class);
        CySwingApplication swingApplication = getService(context, CySwingApplication.class);
        CyServiceRegistrar serviceRegistrar = getService(context, CyServiceRegistrar.class);
        VisualMappingManager visualMappingManager = getService(context, VisualMappingManager.class);
        VizmapReaderManager vizmapReaderManager = getService(context, VizmapReaderManager.class);
        TaskManager taskManager = getService(context, TaskManager.class);
        
        CySwing.init(swingApplication, serviceRegistrar); 
        NetworkController.init(applicationManager, visualMappingManager);
        
        // UI controls
        MenuAction menuAction = new MenuAction(taskManager);
        registerService(context, menuAction, CyAction.class, new Properties());


        taskManager.execute(new TaskIterator(new ImportGFDNetVisualStylesTask(visualMappingManager, vizmapReaderManager)));
        
        serviceRegistrar.registerService(new ClickOnViewListener(), RowsSetListener.class, new Properties());
        serviceRegistrar.registerService(new NetworkClosedListener(), NetworkAboutToBeDestroyedListener.class, new Properties());
    }
}