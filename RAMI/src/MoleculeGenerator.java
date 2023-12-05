import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;


public class MoleculeGenerator {

    //Dictionnaire de valence pour chaques atomes
    private static final Map<String, Integer> VALENCE_MAP = new HashMap<>();

    static {
        addToMap(new String[]{"He", "Ne", "Ar"}, 0);
        addToMap(new String[]{"Li", "Na", "K","H", "F", "Cl"}, 1);
        addToMap(new String[]{"Be", "Mg", "Ca","O", "S", "Se"}, 2);
        addToMap(new String[]{"B", "Al", "Ga","N", "P", "As"}, 3);
        addToMap(new String[]{"C", "Si", "Ge"}, 4);
        //Y'a pas tout il faut finir de rajouter

        //La contrainte devra donc faire respecter chaque atome et sa valeur dans ce dico
    }

    //Pour ajouter des elements au dico de valence sans faire 10000lignes
    private static void addToMap(String[] elements, int valence) {
        for (String element : elements) {
            VALENCE_MAP.put(element, valence);
        }
    }

    public static void main(String[] args) {
        System.out.println("La valence du carbonne est : "+VALENCE_MAP.get("C"));
    }
}
