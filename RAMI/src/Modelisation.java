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
import org.chocosolver.solver.variables.IntVar;

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


        // On définit le degré de chaque sommet
        String[] types = atom.getTypes();
        IntVar[] degrees = new IntVar[n];
        int[] quantities = atom.getQuantities();
        String current_type = types[0];
        int c = 1;
        int indice_type = 0;
        for (int i = 0; i < n; i++) {
            if (c == quantities[indice_type]){
                c = 1;
                indice_type += 1;
            }
            System.out.println(types[indice_type]);
//            degrees[i] = model.intVar("d"+str(i),valenceMap.get(types[indice_type]));
            c += 1;
        }
//        model.degrees(g,degrees).post();

        //Utilisation de choco
        Solution solution = model.getSolver().findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        } else {
            System.out.println("Aucune solution trouvée.");
        }
    }
}
