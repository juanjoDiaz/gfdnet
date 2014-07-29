package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Gene;
import org.cytoscape.gfdnet.model.businessobjects.go.Relationship;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneInput extends Gene{
    private int nodeId;
    private List<Representation> representations;
    
    public GeneInput(String name) {
        super(name);
    }
    
    public GeneInput(String name, int nodeId) {
        super(name);
        representations = new LinkedList<Representation>();
    }
    
    
    public boolean isKnown(String ontology){
        return !getRepresentations(ontology).isEmpty();
    }

    public List<Representation> getRepresentations(String ontology){
        if(representations == null || !(this.loadedOntology == null ? ontology == null : this.loadedOntology.equals(ontology))){
            representations = loadRepresentations(ontology);
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
        List<Representation> representationList = new LinkedList();
        for (GOTerm goTerm : getGoTerms(ontology)) {
            Representation representacion = new Representation(goTerm, this);
            createRepresentations(goTerm, representacion, representationList);
        }
        return representationList;
    }
    
    private static void createRepresentations(GOTerm parentGOTerm, Representation r, List<Representation> representations) {
        r.addNode(parentGOTerm);
        if (!parentGOTerm.isRoot()) {
            List<Relationship> ancestors = parentGOTerm.getAncestors();
            if (ancestors.size() == 1) {
                createRepresentations(ancestors.get(0).getGoTerm(), r, representations);
            } else {
                for(Relationship ancestor : ancestors) {
                    createRepresentations(ancestor.getGoTerm(),(Representation)r.clone(),representations);
                }
            }
        } else {
            representations.add(r);
        }
    }
    
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}