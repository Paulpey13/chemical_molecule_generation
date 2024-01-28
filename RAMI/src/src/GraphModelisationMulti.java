package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.objects.graphs.GraphFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;

public class GraphModelisationMulti {

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

   public GraphModelisationMulti(Atom atom) {

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


       // Matrice des liaisons (0 = aucune, 1 = simple, 2 = double)
       IntVar[][] liaisons = new IntVar[n][n];
       for (int i = 0; i < n; i++) {
           for (int j = i + 1; j < n; j++) {
               liaisons[i][j] = model.intVar("Liaison_" + i + "_" + j, 0, 3);
           }
       }

       // On ajoute les liaisons pré-établit
       int[][] structure = atom.getStructure();
       int[] liaison;
       for(int i=0; i<structure.length; i++){
           liaison = structure[i];
           if(liaison[1] == Atom.SIMPLE_LIAISON){
               LB.addEdge(liaison[0], liaison[2]);
               liaisons[liaison[0]][liaison[2]].eq(1).post();
           }
           if(liaison[1] == Atom.DOUBLE_LIAISON){
               LB.addEdge(liaison[0], liaison[2]);
               liaisons[liaison[0]][liaison[2]].eq(2).post();
           }
           if(liaison[1] == Atom.TRIPLE_LIAISON){
               LB.addEdge(liaison[0], liaison[2]);
               liaisons[liaison[0]][liaison[2]].eq(3).post();
           }
       }

       UndirectedGraphVar g = model.graphVar("g", LB, UB);


       //Contrainte sur les doubles liaisons

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

    public static int[][] getLiaisons(Variable[] vars, int n) {
        int[][] liaisons = new int[n][n];

        for (Variable var : vars) {
            String name = var.getName();
            if (name.startsWith("Liaison")) {
                if (var.isInstantiated()) {
                    int value = ((IntVar) var).getValue(); // Cast en IntVar si c'est le type de vos variables
                    String[] parts = name.split("_");
                    int i = Integer.parseInt(parts[1]);
                    int j = Integer.parseInt(parts[2]);

                    liaisons[i][j] = value;
                    liaisons[j][i] = value; // Si la liaison est symétrique
                }
            }
        }

        return liaisons;
    }

   public static Graph translate(GraphVar g, String[] types, int[][] liaisons) {
        Graph<GraphModelisationMulti.Node, DefaultEdge> transG = new SimpleGraph<>(DefaultEdge.class);
        int nbNodes = types.length;
        GraphModelisationMulti.Node[] nodes = new GraphModelisationMulti.Node[nbNodes];
        for(int i=0; i<nbNodes; i++){
            GraphModelisationMulti.Node node = new GraphModelisationMulti.Node(i, types[i]);
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
                        GraphModelisationMulti.Node newNode = new GraphModelisationMulti.Node(nd,"D");
                        transG.addVertex(newNode);

                        // Ajouter des arêtes entre le nouveau sommet et les sommets i et j
                        transG.addEdge(nodes[i], newNode);
                        transG.addEdge(newNode, nodes[j]);
                    }
                    else if (liaisons[i][j]==3){

                        nd+=1;
                        // Créer et ajouter un nouveau sommet
                        GraphModelisationMulti.Node newNode = new GraphModelisationMulti.Node(nd,"T");
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

        // Ajouter les sommets avec labels
        for (Object o : g.vertexSet()) {
            Node node = (Node) o;
            if (!node.getType().equals("D") && !node.getType().equals("T")) {
                gv += "" + node.getNum() + " [label=\"" + node.getType() + "_" + node.getNum() + "\"]; \n";
            }
        }
        gv += "\n";

        Set<String> addedEdges = new HashSet<>();

        // Parcourir les arêtes et traiter les nœuds D ou T
        for (Object o2 : g.edgeSet()) {
            DefaultEdge edge = (DefaultEdge) o2;
            Node source = (Node) g.getEdgeSource(edge);
            Node target = (Node) g.getEdgeTarget(edge);

            // Gérer les arêtes directes
            if (!source.getType().equals("D") && !source.getType().equals("T") && !target.getType().equals("D") && !target.getType().equals("T")) {
                String edgeKey = Math.min(source.getNum(), target.getNum()) + "--" + Math.max(source.getNum(), target.getNum());
                if (!addedEdges.contains(edgeKey)) {
                    gv += source.getNum() + " -- " + target.getNum() + "; \n";
                    addedEdges.add(edgeKey);
                }
            }
            // Gérer les intermédiaires D ou T
            else {
                Node intermediate = source.getType().equals("D") || source.getType().equals("T") ? source : target;
                Node otherEnd = intermediate.equals(source) ? target : source;

                for (Object connectingEdge : g.edgesOf(intermediate)) {
                    Node connectingNode = (Node) (g.getEdgeSource(connectingEdge).equals(intermediate) ? g.getEdgeTarget(connectingEdge) : g.getEdgeSource(connectingEdge));
                    if (!connectingNode.equals(otherEnd) && !connectingNode.getType().equals("D") && !connectingNode.getType().equals("T")) {
                        String edgeKey = Math.min(connectingNode.getNum(), otherEnd.getNum()) + "--" + Math.max(connectingNode.getNum(), otherEnd.getNum());
                        if (!addedEdges.contains(edgeKey)) {
                            int edgeCount = intermediate.getType().equals("D") ? 2 : 3;
                            for (int i = 0; i < edgeCount; i++) {
                                gv += otherEnd.getNum() + " -- " + connectingNode.getNum() + "; \n";
                            }
                            addedEdges.add(edgeKey);
                        }
                    }
                }
            }
        }

        gv += "}\n";
        return gv;
    }

    public Model getModel() {
        return model;
    }

}
