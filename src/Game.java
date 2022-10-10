import java.util.ArrayList;
import java.util.Arrays;

public class Game implements Comparable<Game>, Cloneable {
    int playerProfit = 0;
    int taxCollectorProfit = 0;
    ArrayList<Integer> moves = new ArrayList<>();
    boolean[] available; // Used to more efficiently determine if a cheque is available. We could use cheques.contains(i), but that is much less efficient than available[i]
    ArrayList<Integer>[] factors;
    int numOfCheques;

    public Game(int numOfCheques) {
        this.numOfCheques = numOfCheques;
        available = new boolean[numOfCheques + 1];
        for (int i = 1; i <= numOfCheques; i++)
            available[i] = true;

        // Create a reference table that holds all the factors of each number
        factors = new ArrayList[numOfCheques + 1];
        for (int i = 1; i <= numOfCheques; i++) {
            ArrayList<Integer> factorsList = new ArrayList<>();
            for (int j = 1; j <= i/2; j++)
                if (i%j==0)
                    factorsList.add(j);
            factors[i] = factorsList;
        }
    }

    public void take(int cheque) {
        if (available[cheque]) {
            // Make sure that the tax collector will have something to take
            if (factors[cheque].size() == 0)
                throw new IllegalArgumentException(String.format("Taking cheque %d from %s does not leave anything for the tax collector", cheque, Arrays.toString(available)));

            ArrayList<Integer> chequesTaken = new ArrayList<>();

            // Take your cheque
            playerProfit += cheque;
            available[cheque] = false;
            moves.add(cheque);
            chequesTaken.add(cheque);

            // Let the tax collector take his cheques
            for (int i : factors[cheque]) {
                taxCollectorProfit += i;
                available[i] = false;
                chequesTaken.add(i);
            }

            // Remove taken cheques from factors table
            for (int removedCheque : chequesTaken)
                for (int i = 1; i <= numOfCheques; i++)
                    factors[i].remove(Integer.valueOf(removedCheque));

        } else {
            throw new IllegalArgumentException(String.format("Cheque %d not available in %s", cheque, Arrays.toString(available)));
        }
    }

    public ArrayList<ArrayList<Integer>> possibleMoves() {
        ArrayList<ArrayList<Integer>> moves = new ArrayList<>();

        for (int i = 1; i <= numOfCheques; i++) {
            if (available[i] && factors[i].size() > 0) {
                ArrayList<Integer> move = new ArrayList<>();
                move.add(i);
                move.addAll(factors[i]);
                moves.add(move);
            }
        }

        return moves;
    }

    public int getNetProfit() {
        return playerProfit - taxCollectorProfit;
    }

    @Override
    public int compareTo(Game o) {
        return this.getNetProfit() - o.getNetProfit();
    }

    public Object clone() throws CloneNotSupportedException {
        Game clone = (Game) super.clone();
        clone.moves = (ArrayList<Integer>) this.moves.clone();
        clone.available = this.available.clone();
        ArrayList<Integer>[] clonedFactors = new ArrayList[numOfCheques + 1];
        for (int i = 1; i <= numOfCheques; i++) {
            clonedFactors[i] = (ArrayList<Integer>) factors[i].clone();
        }
        clone.factors = clonedFactors;


        return clone;
    }

    @Override
    public String toString() {
        return "Game{" +
                "profit=" + playerProfit +
                ", moves=" + moves +
                ", available=" + Arrays.toString(available) +
                ", factors=" + Arrays.toString(factors) +
                ", numOfCheques=" + numOfCheques +
                '}';
    }
}
