package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.GraphFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

import java.util.Map;

public class GraphModelisation {
    Model model;
    GraphModelisation(Atom atom){
        // List des valences de chaque atome
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

        // List des distances
        Map<String, BondDistance> distanceMap = MoleculeUtils.BOND_DISTANCES;

        //Creation d'un model
        model = new Model("Molecule Generation Problem");

        int n = atom.nbAtom(); // Nombre d'atomes de la molécule
        //int nb_types = atom.nbTypes(); // Nombre de type d'atomes différents
        String[] types = atom.getTypes();

        // VARIABLES
        // An undirected graph
        UndirectedGraph LB = GraphFactory.makeStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET);
        // the last parameter indicates that a complete graph is required
        UndirectedGraph UB = GraphFactory.makeCompleteStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET, true);

        // On ajoute les liaisons pré-établit
        int[][] structure = atom.getStructure();
        int[] liaison;
        for(int i=0; i<structure.length; i++){
            liaison = structure[i];
            if(liaison[1] == Atom.SIMPLE_LIAISON){
                LB.addEdge(liaison[0], liaison[2]);
            }
        }

        UndirectedGraphVar g = model.graphVar("g", LB, UB);


        // CONTRAINTES

        // On définit le degré de chaque sommet
        IntVar[] degrees = new IntVar[n];
        int[] quantities = atom.getQuantities();
        String current_type = types[0];
        int c = 0;
        int indice_type = 0;
        for (int i = 0; i < n; i++) {
            if (c == quantities[indice_type]){
                c = 0;
                indice_type += 1;
            }
            String id = "id"+i;

            degrees[i] = model.intVar(id,valenceMap.get(types[indice_type]));
            c += 1;
        }
        model.degrees(g,degrees).post();

        // Contrainte de connexité
        model.connected(g).post();


    }

    public Model getModel() {
        return model;
    }
}
