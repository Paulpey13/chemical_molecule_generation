package src;

import com.google.gson.Gson;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainSpace {


    public static String data;
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        // Données d'entrée au format JSON
        data = "./RAMI/data/test.json";

//        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);

            Map<Integer, String> list_correspondance = AtomIndexer.getAtomIndices(data);

            // Création de la modélisation
            GraphModelisation mod = new GraphModelisation(atom);
            Model model = mod.getModel();
            System.out.println("Fin de Modélisation");

            // Résolution
            Solver solver = model.getSolver();
            Variable[] vars = model.getVars();


            // Recherche de toutes les solutions
            // On itère sur la variable de graphe
            List<Graph> listStruct = new ArrayList<>();
            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) vars[0]));
            while (model.getSolver().solve()) {
                Graph<GraphModelisation.Node, DefaultEdge> graph1 = GraphModelisation.translate((GraphVar) vars[0], atom.listTypes());

                // On regarde si le graphe n'est pas isomorphe à un des graphes déjà trouvé
                boolean new_g = true;
                for(int i = 0; i <listStruct.size(); i++){
                    Graph<GraphModelisation.Node, DefaultEdge> graph2 = listStruct.get(i);
                    VF2GraphIsomorphismInspector<GraphModelisation.Node, DefaultEdge> inspector =
                            new VF2GraphIsomorphismInspector<>(graph1, graph2, new Comparator<GraphModelisation.Node>() {
                                @Override
                                public int compare(GraphModelisation.Node v1, GraphModelisation.Node v2) {
                                    return v1.getType().equals(v2.getType()) ? 0 : -1;
                                }
                            }, null);

                    if (inspector.isomorphismExists()) {
                        // Les graphes sont isomorphes; retirez un des graphes
                        new_g = false;
                    }
                }

                if(new_g){
                    // On ajoute le graphe à la liste
                    listStruct.add(graph1);
                    // On crée la modélisation pour chercher un placement dans l'espace
                    Modelisation mod2 = new Modelisation(atom, (GraphVar) vars[0], -1);
                    Model model2 = mod2.getModel();

                    if(model2.getSolver().solve()){
                        Variable[] vars2 = model2.getVars();
                        for(Variable v : vars2){
                            System.out.println(v);
                        }
                        // Génération du CML
                        GraphVar graphVar = (GraphVar) vars[0];
                        RealVar[] xs = mod2.getXs(); // Assurez-vous que Modelisation a des getters pour xs, ys, zs
                        RealVar[] ys = mod2.getYs();
                        RealVar[] zs = mod2.getZs();
                        String[] atomTypes = CML_generator.buildAtomTypesArray(atom);
                        CML_generator.generateCMLFiles(graphVar, atomTypes, xs, ys, zs,data);
                    }
                    else{
                        System.out.println("Pas de solution de coordonnées pour cette structure de graphe");
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
