package org.cytoscape.gfdnet.model.businessobjects;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Relationship;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GOTreeNode implements Comparable{
    private final GOTree goTree;
    private final GOTerm goTerm;
    private int depth;
    private final List<GOTreeNode> ancestors;
    private final List<GOTreeNode> children;
    // GO Tree Topology Cache
    private Map<GOTreeNode, Integer> allAncestors;
    private final Map<GeneInput, Set<GOTreeNode>> geneAnnotations;
    private final Map<GOTreeNode, Integer> annotationsDistances;
    // Similarity Cache
    private final Map<GeneInput, GOTreeNode> closestAnnotations;
    private final Map<GOTreeNode, GOTreeNode> cachedLCAs;
    
    public GOTreeNode(GOTree goTree, GOTerm goTerm) {
        this.goTree = goTree;
        this.goTerm = goTerm;
        this.depth = -1;
        this.ancestors = new  LinkedList<GOTreeNode>();
        this.children = new  LinkedList<GOTreeNode>();
        
        this.geneAnnotations = new HashMap<GeneInput, Set<GOTreeNode>>();
        this.annotationsDistances = new HashMap<GOTreeNode, Integer>();
        
        this.closestAnnotations = new HashMap<GeneInput, GOTreeNode>();
        this.cachedLCAs = new HashMap<GOTreeNode, GOTreeNode>();
    }
    
    public GOTerm getGoTerm() {
        return goTerm;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    

    public List<GOTreeNode> getAncestors() {
        if (ancestors.isEmpty()) {
            for (Relationship relationship : goTerm.getAncestors()) {
                GOTreeNode parentNode = goTree.getCachedNodes().getOrAdd(new GOTreeNode(goTree, relationship.getGoTerm()));
                addAncestor(parentNode);
            }
        }
        return ancestors;
    }

    public List<GOTreeNode> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    private boolean addAncestor(GOTreeNode node){
        boolean result = ancestors.add(node);
        result &= node.addChildren(this);
        return result;
    }
    
    private boolean addChildren(GOTreeNode node){
        return this.children.add(node);
    }


    public Map<GOTreeNode, Integer> getAllAncestors() {
        return allAncestors;
    }
    
    public void setAllAncestors(Map<GOTreeNode, Integer> nodes) {
        allAncestors = nodes;
    }

    public void addGeneAnnotation(GeneInput gene, GOTreeNode annotation, int distance) {
        if  (!geneAnnotations.containsKey(gene)) {
            geneAnnotations.put(gene, new HashSet<GOTreeNode>());
        }
        geneAnnotations.get(gene).add(annotation);
        Integer currentDistance = annotationsDistances.get(annotation);
        if (currentDistance == null || distance < currentDistance) {
            annotationsDistances.put(annotation, distance);
        }
    }
    
    public Set<GOTreeNode> getAnnotations() {
        return annotationsDistances.keySet();
    }
        
    public Map<GeneInput, Set<GOTreeNode>> getGeneAnnotations() {
        return geneAnnotations;
    }
    
    public Set<GOTreeNode> getGeneAnnotations(GeneInput gene) {
        return geneAnnotations.get(gene);
    }
    
    public Map<GeneInput, Integer> getGeneAnnotationsCount() {
        Map<GeneInput, Integer>  annotationsCount = new HashMap<GeneInput, Integer>();
        for (Map.Entry<GeneInput, Set<GOTreeNode>> entry : geneAnnotations.entrySet()) {
            annotationsCount.put(entry.getKey(), entry.getValue().size());
        }
        return annotationsCount;
    }
    
    public int getGeneAnnotationsCount(GeneInput gene) {
        return geneAnnotations.get(gene).size();
    }
    
    public int getDistanceToAnnotation(GOTreeNode annotation) {
        return annotationsDistances.get(annotation);
    }
    

    public synchronized Map<GeneInput, GOTreeNode> getClosestAnnotations() {
        if (closestAnnotations.isEmpty()) {
            Set<GeneInput> genes = goTree.getRoot().getGeneAnnotations().keySet();
            for (GeneInput gene : genes) {
                GOTreeNode leave = null;
                int distanceNodeToLeave = Integer.MAX_VALUE;
                double similarityNodeToLeave = Double.MAX_VALUE;
                // Check annotations below the node
                Set<GOTreeNode> leaves = getGeneAnnotations(gene);
                if (leaves != null){
                    for (GOTreeNode candidateLeave : leaves) {
                        int distanceNodeToCandidateLeave = getDistanceToAnnotation(candidateLeave);
                        if (distanceNodeToCandidateLeave < distanceNodeToLeave || 
                                (distanceNodeToCandidateLeave == distanceNodeToLeave && 
                                    (candidateLeave.getDepth() > leave.getDepth() ||
                                        (candidateLeave.getDepth() == leave.getDepth() &&
                                            candidateLeave.getGoTerm().getName().compareTo(leave.getGoTerm().getName()) < 0
                                        )
                                    )
                                )
                            ) {
                            leave = candidateLeave;
                            distanceNodeToLeave = distanceNodeToCandidateLeave;
                        }
                    }
                    distanceNodeToLeave++;
                    similarityNodeToLeave = (double)distanceNodeToLeave/(2*depth + distanceNodeToLeave + 1);
                }
                // Check annotations over the node
                int bestDistanceNodeToCALeave = Integer.MAX_VALUE;
                for (Map.Entry<GOTreeNode, Integer> ancestorEntry : allAncestors.entrySet()) {
                    GOTreeNode ca = ancestorEntry.getKey();
                    int distanceNodeToCA = ancestorEntry.getValue();
                    if  (distanceNodeToCA >= bestDistanceNodeToCALeave) {
                        break;
                    }
                    Set<GOTreeNode> caLeaves = ca.getGeneAnnotations(gene);
                    if (caLeaves != null) {                   
                        GOTreeNode caLeave = null;
                        int distanceCAToCALeave = Integer.MAX_VALUE;
                        for (GOTreeNode candidateLeave : caLeaves) {  
                            int distanceCAToCandidateLeave = ca.getDistanceToAnnotation(candidateLeave);
                            if (distanceCAToCandidateLeave < distanceCAToCALeave) {
                                caLeave = candidateLeave;
                                distanceCAToCALeave = distanceCAToCandidateLeave;
                            }
                        }

                        int distanceNodeToCALeave = distanceNodeToCA + distanceCAToCALeave + 1;
                        double similarityNodeToCALeave = (double)distanceNodeToCALeave/(2*ca.getDepth() + distanceNodeToCALeave + 1);
                        
                        if (similarityNodeToCALeave < similarityNodeToLeave || 
                                (similarityNodeToCALeave == similarityNodeToLeave && 
                                    (caLeave.getDepth() > leave.getDepth() ||
                                        (caLeave.getDepth() == leave.getDepth() &&
                                            caLeave.getGoTerm().getName().compareTo(leave.getGoTerm().getName()) < 0
                                        )
                                    )
                                )
                            ) {
                            leave = caLeave;
                            similarityNodeToLeave = similarityNodeToCALeave;
                            bestDistanceNodeToCALeave = distanceNodeToCALeave;
                        }
                    }
                }
                closestAnnotations.put(gene, leave);
            }
        }
        return closestAnnotations;
    }
       
    public synchronized GOTreeNode getLCA(GOTreeNode annotation) {
        GOTreeNode lca = cachedLCAs.get(annotation);
        if (lca == null) {
            lca = goTree.getRoot();
            int lcaDepth = goTree.getRoot().getDepth();
            int distanceLCAToAnnotation = lca.getDistanceToAnnotation(annotation);
            Queue<GOTreeNode> pending = new ArrayDeque<GOTreeNode>();
            pending.add(this);

            while (!pending.isEmpty()) {
                GOTreeNode node = pending.poll();
                if (node.getDepth() > lcaDepth) {
                    if (node.getAnnotations().contains(annotation)) {
                        lca = node;
                        lcaDepth = node.getDepth();
                        distanceLCAToAnnotation = node.getDistanceToAnnotation(annotation);
                    }
                    for (GOTreeNode ancestor : node.getAncestors()) {
                        if (ancestor.getDepth() >= lcaDepth) {
                            pending.add(ancestor);
                        }
                    }
                }
                else if (node.getDepth() == lcaDepth) {
                    if (node.getAnnotations().contains(annotation)) {
                        int distanceLCAToCandidateAnnotation = node.getDistanceToAnnotation(annotation);
                        if (distanceLCAToCandidateAnnotation < distanceLCAToAnnotation) {
                            lca = node;
                            lcaDepth = node.getDepth();
                            distanceLCAToAnnotation = distanceLCAToCandidateAnnotation;
                        }
                    }
                }
            }
        }
        cachedLCAs.put(annotation, lca);
        return lca;
    }
    
    @Override
    public int compareTo(Object o) {
        return goTerm.getId()-((GOTreeNode)o).goTerm.getId();
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
        return this.goTerm == other.goTerm || (this.goTerm != null && this.goTerm.equals(other.goTerm));
    }

    @Override
    public String toString() {
        String s = "GoTreeNode: " + goTerm.getName();
        return s;
    }
    
    @Override
    public int hashCode() {
        return goTerm.hashCode();
    }
}