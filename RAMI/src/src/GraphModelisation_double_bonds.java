package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.objects.graphs.GraphFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class GraphModelisation_double_bonds {

    static class Node {
        private int num;
        private String type;

        public Node(int num, String type) {
            this.num = num;
            this.type = type;
        }
        public int getNum(){
            return this.num;
        }
        public String getType(){
            return this.type;
        }
        @Override
        public String toString(){
            return "("+this.num+" ; "+this.type+")";
        }
    }

    Model model;

   public GraphModelisation_double_bonds(Atom atom) {

       // Initialisation du modèle
       this.model = new Model("Molecule Generation Problem");

       int n = atom.nbAtom(); // Nombre d'atomes

       // Création de la liste des identifiants des atomes
       List<String> Id_Atomes = new ArrayList<>();
       int[] quantites = atom.getQuantities();
       String[] types = atom.getTypes();

       for (int i = 0; i < types.length; i++) {
           for (int j = 0; j < quantites[i]; j++) {
               Id_Atomes.add(types[i]);
           }
       }


       Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;
       IntVar nombreDeNoeuds = model.intVar("nombreDeNoeuds", atom.nbAtom(), atom.nbAtom());

       // Création d'un graphe non orienté
       UndirectedGraph LB = GraphFactory.makeStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET);
       UndirectedGraph UB = GraphFactory.makeCompleteStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET, false);
        UndirectedGraphVar g = model.graphVar("g", LB, UB);

       // Matrice des liaisons (0 = aucune, 1 = simple, 2 = double)
       IntVar[][] liaisons = new IntVar[n][n];


       //Contrainte sur les doubles liaisons

       for (int i = 0; i < n; i++) {
           for (int j = i + 1; j < n; j++) {
               liaisons[i][j] = model.intVar("Liaison_" + i + "_" + j, 0, 3);}
           }

       for (int i = 0; i < n; i++) {
           IntVar[] liaisonsAtome = new IntVar[n];
           for (int j = 0; j < n; j++) {
               if (i != j) {
                   liaisonsAtome[j] = liaisons[Math.min(i, j)][Math.max(i, j)];
               } else {
                   liaisonsAtome[j] = model.intVar(0); // Pas de liaison avec soi-même
               }
           }
           // Le total des liaisons pour l'atome i doit être égal à sa valence
           model.sum(liaisonsAtome, "=", valenceMap.get(Id_Atomes.get(i))).post();
       }


       // Contraintes sur les arêtes  du graph en fonction des liaisons
       for (int i = 0; i < n; i++) {
           for (int j = i + 1; j < n; j++) {

               // Création d'une BoolVar pour la présence de l'arête
               BoolVar aretePresente = model.boolVar("arete_" + i + "_" + j);


               BoolVar liaisonSuperieureAZero = model.arithm(liaisons[i][j], ">", 0).reify();

               // Lier la présence de l'arête à la liaison
               model.arithm(aretePresente, "=", liaisonSuperieureAZero).post();

               // Lier la présence de l'arête au graphe
               model.edgeChanneling(g, aretePresente, i, j).post();


           }

           // Contrainte de connexité
           model.connected(g).post();
           // Contrainte sur le nombre de noeud
           model.nbNodes(g, nombreDeNoeuds).post();

       }
   }
    public static Graph translate(GraphVar g, String[] types, int[][] liaisons) {
        Graph<GraphModelisation_double_bonds.Node, DefaultEdge> transG = new SimpleGraph<>(DefaultEdge.class);
        int nbNodes = types.length;
        GraphModelisation_double_bonds.Node[] nodes = new GraphModelisation_double_bonds.Node[nbNodes];
        for(int i=0; i<nbNodes; i++){
            GraphModelisation_double_bonds.Node node = new GraphModelisation_double_bonds.Node(i, types[i]);
            nodes[i] = node;
            transG.addVertex(node);
        }

        int nd=nbNodes-1;
        for(int i = 0; i < nbNodes; i++) {
            for(int j = i + 1; j < nbNodes; j++) {
                if (g.getValue().containsEdge(i, j)) {
                    if (liaisons[i][j] == 2) {
                        nd+=1;
                        // Créer et ajouter un nouveau sommet
                        GraphModelisation_double_bonds.Node newNode = new GraphModelisation_double_bonds.Node(nd,"D");
                        transG.addVertex(newNode);

                        // Ajouter des arêtes entre le nouveau sommet et les sommets i et j
                        transG.addEdge(nodes[i], newNode);
                        transG.addEdge(newNode, nodes[j]);
                    }
                    else if (liaisons[i][j]==3){

                        nd+=1;
                        // Créer et ajouter un nouveau sommet
                        GraphModelisation_double_bonds.Node newNode = new GraphModelisation_double_bonds.Node(nd,"T");
                        transG.addVertex(newNode);

                        // Ajouter des arêtes entre le nouveau sommet et les sommets i et j
                        transG.addEdge(nodes[i], newNode);
                        transG.addEdge(newNode, nodes[j]);
                    }

                    else {
                        // Ajouter une arête normale si la condition n'est pas remplie
                        transG.addEdge(nodes[i], nodes[j]);
                    }
                }
            }
        }

        return transG;
    }

    public static String getGraphViz(Graph g){
        String gv = "graph G{ \n";

        for(Object o : g.vertexSet()){
            GraphModelisation_double_bonds.Node n = (GraphModelisation_double_bonds.Node) o;
            gv += ""+n.getNum()+" [label=\""+n.getType()+"_"+n.getNum()+"\"]; \n";
        }
        gv +="\n";
        for(Object o : g.edgeSet()){
            DefaultEdge ed = (DefaultEdge) o;
            String edS = ed.toString();
            // Supprimer les caractères non nécessaires
            String cleanedInput = edS.replaceAll("[()]", "");

            // Séparer la chaîne en éléments
            String[] elements = cleanedInput.split(" ");

            // Créer une liste pour stocker les éléments
            ArrayList<String> filteredList = new ArrayList<>();
            for(String e : elements){
                if(e.compareTo(";") != 0 && e.compareTo(":") !=0){
                    filteredList.add(e);
                }
            }
            gv = gv+""+filteredList.get(0)+" -- "+filteredList.get(2)+"; \n";
        }
        gv = gv+"}\n";

        return gv;
    }

    public Model getModel() {
        return model;
    }

}
