package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Stack;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Representation extends GOTreeNode{
    private GeneInput gene;
    private boolean selected;

    public Representation(GOTerm goTerm, GeneInput gen) {
        this(goTerm,new Stack<GOTerm>(), gen);
    }

    public Representation(GOTerm goTerm, Stack<GOTerm> path, GeneInput gene) {
        super(goTerm, path);
        this.gene = gene;
        selected = false;
    }
    
    public GeneInput getGen() {
        return gene;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public Object clone(){
        return new Representation(goTerm, (Stack)path.clone(), gene);
    }
}
