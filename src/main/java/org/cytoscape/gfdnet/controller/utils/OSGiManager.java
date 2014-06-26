package org.cytoscape.gfdnet.controller.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class OSGiManager {
    private static CyApplicationManager applicationManager;
    private static CySwingApplication swingApplication;
    private static CyServiceRegistrar serviceRegistrar;
    private static TaskManager taskManager;
    private static CyNetworkManager networkManager;
    private static CyNetworkViewManager networkViewManager;
    private static CyNetworkViewFactory networkViewFactory;
    
    private static List registeredServices;
    
    public static void setup(CyApplicationManager am,CySwingApplication sa,
            CyServiceRegistrar sr, TaskManager tm, CyNetworkManager rm,
            CyNetworkViewManager rvm, CyNetworkViewFactory rvf){
        applicationManager = am;
        swingApplication = sa;
        serviceRegistrar = sr;
        taskManager = tm;
        networkManager = rm;
        networkViewManager = rvm;
        networkViewFactory = rvf;
        registeredServices = new LinkedList();
    }
    
    public static void dispose(){
        for (Object registeredService : registeredServices){
            serviceRegistrar.unregisterAllServices(registeredService);
        }
    }
    
    public static CyApplicationManager getCyApplicationManager(){
        return applicationManager;
    }
    
    public static CySwingApplication getCySwingApplication(){
        return swingApplication;
    }
    
    public static <S extends Object> S getService(Class<S> type){
        return serviceRegistrar.getService(type); 
    }
    
    public static void registerService(Object o, Class<?> type, Properties prprts){
        registeredServices.add(o);
        serviceRegistrar.registerService(o, type, prprts); 
    }
    
    public static void unregisterService(Object o, Class<?> type){
        registeredServices.remove(o);
        serviceRegistrar.unregisterService(o, type); 
    }
    
    public static void executeTask(AbstractTask task){
        taskManager.execute(new TaskIterator(task)); 
    }
}