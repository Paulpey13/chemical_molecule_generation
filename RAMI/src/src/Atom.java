package src;

import java.util.Arrays;

public class Atom {
    private String[] types;
    private int[] quantities;

    public int nbAtom(){
        int sum = 0;
        for (int i = 0; i<quantities.length; i++) {
            sum += quantities[i];
        }
        return sum;
    }

    public int nbTypes(){
        return quantities.length;
    }

    public String[] getTypes(){
       return types;
    }

    public int[] getQuantities() {
        return quantities;
    }

    public void setQuantities(int[] quantities) {
        this.quantities = quantities;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "Atom{" +
                "types=" + Arrays.toString(types) +
                ", quantities=" + Arrays.toString(quantities) +
                '}';
    }
}