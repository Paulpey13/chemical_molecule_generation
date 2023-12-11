package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import java.util.Map;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.util.objects.graphs.GraphFactory;

import src.Atom;
import src.MoleculeUtils;

public class Modelisation {
    Modelisation(Atom atom){
        // List des valences de chaque atome
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

        //Creation d'un model
        Model model = new Model("Molecule Generation Problem");

        int n = atom.nbAtom();


        // An undirected graph
        UndirectedGraph LB = GraphFactory.makeStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET);
        // the last parameter indicates that a complete graph is required
        UndirectedGraph UB = GraphFactory.makeCompleteStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET, true);
        UndirectedGraphVar g = model.graphVar("g", LB, UB);


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
