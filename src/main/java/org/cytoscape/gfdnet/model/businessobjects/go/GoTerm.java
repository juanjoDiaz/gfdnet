package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.dataaccess.go.RelationshipDAO;
import org.cytoscape.gfdnet.model.logic.utils.Cache;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GoTerm implements Comparable{
    private final int id;
    private final String name;
    private final String description;
    private final String ontology;

    private final SortedSet<GeneProduct> geneProducts;
    private final List<Relationship> ancestors;
    private final List<Relationship> children;

    public GoTerm(int id, String name, String description, String ontology) {
        this.id=id;
        this.name = name;
        this.description=description;
        this.ontology=ontology;
        this.geneProducts=new TreeSet();
        this.ancestors=new ArrayList();
        this.children=new ArrayList();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
    
    public void loadAncestors(){
        List<Relationship> relations = RelationshipDAO.loadAncestors(id, ontology);
        for(Relationship relation : relations){
            GoTerm gtParent = relation.getGoTerm();
            GoTerm cachedGTParent = Cache.getGoTerm(gtParent);
            if(cachedGTParent != null){
                relation.setGoTerm(cachedGTParent);
            } else {
                if (!gtParent.isRoot()){
                    gtParent.loadAncestors();
                    Cache.addGoTerm(gtParent);
                }
            }
            addAncestor(relation);
        }
    }
    
    public boolean isRoot(){
        return Ontology.isOntology(description);
    }
    
    public List<Relationship> getAncestors() {
        return Collections.unmodifiableList(ancestors);
    }

    public List<Relationship> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean addAncestor(Relationship relation){
        boolean result=ancestors.add(relation);
        result&=addChildren(relation);
        return result;
    }

    private boolean addChildren(Relationship relation){
        Relationship rel=new Relationship(-1,relation.getRelationshipType(),this);
        return relation.getGoTerm().children.add(rel);
    }

    @Override
    public int compareTo(Object o) {
        return id-((GoTerm)o).id;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GoTerm other = (GoTerm) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getInformationRelated(){
        String s="Id:"+getName();
        s+="\nDescription:"+getDescription();
        s+="\nOntology:"+ ontology;
        s+="\nGeneProduct related information "+"//TODO";
        for(GeneProduct gp:geneProducts){
            s+="\n\tId:"+gp.getId();
            s+="\n\tDescription:"+gp.getDescription();
            s+="\n\tGenes related:";
        }
        return s;
    }   
}
