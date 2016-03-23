package org.cytoscape.gfdnet.model.businessobjects;

import java.util.List;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GenesSearchResult {
    public final List<GeneInput> found;
    public final List<String> unknown;
    
    public GenesSearchResult(List<GeneInput> found, List<String> unknown) {
        this.found = found;
        this.unknown = unknown;
    }
}
