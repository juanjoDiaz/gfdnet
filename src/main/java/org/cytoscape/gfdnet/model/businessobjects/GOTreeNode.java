package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Collection;
import java.util.Stack;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;

/**
 * Class used to represent each node of the GO-Tree as a possible central node
 * @author Norberto Díaz-Díaz
 */
public class GOTreeNode implements Cloneable{
    protected GOTerm goTerm;
    protected Stack<GOTerm> path;

    public GOTreeNode(GOTerm goTerm, Collection<GOTerm> path) {
        this.goTerm = goTerm;
        this.path = new Stack<GOTerm>();
        this.path.addAll(path);
    }
    
    public GOTerm getGoTerm() {
        return goTerm;
    }
    
    /**
     * At  node to the representation path
     * 
     * @param goTerm
     * @return
     */
    public boolean addNode(GOTerm goTerm){
        return path.add(goTerm);
    }

    public Stack<GOTerm> getPath() {
        return path;
    }
    
    @Override
    public Object clone(){
        return new GOTreeNode(goTerm, (Stack)path.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        GOTreeNode other = (GOTreeNode) obj;
        if (this.goTerm != other.goTerm && (this.goTerm == null || !this.goTerm.equals(other.goTerm))) {
            return false;
        }
        if (this.path != other.path && (this.path == null || !this.path.equals(other.path))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s="GoTerm: " + goTerm.getName();
        s+="\n\t\t\tRepresentation: " + path.toString();
        return s;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.goTerm != null ? this.goTerm.hashCode() : 0);
        hash = 79 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }
}