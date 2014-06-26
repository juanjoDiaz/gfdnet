package org.cytoscape.gfdnet.controller;

import org.cytoscape.gfdnet.controller.tasks.ExecuteGFDnetTask;
import org.cytoscape.gfdnet.controller.tasks.PreloadOrganismTask;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.controller.utils.NetworkAdapter;
import org.cytoscape.gfdnet.controller.utils.OSGiManager;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.go.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class CoreController {
    private String URL;
    private String user;
    private String password;
    private Graph<String> graph;
    private Organism organism;
    private String ontology;
    
    private NetworkViewController networkView;
    private ToolBarController toolbar;
    private ResultPanelsController resultPanels;

    public CoreController() throws Exception{
        URL = "localhost/GO";
        user = "root";
        password = "root";
        networkView = new NetworkViewController();
        graph = NetworkAdapter.IncomingCyNetworkToGraph(networkView.getNetworkView().getModel());
        ontology = Ontology.BP;
        toolbar = new ToolBarController(this);
    }
    
    public void dispose(){
        networkView.dispose();
        toolbar.dispose(); 
        if (resultPanels != null){
            resultPanels.dispose();
        }
    }
    
    public String getURL(){
        return URL;
    }
    
    public String getUser(){
        return user;
    }
    
    public String getPassword(){
        return password;
    }
   
    public void configurateDB(String url, String user, String password){
        try{
            DataBase.setConnection(url, user, password);
            DataBase.testConnection();
            toolbar.setOrganismButtonEnabled(true);
            CySwing.displayPopUpMessage("Database connection successfully set!");
        }
        catch(Exception ex){
            CySwing.displayPopUpMessage(ex.getMessage());
            toolbar.setOrganismButtonEnabled(false);
            toolbar.executeButtonEnabled(false);
        }
    }
   
    public void setOntology(String o){
        try{
            if(!ontology.equals(o)){
                ontology = o;
                reset();
            }
            CySwing.displayPopUpMessage("Ontology successfully set!");
        }
        catch(Exception ex){
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    }
    
    public void setOrganism(String genus, String species, boolean preload){
        if(organism == null || (!organism.getGenus().equals(genus) && !organism.getSpecies().equals(species))){
            organism = new Organism(genus, species);
        }
        if (preload && !organism.isPreloaded()){
            OSGiManager.executeTask(new PreloadOrganismTask(organism, ontology, this));
        }
        else {
            reset();
            CySwing.displayPopUpMessage("Orgamism successfully set!");
        }
    }

    public void executeGFDnet(){
        OSGiManager.executeTask(new ExecuteGFDnetTask(graph, organism, ontology, this));
    }
    
    public void refresh(){
        try{
            if (resultPanels != null){
                resultPanels.dispose();
            }
            networkView.dispose();
            networkView = new NetworkViewController();
            graph = NetworkAdapter.IncomingCyNetworkToGraph(networkView.getNetworkView().getModel());
            toolbar.executeButtonEnabled(true);
        }
        catch(Exception ex){
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    }
    
    public void reset(){
        if (resultPanels != null){
            resultPanels.dispose();
        }
        networkView.restoreNetwork();
        toolbar.executeButtonEnabled(true);
    }
    
    public void showResults(GFDnetResult result){
        resultPanels = new ResultPanelsController(result, networkView);        
    }
    
    public ToolBarController getToolbar(){
        return toolbar;
    }
}