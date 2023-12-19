package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import src.BondDistance;


public class MoleculeUtils {

    //Dictionnaire de valence pour chaques atomes
    public static final Map<String, Integer> VALENCE_MAP = new HashMap<>();
    //Dictionnaire de valence pour chaques atomes

    public static final Map<String, BondDistance> BOND_DISTANCES = new HashMap<>();


    static {
        addToMap(new String[]{"He", "Ne", "Ar"}, 0);
        addToMap(new String[]{"Li", "Na", "K","H", "F", "Cl"}, 1);
        addToMap(new String[]{"Be", "Mg", "Ca","O", "S", "Se"}, 2);
        addToMap(new String[]{"B", "Al", "Ga","N", "P", "As"}, 3);
        addToMap(new String[]{"C", "Si", "Ge"}, 4);
        //Y'a pas tout il faut finir de rajouter

        //La contrainte devra donc faire respecter chaque atome et sa valeur dans ce dico


        //Distances :
        try {
            loadBondDistances("./RAMI/data/distances");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Pour ajouter des elements au dico de valence sans faire 10000lignes
    private static void addToMap(String[] elements, int valence) {
        for (String element : elements) {
            VALENCE_MAP.put(element, valence);
        }
    }

    //Sert à lire le fichier des ditances distance.txt
    //Retourne pour les liaisons avec un interval :
    //  getMinDistance() la valeur la plus petite
    //  getMaxDistance() la valeur la plus grande
    //Pour les liaions sans interval :
    //  getMinDistance() valeur-1
    //  getMaxDistance() valeur+1
    private static void loadBondDistances(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\s+");
                String[] atoms = parts[0].split("-");
                String[] distanceRange = parts[1].split("–");
                int minDistance;
                int maxDistance;

                if (distanceRange.length == 2) {
                    //Si un interval de var est fourni :
                    minDistance = Integer.parseInt(distanceRange[0]);
                    maxDistance = Integer.parseInt(distanceRange[1]);

                } else {
                    //Si y'a une seule variable fournie
                    minDistance = Integer.parseInt(distanceRange[0]) - 1;
                    maxDistance = Integer.parseInt(distanceRange[0]) + 1;
                }

                BondDistance bondDistance = new BondDistance(minDistance, maxDistance);
                BOND_DISTANCES.put(atoms[0] + "-" + atoms[1], bondDistance);
                BOND_DISTANCES.put(atoms[1] + "-" + atoms[0], bondDistance); // Stocker la distance dans les deux sens
            }
        }
    }


    public static BondDistance getBondDistance(String atom1, String atom2) {
        return BOND_DISTANCES.getOrDefault(atom1 + "-" + atom2, new BondDistance(-1, -1));
    }
}


/**
 *
 * Usage bond getBondDistance :
 *
 * System.out.println(getBondDistance("C","O").getMinDistance()); Valeur inf de l'interval
 * System.out.println(getBondDistance("C","O").getMaxDistance()); Valeur sup de l'interval
 *
 *
 *
 *
 */