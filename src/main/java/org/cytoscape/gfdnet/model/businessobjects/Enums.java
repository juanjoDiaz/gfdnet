package org.cytoscape.gfdnet.model.businessobjects;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Enums {
    public enum Ontology {
        BP("Biological Proccess", "biological_process"),
        CC("Cellular Component", "cellular_component"),
        MF("Molecular Function", "molecular_function");
        
        private Ontology(String description, String dbString){
            this.description = description;
            this.dbString = dbString;
        }

        private final String description;
        private final String dbString;

        public String getDescription(){ return description; }
        public String getDBString(){ return dbString; }
        
        public static boolean isOntology(String string) {          
            for(Ontology v : values()) {
                if(v.getDBString().equalsIgnoreCase(string)) {
                    return true;
                }
            }
            return false;
        }
        
        public static Ontology getEnum (String string) {
            try {
                return valueOf(string);
            } catch (IllegalArgumentException e){
                for(Ontology v : values()) {
                    if(v.getDescription().equalsIgnoreCase(string) || v.getDBString().equalsIgnoreCase(string)) {
                        return v;
                    }
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    public enum RelationshipType {
        is_a("is_a"),
        part_of("part_of"),
        regulate("regulates"),
        positive_regulate("positively_regulates"), 
        negative_regulate("negatively_regulates"),
        occurs_in("occurs_in");
        
        private RelationshipType(String dbString){
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDBString(){ return dbString; }
    }
}
