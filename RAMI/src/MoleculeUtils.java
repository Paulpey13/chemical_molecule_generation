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

    //Lis le fichier des instances atomes.txt et retourne une list de dico
    //Chaque dico est de la forme : {atome1=nb1,...,atomeN=nbN}
    public static List<Map<String, Integer>> readInstancesFile(String fileName) {
        List<Map<String, Integer>> atomCountsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Map<String, Integer> atomCounts = new HashMap<>();
                String[] atoms = line.split(" ");
                for (String atom : atoms) {
                    String element = atom.substring(0, 1);
                    int count = Integer.parseInt(atom.substring(1));
                    atomCounts.put(element, atomCounts.getOrDefault(element, 0) + count);
                }
                atomCountsList.add(atomCounts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return atomCountsList;
    }

}
