package org.cytoscape.gfdnet.model.businessobjects.go;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Ontology {
    public static String MF="molecular_function",
                         BP="biological_process",
                         CC="cellular_component";
    
    public static boolean isOntology(String string) {
        return string != null && (string.equals(Ontology.BP) ||string.equals(Ontology.MF)||string.equals(Ontology.CC));
    }
}