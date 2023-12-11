package src;

public class BondDistance {
    private final int minDistance;
    private final int maxDistance;

    public BondDistance(int minDistance, int maxDistance) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    @Override
    public String toString() {
        return "(" + minDistance + ", " + maxDistance + ")";
    }
}


