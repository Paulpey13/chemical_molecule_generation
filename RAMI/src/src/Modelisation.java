package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;

import java.util.Map;

public class Modelisation {
    private Model model;
    public RealVar[] xs;
    public RealVar[] ys;
    public RealVar[] zs;

    public RealVar minDistSquared;

    public int numFctObj; // -1 si pas de fonction objective

    Modelisation(Atom atom, int[][] liaisons, int num){
        this.numFctObj = num;
        // List des valences de chaque atome
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

        // List des distances
        Map<String, BondDistance> distanceMap = MoleculeUtils.BOND_DISTANCES;

        //Creation d'un model
        model = new Model("Molecule Generation Problem");

        int n = atom.nbAtom(); // Nombre d'atomes de la molécule
        String[] types = atom.getTypes();


        // Les coordonées pour chaques sommets en 3 dimensions (x,y,z)
        xs = new RealVar[n];
        ys = new RealVar[n];
        zs = new RealVar[n];
        int maxx = 300 ; int minx = -300;
        int maxy = 300 ; int miny = -300;
        int maxz = 300 ; int minz = -300;
        double p = 1;

        // On fixe le premier atome à l'origine du repère (0;0;0;)
        xs[0] = model.realVar("x0", 0, 0, p);
        ys[0] = model.realVar("y0", 0, 0, p);
        zs[0] = model.realVar("z0", 0,0, p);
        // On fixe le deuxième atome sur l'axe des z positifs (0;0;z>=0)
        if(n >= 2) {
            xs[1] = model.realVar("x1", 0, 0, p);
            ys[1] = model.realVar("y1", 0, 0, p);
            zs[1] = model.realVar("z1", 0, maxz, p);
        }
        // On fixe le troisième atome dans le plan yz y positif (0;y>=0;z)
        if(n >= 3) {
            xs[2] = model.realVar("x2", 0, 0, p);
            ys[2] = model.realVar("y2", 0, maxy, p);
            zs[2] = model.realVar("z2", minz, maxz, p);
        }
        // On fixe le quatrième atome dans le demi-espace x positif (x >= 0;y;z)
        if(n >= 4) {
            xs[3] = model.realVar("x3", 0, maxx, p);
            ys[3] = model.realVar("y3", miny, maxy, p);
            zs[3] = model.realVar("z4", minz, maxz, p);
        }
        for(int i = 4; i < n ; i++) {
            xs[i] = model.realVar("x" + i, minx, maxx, p);
            ys[i] = model.realVar("y" + i, miny, maxy, p);
            zs[i] = model.realVar("z" + i, minz,maxz, p);
        }


        // CONTRAINTES

        // On définit le degré de chaque sommet
        IntVar[] degrees = new IntVar[n];
        int[] quantities = atom.getQuantities();
        String current_type = types[0];

        // Contraintes de distances
        int dist_max = 1; int dist_min = 1; int index;
        boolean isConnected = false;
        String second_type="X";
        for(int i=0; i<n; i++){
            for(int j=i+1;j<n; j++){
                // On récupère l'intervalle de distance concernant cette liaisons
                if(i < quantities[0]){
                    current_type = types[0];
                }
                if(j <quantities[0]){
                    second_type = types[0];
                }
                int sum_indice = quantities[0];
                for(int t =1; t<types.length; t++){
                    if(sum_indice <= i && i < sum_indice+quantities[t]){
                        current_type = types[t];
                    }
                    if(sum_indice <= j && j <sum_indice + quantities[t]){
                        second_type = types[t];
                    }
                    sum_indice += quantities[t];
                }


                if(liaisons[i][j] == 1){
                    isConnected = true;
                    dist_min = distanceMap.get(current_type+"-"+second_type).getMinDistance();
                    dist_max = distanceMap.get(current_type+"-"+second_type).getMaxDistance();
                } else if (liaisons[i][j] == 2) {
                    isConnected = true;
                    dist_min = distanceMap.get(current_type+"="+second_type).getMinDistance();
                    dist_max = distanceMap.get(current_type+"="+second_type).getMaxDistance();
                } else if (liaisons[i][j] == 3) {
                    isConnected = true;
                    dist_min = distanceMap.get(current_type+"#"+second_type).getMinDistance();
                    dist_max = distanceMap.get(current_type+"#"+second_type).getMaxDistance();
                }else{
                    isConnected = false;
                    dist_max = distanceMap.get(current_type+"-"+second_type).getMaxDistance();
                    dist_min = distanceMap.get(current_type+"-"+second_type).getMinDistance();
                }

                if(isConnected){
                    dist_max = dist_max*dist_max;
                    dist_min = dist_min*dist_min;


                    // (i,j) dans G ==> distance(i,j) <= dist_max
                    xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                            .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                            .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                            .le(dist_max).equation().post();

                    // (i,j) dans G ==> distance(i,j) >= dist_min
                    xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                            .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                            .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                            .ge(dist_min).equation().post();
                }else{
                    dist_max = dist_max*dist_max;
                    // (i,j) pas dans G ==> distance(i,j) >= dist_max
                    xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                            .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                            .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                            .ge(dist_max).equation().post();
                }


            }
        }

        // On définit la fonction objective
        // On définit la variable de distance
        double distMax = Math.pow(maxx - minx, 2) + Math.pow(maxy - miny, 2) + Math.pow(maxz - minz, 2);
        minDistSquared = model.realVar("minDistSquared", 0.0, distMax, p);
        int t = 0;
        int nb_dist = n*(n-1)/2;
        RealVar[] distSquared = new RealVar[nb_dist];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if(this.numFctObj == 1) {
                    distSquared[t] = model.realVar("distSquared_" + i + "_" + j, 0.0, distMax, p);

                    // distSquared = distance_euclidienne(i,j)
                    xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                            .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                            .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                            .eq(distSquared[t].sqr()).equation().post();

                    // Assurer que cette distance est au moins la distance minimale
                    distSquared[t].ge(minDistSquared).post();
                    t += 1;
                }

                if(this.numFctObj == 2){
                    distSquared[t] = model.realVar("distSquared_"+i+"_"+j, 0.0, distMax, p);

                    if (liaisons[i][j] == 0){
                        // distSquared = distance_euclidienne(i,j)
                        xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                                .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                                .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                                .eq(distSquared[t].sqr()).equation().post();

                        // Assurer que cette distance est au moins la distance minimale
                        distSquared[t].ge(minDistSquared).post();
                    }
                    t += 1;
                }

                // FCT OBJ = somme des distances
                if(this.numFctObj == 3){
                    distSquared[t] = model.realVar("distSquared_"+i+"_"+j, 0.0, distMax, p);
                    // distSquared = distance_euclidienne(i,j)
                    xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                            .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                            .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                            .eq(distSquared[t].sqr()).equation().post();

                    t +=1;
                }




            }
        }

        if(this.numFctObj == 1){
            model.setObjective(Model.MINIMIZE, minDistSquared);
        } else if (this.numFctObj == 2) {
            model.setObjective(Model.MINIMIZE, minDistSquared);
        } else if (this.numFctObj == 3) {
            RealVar sumDist = model.realVar("sumDist", 0.0, nb_dist*distMax, p);
            String sum = "";
            Variable[] vars = new Variable[distSquared.length+1];
            for(t = 0; t<distSquared.length-1; t++){
                sum = sum + "{"+t+"}+";
                vars[t] = distSquared[t];
            }
            int t2 = t+1;
            vars[t] = distSquared[t];
            vars[t2] = sumDist;
            sum = sum + "{"+t+"} = {"+t2+"}";
            model.realIbexGenericConstraint(sum, vars).post();

            model.setObjective(Model.MAXIMIZE, sumDist);
        }


    }


    public RealVar[] getXs() {
        return xs;
    }

    public RealVar[] getYs() {
        return ys;
    }

    public RealVar[] getZs() {
        return zs;
    }
    public Model getModel() {
        return model;
    }
}
