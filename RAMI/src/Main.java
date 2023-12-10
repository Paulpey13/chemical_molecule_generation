import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import java.util.Map;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String data = "C:/Users/paul/OneDrive/Bureau/RAMI_Project/chemical_molecule_generation/RAMI/data/atomes";
        // Aucune idée pq j'arrive pas à acceder avec un path normal sur intellijidea donc j'ai tout mis

        // Utilisation de la fonction MoleculeUtils.readInstancesFile() pour lire le fichier des instances
        // ça retourne une liste de dico
        List<Map<String, Integer>> instances = MoleculeUtils.readInstancesFile(data);

        // Print chaque dico de chaque instance
        //for (Map<String, Integer> atomCounts : instances) {System.out.println(atomCounts);}

        // List des valences de chaque atome
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

        //********************** TUTO CREATION DE GRAPHES AVEC CHOCO **********************//
        //Creation d'un model
        Model model = new Model("Molecule Generation Problem");

        //Pour faire des test juste sur la premiere instance
        Map<String, Integer> instance = instances.get(0);
        System.out.println(instance);

        //Definition des variables et contraintes
        //Nombre total d'atome (prendre la somme des atome qu'on a sur la ligne correspondantes de atomes.txt)
        int n = 10;
        UndirectedGraph LB = new UndirectedGraph(model, n, SetType.BITSET, false);
        UndirectedGraph UB = new UndirectedGraph(model, n, SetType.BITSET, false);
        // Ajouter des sommets et des arêtes aux bornes LB et UB selon les règles chimiques

        GraphVar graphVar = model.graphVar("molecule", LB, UB);

        //Ici ajouter les contraintes


        //Utilisation de choco
        Solution solution = model.getSolver().findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        } else {
            System.out.println("Aucune solution trouvée.");
        }
    }
}
