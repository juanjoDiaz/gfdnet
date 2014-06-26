package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.logic.utils.CollectionUtil;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Gene implements Comparable{
    private String name;
    private final SortedSet<String> synonyms;
    private final SortedSet<GeneProduct> geneProducts;
    private final SortedSet<GoTerm> goTerms;
    
    public Gene(String name) {
        this.name = name;
        synonyms = new TreeSet();
        geneProducts = new TreeSet();
        goTerms = new TreeSet();
    }
    
    public String getName() {
        return name;
    }
    
    public void convertSynonymInName(String synonym){
        if (synonyms.remove(synonym)){
            synonyms.add(name);
            name = synonym;
        }
    }

    public void addSynonym(String synonym){
        synonyms.add(synonym);
    }
    
    public boolean isSynonym(String geneName) {
        return this.name == null
                ? geneName == null 
                : (this.name.equalsIgnoreCase(geneName) || CollectionUtil.search(synonyms, geneName) != null);
    }

    public boolean isSynonym(Gene gene) {
        return this.name == null
                ? gene.name == null 
                : (this.name.equalsIgnoreCase(gene.name) || CollectionUtil.search(synonyms, gene.name) != null);
    }
    
    public void addGeneProduct(GeneProduct geneProduct) {
        geneProducts.add(geneProduct);
    }
    
    protected SortedSet<GoTerm> getGoTerms(String ontology) {
        if (goTerms.isEmpty()){
            for(GeneProduct geneProduct : geneProducts){
                goTerms.addAll(geneProduct.getGoTerms(ontology));
            }
        }
        return goTerms;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Gene other = (Gene) obj;
        return (this.name == null) ? (other.name == null) : this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        return this.name.compareToIgnoreCase(((Gene)o).name);
    }

    @Override
     public String toString(){
         return name;
     }

     public String getInformationRelated(){
        String s="Name: "+name;
        s+="\nGenes synonyms:"+synonyms;
        s+="\nGeneProduct related information";
        for(GeneProduct gp:geneProducts){
            s+="\n\tId:"+gp.getId();
            s+="\n\tDescription:"+gp.getDescription();
            s+="\n\tGo-Terms related:" + gp.getGoTerms(name).toString();
        }
        return s;
    }
}
