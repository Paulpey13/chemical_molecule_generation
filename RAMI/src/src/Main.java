package src;

import com.google.gson.Gson;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.solver.variables.Variable;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Main {

    public static String data;
    public static void main(String[] args) {

        // Données d'entrée au format JSON
        data = "./RAMI/data/test.json";

//        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);
            System.out.println(atom);

            // Création de la modélisation
            GraphModelisation mod = new GraphModelisation(atom);
            Model model = mod.getModel();
            System.out.println("Fin de Modélisation");

            // Résolution
            Solver solver = model.getSolver();
            Variable[] vars = model.getVars();

//            System.out.println(atom.printListTypes());
            // Recherche de toutes les solutions
            // On itère sur la variable de graphe
            int c = 0;
            List<Graph> listStruct = new ArrayList<>();
            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) vars[0]));
           while (model.getSolver().solve()) {
//               Modelisation mod2 = new Modelisation(atom, (GraphVar) vars[0], -1);
//               Model model2 = mod2.getModel();
//               System.out.println("Le graphe"+vars[0]);
                listStruct.add(GraphModelisation.translate((GraphVar) vars[0], atom.listTypes()));
//               System.out.print(g.getValue().getNodes());
//               System.out.println("new"+g.getValue().containsEdge(0,7));

               c += 1;

//               if(model2.getSolver().solve()){
//                   Variable[] vars2 = model2.getVars();
//                   for(Variable v : vars2){
//                       System.out.println(v);
//                   }
//                   // Génération du CML
//                   GraphVar graphVar = (GraphVar) vars[0];
//                   RealVar[] xs = mod2.getXs(); // Assurez-vous que Modelisation a des getters pour xs, ys, zs
//                   RealVar[] ys = mod2.getYs();
//                   RealVar[] zs = mod2.getZs();
//                   String[] atomTypes = CML_generator.buildAtomTypesArray(atom);
//                   CML_generator.generateCMLFiles(graphVar, atomTypes, xs, ys, zs,data);
//               }
//               else{
//                   System.out.println("Pas de solution de coordonnées pour cette structure de graphe");
//               }

           }
            System.out.println(c+" solutions trouvées");
           if(listStruct.size() == 0){
               System.out.println("Aucunes solutions trouvées");
           }else{
               for(int i=0; i<listStruct.size(); i++){
                   for(int j=i+1; j<listStruct.size(); j++){
                       Graph<GraphModelisation.Node, DefaultEdge> graph1 = listStruct.get(i);
                       Graph<GraphModelisation.Node, DefaultEdge> graph2 = listStruct.get(j);

                       VF2GraphIsomorphismInspector<GraphModelisation.Node, DefaultEdge> inspector =
                               new VF2GraphIsomorphismInspector<>(graph1, graph2, new Comparator<GraphModelisation.Node>() {
                                   @Override
                                   public int compare(GraphModelisation.Node v1, GraphModelisation.Node v2) {
                                       return v1.getType().equals(v2.getType()) ? 0 : -1;
                                   }
                               }, null);

                       if (inspector.isomorphismExists()) {
                           // Les graphes sont isomorphes; retirez un des graphes
                           listStruct.remove(j);
                           j--; // Ajustez l'indice après la suppression
                       }
                   }
               }
           }

           for(Graph g : listStruct){
               System.out.println("Maintenant, "+listStruct.size()+" solutions");
               System.out.println(g);
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
