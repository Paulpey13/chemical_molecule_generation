package src;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cette classe gère la conversion des indices/nœuds d'un graphe en noms d'atomes
 * en utilisant un fichier JSON pour spécifier les types et les quantités d'atomes.
 * Cela permet une bonne visualition via graphviz
 */
public class AtomIndexer {

    /**
     * Classe interne pour stocker les données d'atome à partir du fichier JSON.
     */
    static class AtomData {
        List<String> types;
        List<Integer> quantities;
    }

    /**
     * Cette méthode extrait les indices d'atomes à partir du fichier JSON
     * et les associe à leurs types correspondants.
     *
     * @param filePath Le chemin du fichier JSON contenant les données des atomes.
     * @return Un dictionnaire associant les indices d'atomes à leurs types.
     */
    public static Map<Integer, String> getAtomIndices(String filePath) {
        Map<Integer, String> atomIndices = new HashMap<>();
        Map<String, Integer> atomCount = new HashMap<>();  // Nouvelle map pour suivre le nombre d'occurrences de chaque type d'atome
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(filePath)) {
            AtomData atomData = gson.fromJson(reader, AtomData.class);

            int currentIndex = 0;
            for (int i = 0; i < atomData.types.size(); i++) {
                String atomType = atomData.types.get(i);
                int quantity = atomData.quantities.get(i);

                for (int j = 0; j < quantity; j++) {
                    // Obtenir le nombre actuel d'occurrences de ce type d'atome
                    int count = atomCount.getOrDefault(atomType, 0);

                    // Générer la clé de l'atome avec un indice si nécessaire
                    String atomKey = atomType + (count > 0 ? "_" + count : "");
                    atomIndices.put(currentIndex, atomKey);

                    // Mettre à jour currentIndex et atomCount
                    currentIndex++;
                    atomCount.put(atomType, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return atomIndices;
    }

    /**
     * Cette méthode convertit la sortie de GraphViz en remplaçant les indices/nœuds
     * par les noms d'atomes correspondants en utilisant le dictionnaire fourni.
     *
     * @param graphVizOutput La sortie générée par GraphViz.
     * @param atomIndices    Le dictionnaire associant les indices d'atomes à leurs noms.
     * @return La sortie de GraphViz avec les noms d'atomes au lieu des indices.
     */
    public static String convertGraphVizOutput(String graphVizOutput, Map<Integer, String> atomIndices) {
        // Diviser la sortie en lignes
        String[] lines = graphVizOutput.split("\n");

        StringBuilder convertedOutput = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                // Diviser la ligne par des espaces pour obtenir les indices/nœuds et les arêtes
                String[] elements = line.trim().split("\\s+");

                for (String element : elements) {
                    if (element.contains("--")) {
                        // C'est une arête, la garder telle quelle
                        convertedOutput.append(element);
                    } else if (element.matches("\\d+")) {
                        // C'est un indice/nœud, le convertir en atome
                        int index = Integer.parseInt(element);
                        String atom = atomIndices.getOrDefault(index, "Unknown");  // Utiliser "Unknown" si l'atome n'est pas trouvé
                        convertedOutput.append(atom);
                    } else {
                        // Gérer les cas inattendus (si besoin)
                        convertedOutput.append(element);
                    }
                    convertedOutput.append(" ");
                }
                convertedOutput.append("\n"); // Fin de la ligne
            }
        }

        return convertedOutput.toString();
    }

    // Vous pouvez tester la fonction ici
    public static void main(String[] args) {
        String filePath = "path_to_your_file.json";  // Remplacez par le chemin réel de votre fichier
        Map<Integer, String> atomIndices = getAtomIndices(filePath);
        System.out.println(atomIndices);
    }
}
