package src;

import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe génère des fichiers au format CML à partir des informations du graphe et des atomes.
 */
public class CML_generator {

    private static int fileCount = 0; // Compteur pour les fichiers

    /**
     * Génère des fichiers CML à partir des données du graphe, des atomes et des coordonnées.
     *
     * @param graphVar   La variable du graphe.
     * @param atomTypes  Les types d'atomes.
     * @param xs         Les coordonnées x.
     * @param ys         Les coordonnées y.
     * @param zs         Les coordonnées z.
     * @param data       Les données d'entrée.
     * @param liaisons   Les liaisons entre les atomes.
     */
    public static void generateCMLFiles(GraphVar graphVar, String[] atomTypes, RealVar[] xs, RealVar[] ys, RealVar[] zs, String data, int[][] liaisons) {
        //Seul moyen que j'ai trouvé pour pouvoir garder les fichiers en mémoire, on les nomme avec date et heure
        //Pour éviter l'écriture par-dessus
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        String timestamp = now.format(formatter);
        int lastSlashIndex = data.lastIndexOf('/'); //Supprimer du nom le chemin avant le dernier /
        int lastDotIndex = data.lastIndexOf('.'); //Supprimer l'extension .json du nom

        data = data.substring(lastSlashIndex + 1, lastDotIndex);
        String fileName = "cml_output/solution_" + data + "_" + timestamp + ".cml";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<cml xmlns=\"http://www.xml-cml.org/schema\">\n");
            writer.write("  <molecule id =\"1\">\n");
            writeAtomsAndBonds(writer, graphVar, atomTypes, xs, ys, zs, liaisons);
            writer.write("  </molecule>\n");
            writer.write("</cml>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Écrit les atomes et les liaisons dans le fichier CML.
     *
     * @param writer     Le BufferedWriter pour écrire dans le fichier.
     * @param graphVar   La variable du graphe.
     * @param atomTypes  Les types d'atomes.
     * @param xs         Les coordonnées x.
     * @param ys         Les coordonnées y.
     * @param zs         Les coordonnées z.
     * @param liaisons   Les liaisons entre les atomes.
     * @throws IOException En cas d'erreur d'écriture.
     */
    private static void writeAtomsAndBonds(BufferedWriter writer, GraphVar graphVar, String[] atomTypes, RealVar[] xs, RealVar[] ys, RealVar[] zs, int[][] liaisons) throws IOException {
        UndirectedGraph graph = (UndirectedGraph) graphVar.getValue();

        // Écrire les atomes
        writer.write("    <atomArray>\n");
        for (int i = 0; i < graph.getNodes().size(); i++) {
            String atomType = atomTypes[i];
            double x = xs[i].getLB(); // ou getUB() selon la précision nécessaire
            double y = ys[i].getLB();
            double z = zs[i].getLB();
            writer.write(String.format("      <atom id=\"a%d\" elementType=\"%s\" x3=\"%f\" y3=\"%f\" z3=\"%f\"/>\n", i, atomType, x / 100, y / 100, z / 100));
        }
        writer.write("    </atomArray>\n");

        // Écrire les liaisons
        writer.write("    <bondArray>\n");
        for (int i = 0; i < graph.getNodes().size(); i++) {
            for (int j : graph.getNeighborsOf(i)) {
                if (i < j) { // Pour éviter les doublons
                    writer.write("      <bond atomRefs2=\"a" + i + " a" + j + "\" order=\"" + liaisons[i][j] + "\"/>\n");
                }
            }
        }
        writer.write("    </bondArray>\n");
    }

    /**
     * Construit un tableau de types d'atomes en fonction des quantités spécifiées dans l'objet Atom.
     *
     * @param atom L'objet Atom contenant les types et les quantités.
     * @return Un tableau de types d'atomes.
     */
    public static String[] buildAtomTypesArray(Atom atom) {
        List<String> typesList = new ArrayList<>();
        String[] types = atom.getTypes();
        int[] quantities = atom.getQuantities();

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < quantities[i]; j++) {
                typesList.add(types[i]);
            }
        }

        return typesList.toArray(new String[0]);
    }
}
