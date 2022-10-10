import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        for (int i = 2; i <= 36; i+=2) {
            long start = System.currentTimeMillis();

            Game game = new Game(i);
            Game bestGame = bruteForceBestGame(game);

            System.out.println(i + " cheques: Profit of $" + bestGame.playerProfit + ", using moves: " + bestGame.moves + " (" + (System.currentTimeMillis() - start) + "ms)");
        }
    }

    public static Game bruteForceBestGame(Game game) throws CloneNotSupportedException {
        ArrayList<ArrayList<Integer>> moves = game.possibleMoves();

        ArrayList<Game> games = new ArrayList<>();

        for (ArrayList<Integer> move : moves) {
            Game clonedGame = (Game) game.clone();
            clonedGame.take(move.get(0));
            games.add(bruteForceBestGame(clonedGame));
        }

        if (games.size() > 0) {
            Game max = games.get(0);
            for (int i = 1; i < games.size(); i++)
                if (games.get(i).compareTo(max) > 0)
                    max = games.get(i);

            return max;
        }
        else {
            return game;
        }
    }
}
