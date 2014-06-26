package org.cytoscape.gfdnet.model.businessobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.go.Gene;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Relationship;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneInput extends Gene{
    private int nodeId;
    private final List<Representation> representations;
    private String loadedOntology;
    
    public GeneInput(String name) {
        super(name);
        representations = new LinkedList<Representation>();
    }
    
    public GeneInput(String name, int nodeId) {
        super(name);
        representations = new LinkedList<Representation>();
    }
    
    
    public boolean isKnown(String ontology){
        return !getRepresentations(ontology).isEmpty();
    }
    
    public String getLoadedOntology(){
        return loadedOntology;
    }
    
    public List<Representation> getRepresentations(){
        if(representations.isEmpty() || loadedOntology == null){
            throw new InternalError("No ontology has been loaded.");
        }
        return Collections.unmodifiableList(representations);
    }

    public List<Representation> getRepresentations(String ontology){
        if(representations.isEmpty() || !loadedOntology.equals(ontology)){
            loadedOntology = ontology;
            representations.clear();
            representations.addAll(loadRepresentations(ontology));
        }
        return Collections.unmodifiableList(representations);
    }
    
    public Representation getRepresentationSelected(){
        if(representations==null){
            return null;
        }
        for (Representation representation : representations) {
            if (representation.isSelected()){
                return representation;
            }
        }
        return null;
    }
    
    public void clearRepresentationSelected(){
        if(representations!=null){
            for (Representation representation : representations) {
                if (representation.isSelected()){
                    representation.setSelected(false);
                }
            }
        }
    }
    
    private List<Representation> loadRepresentations(String ontology){
        List<Representation> representaciones = new ArrayList();
        for (GoTerm goTerm : getGoTerms(ontology)) {
            Representation representacion = new Representation(goTerm, this);
            createRepresentations(goTerm, representacion, representaciones);
        }
        return representaciones;
    }
    
    private static void createRepresentations(GoTerm goTermPadre, Representation r, List<Representation> representaciones) {
        r.addNode(goTermPadre);
        if (!goTermPadre.isRoot()) {
        List<Relationship> relacionesAncestrales=new ArrayList();
            for(Relationship relation : goTermPadre.getAncestors()) {
                if(relation.getRelationshipType().equals(Relationship.is_a)) {
                    relacionesAncestrales.add(relation);
                }
            }
            if (relacionesAncestrales.size() > 1) {
                for(Relationship relation:relacionesAncestrales) {
                    createRepresentations(relation.getGoTerm(),(Representation)r.clone(),representaciones);
                }
            } else if (relacionesAncestrales.size() == 1) {
                createRepresentations(relacionesAncestrales.get(0).getGoTerm(), r, representaciones);
            }
        } else {
            representaciones.add(r);
        }
    }
    
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}