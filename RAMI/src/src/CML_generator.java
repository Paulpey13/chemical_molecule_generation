package src;

import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CML_generator {

    private static int fileCount = 0; // Compteur pour les fichiers

    public static void generateCMLFiles(GraphVar graphVar, String[] atomTypes) {
        //Seul moyen que j'ai trouvé pour pouvoir garder les fichiers en mémoire, on les nommes avec date et heure
        //Pour pas qu'il ne s'override
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        String timestamp = now.format(formatter);
        String fileName = "cml_output/solution" + timestamp + ".cml";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<cml xmlns=\"http://www.xml-cml.org/schema\">\n");
            writer.write("  <molecule id =\"1\">\n");
            writeAtomsAndBonds(writer, graphVar, atomTypes);
            writer.write("  </molecule>\n");
            writer.write("</cml>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeAtomsAndBonds(BufferedWriter writer, GraphVar graphVar, String[] atomTypes) throws IOException {
        UndirectedGraph graph = (UndirectedGraph) graphVar.getValue();

        // Écrire les atomes
        writer.write("    <atomArray>\n");
        for (int i = 0; i < graph.getNodes().size(); i++) {
            String atomType = atomTypes[i]; // Récupérer le type d'atome
            writer.write("      <atom id=\"a" + i + "\" elementType=\"" + atomType + "\"/>\n");
        }
        writer.write("    </atomArray>\n");

        // Écrire les liaisons
        writer.write("    <bondArray>\n");
        for (int i = 0; i < graph.getNodes().size(); i++) {
            for (int j : graph.getNeighborsOf(i)) {
                if (i < j) { // Pour éviter les doublons
                    writer.write("      <bond atomRefs2=\"a" + i + " a" + j + "\" order=\"1\"/>\n");
                }
            }
        }
        writer.write("    </bondArray>\n");
    }

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
