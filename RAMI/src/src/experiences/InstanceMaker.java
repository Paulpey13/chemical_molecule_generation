package src.experiences;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Cette classe permet de créer une instance de molécule à partir d'une chaîne de caractères
 * et de l'écrire dans un fichier au format JSON.
 */
public class InstanceMaker {

    /**
     * Crée une instance de molécule à partir d'une chaîne d'entrée et l'écrit dans un fichier au format JSON.
     *
     * @param input      La chaîne d'entrée représentant la composition de la molécule.
     *                  La chaîne doit être au format "élément1:quantité1,élément2:quantité2,..."
     * @param outputPath Le chemin du fichier de sortie où l'instance de molécule sera écrite au format JSON.
     */
    public void createInstance(String input, String outputPath) {
        // On découpe la chaîne d'entrée pour avoir les différents atomes
        String[] atoms = input.split(",");

        // Construire les chaînes pour les types et les quantités
        StringBuilder typesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();

        // On parcourt chaque atome pour obtenir le type et la quantité
        for (int i = 0; i < atoms.length; i++) {
            // Ici, on sépare le type de l'atome et sa quantité
            String[] parts = atoms[i].split(":");
            typesBuilder.append("\"").append(parts[0]).append("\"");
            // Quantité
            quantitiesBuilder.append(parts[1]);

            // Si ce n'est pas le dernier, on met une virgule pour séparer
            if (i < atoms.length - 1) {
                typesBuilder.append(", ");
                quantitiesBuilder.append(", ");
            }
        }

        // Construire la chaîne JSON
        String jsonString = "{\n" +
                "\t\"types\": [" + typesBuilder.toString() + "],\n" +
                "\t\"quantities\": [" + quantitiesBuilder.toString() + "],\n" +
                "\t\"structure\": []\n}";

        // Écrire la chaîne JSON dans le fichier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
