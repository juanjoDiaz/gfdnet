package org.cytoscape.gfdnet.model.logic.utils;

import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.GraphImpl;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.go.GeneDAO;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOUtils {
    
    /**
     * Receives a graph containing gene names as nodes and return a equivalent graph containing genes
     * 
     * @param network
     * @param organism
     * @param ontology
     * @return Graph formed by the genes retrieved from GO
     */
    public static Graph<GeneInput> getGenInputNetwork(Graph<String> network, Organism organism, String ontology){
        List<String> nodes = network.getNodes();
        List<GeneInput> genes = GOUtils.getGenInputs(nodes, organism, ontology);
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
     * Receives a list containing gene names and return a equivalent list containing genes
     * 
     * @param geneNames List of gene names
     * @param organism
     * @param ontology
     * @return List of genes extracted from GO
     */
    public static List<GeneInput> getGenInputs(List<String> geneNames, Organism organism, String ontology){
        List<GeneInput> genes;
        if (organism.isPreloaded()){
            genes = new LinkedList<GeneInput>();
            for (String geneName : geneNames){
                GeneInput gene = new GeneInput(geneName);
                GeneInput selectedGene = null;
                for (GeneInput preloadGene : organism.getGenes()){
                    if(preloadGene.equals(gene)){
                        selectedGene = preloadGene;
                        break;
                    }
                    else if (preloadGene.isSynonym(gene)){
                        preloadGene.convertSynonymInName(geneName);
                        selectedGene = preloadGene;
                    }
                }
                if(selectedGene != null) {
                    genes.add(selectedGene);
                }
            }
        }
        else {
            genes = GeneDAO.getGenes(organism, geneNames);
        }
        List<GeneInput> genesToRemove = new LinkedList<GeneInput>();
        for(GeneInput gene : genes){
            if(!gene.isKnown(ontology)){
                genesToRemove.add(gene);
            }
        }
        genes.removeAll(genesToRemove);
        return genes;
    }
}