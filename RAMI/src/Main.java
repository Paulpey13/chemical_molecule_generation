import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String data = "C:/Users/paul/OneDrive/Bureau/RAMI_Project/chemical_molecule_generation/RAMI/data/atomes";
        //Aucune idée pq j'arrive pas à acceder avec un path normal sur intellijidea donc j'ai tout mis

        //Use la fonction MoleculeUtils.readInstancesFile() pour lire le fichier des instances
        //ça retourne une liste de dico
        List<Map<String, Integer>> instances = MoleculeUtils.readInstancesFile(data);

        //Print chaque dico de chaque instance
        for (Map<String, Integer> atomCounts : instances) {
            System.out.println(atomCounts);
        }

        //List des valence de chaques atomes
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

    }
}
