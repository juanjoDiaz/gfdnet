package org.cytoscape.gfdnet.model.businessobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.utils.Cache;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GOTree {          
    private GOTreeNode root;
    private final Cache<GOTreeNode> nodes;

    public GOTree(List<GeneInput> genes, Ontology ontology) {
        nodes = new Cache<GOTreeNode>();
        for (GeneInput gene : genes) {
            for (GOTerm goTerm : gene.getGoTerms(ontology)) {
                GOTreeNode annotation = nodes.getOrAdd(new GOTreeNode(this, goTerm));
                buildGOTree(annotation, gene, annotation, 0);
            }
        }
    }
    
    private BuildHelper buildGOTree(GOTreeNode node, GeneInput gene, GOTreeNode annotation, int distance) {
        node.addGeneAnnotation(gene, annotation, distance);
        BuildHelper helper = new BuildHelper();
        helper.setAncestor(node, 0);
        if (node.getGoTerm().isRoot()) {
            this.root = node;
        } else {
            for (GOTreeNode parentNode : node.getAncestors()) {
                BuildHelper ancestorHelper = buildGOTree(parentNode, gene, annotation, distance + 1);
                for (Entry<GOTreeNode, Integer> entry : ancestorHelper.getAncestors().entrySet()) {
                    helper.setAncestor(entry.getKey(), entry.getValue() + 1);
                }
                if (ancestorHelper.depth > helper.depth) {
                    helper.depth = ancestorHelper.depth;
                }
            }
        }
        helper.depth++;
        node.setDepth(helper.depth);
        node.setAllAncestors(helper.getSortedAncestors());
        return helper;
    }
    
    private class BuildHelper {
        public int depth = -1;
        
        public Map<GOTreeNode, Integer> getSortedAncestors() {
            List<Entry<GOTreeNode, Integer>> entries = new ArrayList<Map.Entry<GOTreeNode, Integer>>(ancestors.size());
            entries.addAll(ancestors.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<GOTreeNode, Integer>>() {
                @Override
                public int compare(final Map.Entry<GOTreeNode, Integer> entry1, final Map.Entry<GOTreeNode, Integer> entry2) {
                        return entry1.getValue().compareTo(entry2.getValue());
                }
            });
            ancestors = new LinkedHashMap<GOTreeNode, Integer>();
            for (Map.Entry<GOTreeNode, Integer> entry : entries) {
                ancestors.put(entry.getKey(), entry.getValue());
            }
            return ancestors;
        }
        
        private Map<GOTreeNode, Integer> ancestors =  new LinkedHashMap<GOTreeNode, Integer>();
        
        public Map<GOTreeNode, Integer> getAncestors() {
            return ancestors;
        }
        
        public void setAncestor(GOTreeNode node, int distance) {
            Integer currentDistance = ancestors.get(node);
            if (currentDistance == null || distance < currentDistance) {
                ancestors.put(node, distance);
            }
        }
    }
    
    
    public GOTreeNode getRoot() {
        return root;
    }
    
    public Cache<GOTreeNode> getCachedNodes() {
        return nodes;
    }
    
    public Set<GOTreeNode> getNodes() {
        return nodes.getAll();
    }
    
    public Map<GeneInput, GOTreeNode> getClosestAnnotationsToLCA(GOTreeNode node) {
        return node.getClosestAnnotations();
    }
    
    public GOTreeNode getLCA(GOTreeNode annotation1, GOTreeNode annotation2) {
        if (annotation1.equals(annotation2)) {
            return annotation1;
        }

        if (annotation1.getDepth() < annotation2.getDepth() || 
                (annotation1.getDepth() == annotation2.getDepth() &&
                    annotation1.getGoTerm().getName().compareTo(annotation2.getGoTerm().getName()) < 0)
            ) {
           return annotation1.getLCA(annotation2);
        }
        else {
            return annotation2.getLCA(annotation1);
        }
    }
}
