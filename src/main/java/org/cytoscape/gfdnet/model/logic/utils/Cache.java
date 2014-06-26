package org.cytoscape.gfdnet.model.logic.utils;

import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Cache {
    private static final SortedSet<GoTerm> loadedGoTerms = new TreeSet();
    
    public static GoTerm getGoTerm(GoTerm goTerm) {
        return CollectionUtil.search(loadedGoTerms, goTerm);
    }

    public static void addGoTerm(GoTerm goTerm) {
        loadedGoTerms.add(goTerm);
    }
}