package org.cytoscape.gfdnet.model.dataaccess;

import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.GeneProduct;
import org.cytoscape.gfdnet.model.businessobjects.utils.Cache;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class DBCache {
    public static Cache<GOTerm> goTerms = new Cache<GOTerm>();
    public static Cache<GeneProduct> geneProducts = new Cache<GeneProduct>();
}