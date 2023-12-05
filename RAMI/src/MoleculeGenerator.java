import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import java.util.Map;
import java.util.HashMap;

public class MoleculeGenerator {
    private static final Map<String, Integer> VALENCE_MAP = new HashMap<String, Integer>() {{
        put("H", 1);  // Hydrogène
        put("He", 0); // Hélium
        put("Li", 1); // Lithium
        put("Be", 2); // Béryllium
        put("B", 3);  // Bore
        put("C", 4);  // Carbone
        put("N", 3);  // Azote
        put("O", 2);  // Oxygène
        put("F", 1);  // Fluor
        put("Ne", 0); // Néon
        put("Na", 1); // Sodium
        put("Mg", 2); // Magnésium
        put("Al", 3); // Aluminium
        put("Si", 4); // Silicium
        put("P", 3);  // Phosphore
        put("S", 2);  // Soufre
        put("Cl", 1); // Chlore
        put("Ar", 0); // Argon
        put("K", 1);  // Potassium
        put("Ca", 2); // Calcium
        // ... continuez pour les autres éléments ou voir si y'a pas une meilleur solution
    }};



    public static void main(String[] args) {
        System.out.println("La valence du varbonne est : "+VALENCE_MAP.get("C"));
    }
}
