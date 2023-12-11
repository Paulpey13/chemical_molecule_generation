package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class MoleculeUtils {

    //Dictionnaire de valence pour chaques atomes
    public static final Map<String, Integer> VALENCE_MAP = new HashMap<>();

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



}
