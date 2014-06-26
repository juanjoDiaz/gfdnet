package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Stack;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Representation implements Cloneable{
    private GeneInput gene; //indica el gen al que corresponde la representacion
    private GoTerm goTerm;
    private Stack<GoTerm> path;
    private boolean selected;//indica si esta representación ha sido seleccionada como representante del gen en alguna ontologia

    public Representation(GoTerm goTerm, GeneInput gen) {
        this(goTerm,new Stack<GoTerm>(), gen);
    }

    public Representation(GoTerm goTerm, Stack<GoTerm> path, GeneInput gene) {
        this.goTerm = goTerm;
        this.path = path;
        this.gene = gene;
        selected = false;
    }

    /**
     * At  node to the representation path
     * 
     * @param goTerm
     * @return
     */
    public boolean addNode(GoTerm goTerm){
        return path.add(goTerm);
    }

    public Stack<GoTerm> getPath() {
        return path;
    }
    
    public GeneInput getGen() {
        return gene;
    }

    public GoTerm getGoTerm() {
        return goTerm;
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

    @Override
    public String toString(){
        String s="GoTerm: " + goTerm.getName();
        s+="\n\t\t\tRepresentation: " + path.toString();
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Representation other = (Representation) obj;
        if (this.goTerm != other.goTerm && (this.goTerm == null || !this.goTerm.equals(other.goTerm))) {
            return false;
        }
        if (this.path != other.path && (this.path == null || !this.path.equals(other.path))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.goTerm != null ? this.goTerm.hashCode() : 0);
        hash = 79 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }
}
