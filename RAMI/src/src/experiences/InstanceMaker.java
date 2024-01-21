package src.experiences;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InstanceMaker {

    // On va créer une instance à partir d'une string, puis l'écrire dans un fichier
    public void createInstance(String input, String outputPath) {
        // On découpe la chaîne d'entrée pour avoir les différents atomes
        String[] atoms = input.split(",");

        //construire les chaînes pour les types et les quantités
        StringBuilder typesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();

        // On parcourt chaque atome pour choper le type et la quantité
        for (int i = 0; i < atoms.length; i++) {
            // Ici, on sépare le type de l'atome et sa quantité
            String[] parts = atoms[i].split(":");
            typesBuilder.append("\"").append(parts[0]).append("\"");
            // quantité
            quantitiesBuilder.append(parts[1]);

            // Si c'est pas le dernier, on met une virgule pour séparer
            if (i < atoms.length - 1) {
                typesBuilder.append(", ");
                quantitiesBuilder.append(", ");
            }
        }

        //construire la string JSON
        String jsonString = "{\n" +
                "\t\"types\": [" + typesBuilder.toString() + "],\n" +
                "\t\"quantities\": [" + quantitiesBuilder.toString() + "]\n" +
                "}";

        //écrit string JSON dans le fichier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
