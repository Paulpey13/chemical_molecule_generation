package src;

import java.util.Arrays;

public class Atom {
    private String[] type;
    private int[] quantities;

    public int nbAtom(){
        int sum = 0;
        for (int i = 0; i<quantities.length; i++) {
            sum += quantities[i];
        }
        return sum;
    }
    public String[] getType(){
       return type;
    }

    public int[] getQuantities() {
        return quantities;
    }

    public void setQuantities(int[] quantities) {
        this.quantities = quantities;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Atom{" +
                "type=" + Arrays.toString(type) +
                ", quantities=" + Arrays.toString(quantities) +
                '}';
    }
}