package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe utilitaire gère les informations sur les molécules, y compris la valence des atomes et les distances de liaison.
 */
public class MoleculeUtils {

    // Dictionnaire de valence pour chaque atome
    public static final Map<String, Integer> VALENCE_MAP = new HashMap<>();

    // Dictionnaire de distances de liaison entre les atomes
    public static final Map<String, BondDistance> BOND_DISTANCES = new HashMap<>();

    static {
        addToValenceMap(new String[]{"He", "Ne", "Ar"}, 0);
        addToValenceMap(new String[]{"Li", "Na", "K", "H", "F", "Cl"}, 1);
        addToValenceMap(new String[]{"Be", "Mg", "Ca", "O", "S", "Se"}, 2);
        addToValenceMap(new String[]{"B", "Al", "Ga", "N", "P", "As"}, 3);
        addToValenceMap(new String[]{"C", "Si", "Ge"}, 4);

        // Vous pouvez ajouter d'autres éléments ici

        // Charger les distances de liaison depuis un fichier
        try {
            loadBondDistances("./RAMI/data/distances");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ajoute des éléments au dictionnaire de valence.
     *
     * @param elements Liste des éléments chimiques.
     * @param valence  Valeur de valence correspondante.
     */
    private static void addToValenceMap(String[] elements, int valence) {
        for (String element : elements) {
            VALENCE_MAP.put(element, valence);
        }
    }

    /**
     * Charge les distances de liaison depuis un fichier.
     *
     * @param fileName Le chemin du fichier de distances.
     * @throws IOException En cas d'erreur de lecture du fichier.
     */
    private static void loadBondDistances(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String[] atoms;
                String[] distanceRange;
                int type;

                if (line.contains("#")) { // Liaisons simples
                    atoms = parts[0].split("#");
                    distanceRange = parts[1].split("–");
                    type = 3;
                } else if (line.contains("=")) { // Liaisons doubles
                    atoms = parts[0].split("=");
                    distanceRange = parts[1].split("–");
                    type = 2;
                } else { // Liaisons triples
                    atoms = parts[0].split("-");
                    distanceRange = parts[1].split("–");
                    type = 1;
                }

                int minDistance;
                int maxDistance;

                if (distanceRange.length == 2) {
                    // Si un intervalle de distance est fourni
                    minDistance = Integer.parseInt(distanceRange[0]);
                    maxDistance = Integer.parseInt(distanceRange[1]);
                } else {
                    // Si une seule distance est fournie
                    minDistance = Integer.parseInt(distanceRange[0]) - 1;
                    maxDistance = Integer.parseInt(distanceRange[0]) + 1;
                }

                BondDistance bondDistance = new BondDistance(minDistance, maxDistance, type);

                if (type == 1) {
                    BOND_DISTANCES.put(atoms[0] + "-" + atoms[1], bondDistance);
                    BOND_DISTANCES.put(atoms[1] + "-" + atoms[0], bondDistance); // Stocker la distance dans les deux sens
                } else if (type == 2) {
                    BOND_DISTANCES.put(atoms[0] + "=" + atoms[1], bondDistance);
                    BOND_DISTANCES.put(atoms[1] + "=" + atoms[0], bondDistance); // Stocker la distance dans les deux sens
                } else {
                    BOND_DISTANCES.put(atoms[0] + "#" + atoms[1], bondDistance);
                    BOND_DISTANCES.put(atoms[1] + "#" + atoms[0], bondDistance); // Stocker la distance dans les deux sens
                }
            }
        }
    }

    /**
     * Récupère la distance de liaison entre deux atomes donnés.
     *
     * @param atom1 Le premier atome.
     * @param atom2 Le deuxième atome.
     * @param type  Le type de liaison (1 = simple, 2 = double, 3 = triple).
     * @return La distance de liaison entre les deux atomes ou une distance par défaut si non trouvée.
     */
    public static BondDistance getBondDistance(String atom1, String atom2, int type) {
        if (type == 1) {
            return BOND_DISTANCES.getOrDefault(atom1 + "-" + atom2, new BondDistance(-1, -1, 1));
        } else if (type == 2) {
            return BOND_DISTANCES.getOrDefault(atom1 + "=" + atom2, new BondDistance(-1, -1, 2));
        } else {
            return BOND_DISTANCES.getOrDefault(atom1 + "#" + atom2, new BondDistance(-1, -1, 3));
        }
    }
}

/**
 * Usage pour obtenir la distance de liaison :
 *
 * System.out.println(getBondDistance("C", "O", 1).getMinDistance()); // Valeur minimale de l'intervalle
 * System.out.println(getBondDistance("C", "O", 1).getMaxDistance()); // Valeur maximale de l'intervalle
 */
