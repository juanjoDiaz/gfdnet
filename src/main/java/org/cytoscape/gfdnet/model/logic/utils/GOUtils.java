package org.cytoscape.gfdnet.model.logic.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.GraphImpl;
import org.cytoscape.gfdnet.model.businessobjects.ProgressMonitor;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;
import org.cytoscape.gfdnet.model.dataaccess.go.GeneDAO;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOUtils {
        
    /**
     * Retrieve all the genes related to a given organism from GO
     * 
     * @param organism
     * @param ontology
     * @param pm
     * @return List of genes extracted from GO
     */
    public static SortedSet<GeneInput> getGenInputs(Organism organism, String ontology, ProgressMonitor pm) {
        DataBase.openConnection();
        pm.setStatus("Retrieving genes from GO");
        SortedSet<GeneInput> genes = (SortedSet<GeneInput>) removeUnknown(GeneDAO.getGenes(organism), ontology, pm);
        DataBase.closeConnection();
        return genes;
    } 
    
    /**
     * Receives a list containing gene names and return a equivalent list containing genes
     * 
     * @param organism
     * @param ontology
     * @param geneNames List of gene names
     * @return List of genes extracted from GO
     */
    public static List<GeneInput> getGenInputs(Organism organism, String ontology, List<String> geneNames){
        List<GeneInput> genes;
        if (organism.isPreloaded()){
            genes = new LinkedList<GeneInput>();
            for (String geneName : geneNames){
                GeneInput gene = new GeneInput(geneName);
                GeneInput selectedGene = null;
                for (GeneInput preloadedGene : organism.getGenes()){
                    if(preloadedGene.equals(gene)){
                        selectedGene = preloadedGene;
                        break;
                    }
                    else if (preloadedGene.isSynonym(gene)){
                        preloadedGene.convertSynonymInName(geneName);
                        selectedGene = preloadedGene;
                    }
                }
                if(selectedGene != null) {
                    genes.add(selectedGene);
                }
            }
        }
        else {
            DataBase.openConnection();
            genes = (List<GeneInput>) removeUnknown(GeneDAO.getGenes(organism, geneNames), ontology);  
            DataBase.closeConnection();
        }
        return genes;
    }
    
    /**
     * Receives a graph containing gene names as nodes and return a equivalent graph containing genes
     * 
     * @param organism
     * @param ontology
     * @param network
     * @return Graph formed by the genes retrieved from GO
     */
    public static Graph<GeneInput> getGenInputNetwork(Organism organism, String ontology, Graph<String> network){
        List<String> nodes = network.getNodes();
        List<GeneInput> genes = GOUtils.getGenInputs(organism, ontology, nodes);
        if (genes.isEmpty()) {
            throw new IllegalArgumentException("There isn't any gene in the network that is annotated in GO.");
        }
        Graph<GeneInput> geneInputsNetwork = new GraphImpl<GeneInput>(genes.size());
        int genesSize = genes.size();
        for(int i = 0; i < genesSize; i++){
            GeneInput gene1 = genes.get(i);
            gene1.setNodeId(i);            
            geneInputsNetwork.updateNodeValue(i, gene1);
            for(int j = 0; j < i; j++){
                String gene1Name = gene1.getName();
                String gene2Name = genes.get(j).getName();
                int posI = -1;
                int posJ = -1;
                int cont = 0;
                for (String node : nodes) {
                    if (node.equalsIgnoreCase(gene1Name)){
                        posI = cont;
                    }
                    else if (node.equalsIgnoreCase(gene2Name)){
                        posJ = cont;
                    }
                    if(posI != -1 && posJ != -1){
                        break;
                    }
                    cont++;
                }
                geneInputsNetwork.addEdge(j, i, network.getEdgeWeight(posJ, posI));
            }
        }
        return geneInputsNetwork; 
    }
       
    /**
     * Receives a list genes, load the relevant section of the GOTree 
     * and remove the irrelevant genes for the analysis.
     * In this method is where the database is more heavily queried.
     * 
     * @param genes List of genes
     * @param ontology
     * @return List of genes extracted from GO
     */
    public static Collection<GeneInput> removeUnknown(Collection<GeneInput> genes, String ontology) {
        List<GeneInput> genesToRemove = new LinkedList<GeneInput>();
        for(GeneInput gene : genes){
            if(!gene.isKnown(ontology)){
                genesToRemove.add(gene);
            }
        }
        genes.removeAll(genesToRemove);
        return genes;
    }
    
    /**
     * Receives a list genes, load the relevant section of the GOTree 
     * and remove the irrelevant genes for the analysis.
     * In this method is where the database is more heavily queried.
     * 
     * @param genes List of genes
     * @param ontology
     * @return List of genes extracted from GO
     */
    public static Collection<GeneInput> removeUnknown(Collection<GeneInput> genes, String ontology, ProgressMonitor pm) {
        List<GeneInput> genesToRemove = new LinkedList<GeneInput>();
        int cont = 1;
        int genesSize = genes.size();
        for(GeneInput gene : genes){
            pm.setStatus("Loading " + gene.getName());
            if(!gene.isKnown(ontology)){
                genesToRemove.add(gene);
            }
            pm.setProgress((float)cont/genesSize);
            cont++;
        }
        genes.removeAll(genesToRemove);
        return genes;
    }
}