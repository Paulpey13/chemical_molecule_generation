package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.graph.connectivity.PropConnected;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import java.util.Map;

import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.util.objects.graphs.GraphFactory;
import org.chocosolver.solver.variables.IntVar;

import src.Atom;
import src.MoleculeUtils;

public class Modelisation {
    private Model model;
    Modelisation(Atom atom){
        // List des valences de chaque atome
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

        // List des distances
        Map<String, BondDistance> distanceMap = MoleculeUtils.BOND_DISTANCES;

        //Creation d'un model
        model = new Model("Molecule Generation Problem");

        int n = atom.nbAtom(); // Nombre d'atomes de la molécule
        int nb_types = atom.nbTypes(); // Nombre de type d'atomes différents
        String[] types = atom.getTypes();

        // VARIABLES
        // An undirected graph
        UndirectedGraph LB = GraphFactory.makeStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET);
        // the last parameter indicates that a complete graph is required
        UndirectedGraph UB = GraphFactory.makeCompleteStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET, true);
        UndirectedGraphVar g = model.graphVar("g", LB, UB);

        // Les coordonées pour chaques sommets en 3 dimensions (x,y,z)
        RealVar [] xs = new RealVar[n];
        RealVar [] ys = new RealVar[n];
        RealVar [] zs = new RealVar[n];
        int maxx = 10000 ; int minx = -10000;
        int maxy = 10000 ; int miny = -10000;
        int maxz = 10000 ; int minz = -10000;
        double p = 0.001;
        // On fixe le premier atom à l'origine du repère (0;0;0;)
        xs[0] = model.realVar("x" + 0, 0, 0, p);
        ys[0] = model.realVar("y" + 0, 0, 0, p);
        zs[0] = model.realVar("z" + 0, 0,0, p);
        for(int i = 1; i < n ; i++) {
            xs[i] = model.realVar("x" + i, minx, maxx, p);
            ys[i] = model.realVar("y" + i, miny, maxy, p);
            zs[i] = model.realVar("z" + i, minz,maxz, p);
        }

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

        // Contraintes reify d'arête entre deux atomes
        BoolVar[][] isConnected = new BoolVar[n][n];
        for(int i =0; i<n;i++){
            for(int j = 0 ; j <n; j++){
                isConnected[i][j] = model.boolVar(i+"-"+j);
                model.edgeChanneling(g,isConnected[i][j], i, j).post();
            }
        }

        // Contraintes de distances
        int dist_max; int dist_min; int index;
        String second_type="X";
        for(int i=0; i<n; i++){
            for(int j=i+1;j<n; j++){
                // On récupère l'intervalle de distance concernant cette liaisons
                if(i <quantities[0]){
                    current_type = types[0];
                }
                if(j <quantities[0]){
                    second_type = types[0];
                }
                for(int t =1; t<types.length; t++){
                    if(quantities[t-1] <= i && i <quantities[t]){
                        current_type = types[t];
                    }
                    if(quantities[t-1] <= j && j <quantities[t]){
                        second_type = types[t];
                    }
                }


                dist_min = distanceMap.get(current_type+"-"+second_type).getMinDistance();
                dist_max = distanceMap.get(current_type+"-"+second_type).getMaxDistance();
                System.out.println(current_type+" "+second_type+" dist ["+dist_min+","+dist_max+"]");

                model.ifThen(isConnected[i][j],
                        xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                                .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                                .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                                .ge(dist_min).equation());
                model.ifThen(isConnected[i][j], xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                        .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                        .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                        .le(dist_max).equation());
            }
        }



    }

    public Model getModel() {
        return model;
    }
}
