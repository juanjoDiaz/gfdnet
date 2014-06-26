package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Collection;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;

/**
 * Class used to represent each node of the GO-Tree as a possible central node
 * @author Norberto Díaz-Díaz
 */
public class RepresentationNode extends Representation{

    public RepresentationNode(GoTerm goTerm, GeneInput gen) {
        super(goTerm, gen);
    }

    public void setPath(Collection<GoTerm> path){
        for(GoTerm goTerm : path) {
            super.addNode(goTerm);
        }
    }

    @Override
    public boolean equals(Object o){
        return super.equals(o);
    }

    @Override
    public String toString(){
        String s="GoTerm: "+super.getGoTerm().getName();
        s+="\n\t\t\tRepresentationNode: "+super.getPath().toString();
        return s;
    }
}