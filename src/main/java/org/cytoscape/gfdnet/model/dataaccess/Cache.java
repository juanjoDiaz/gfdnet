package org.cytoscape.gfdnet.model.dataaccess;

import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.logic.utils.CollectionUtil;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Cache {
    private static final SortedSet<GOTerm> loadedGoTerms = new TreeSet();
    
    public static GOTerm getGoTerm(GOTerm goTerm) {
        return CollectionUtil.search(loadedGoTerms, goTerm);
    }

    public static void addGoTerm(GOTerm goTerm) {
        loadedGoTerms.add(goTerm);
    }
    
    public static GOTerm getOrAddGoTerm(GOTerm goTerm) {
        GOTerm cachedGOTerm = getGoTerm(goTerm);
        if (cachedGOTerm != null) {
            return cachedGOTerm;
        }
        else {
            addGoTerm(goTerm);
            return goTerm;
        }
    }
}