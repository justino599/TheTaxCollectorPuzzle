import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        Scanner in = new Scanner(System.in);

        // Move old logs to archive directory. Only newest 5 in main logs dir
        File logDir = new File("logs");
        logDir.mkdir();

        File logArchive = new File("logs/archived");
        logArchive.mkdir();

        File[] logs = logDir.listFiles(pathname -> !pathname.getName().equals("archived"));

        if (logs.length > 5) {
            Arrays.sort(logs, (o1, o2) -> (int) (o1.lastModified() - o2.lastModified()));
            for (int i = 0; i < logs.length - 5; i++) {
                logs[i].renameTo(new File("logs/archived/" + logs[i].getName()));
                logs[i].delete();
            }
        }

        System.out.println("Algorithms:\n(1) Brute Force\n(2) Algo 1");
        System.out.print("Choose an algorithm: ");
        int algorithm = in.nextInt();

        System.out.print("Verbose output (y/n)? ");
        boolean verbose = in.next().toLowerCase(Locale.ROOT).charAt(0) == 'y';

        System.out.print("Run for which numbers (end, step, start): ");
        in.nextLine();
        String input = in.nextLine().strip().replace(",", "");
        Scanner in2 = new Scanner(input);
        int numCheques = in2.nextInt();
        int step;
        int start;
        if (in2.hasNextInt())
            step = in2.nextInt();
        else
            step = 2;
        if (in2.hasNextInt())
            start = in2.nextInt();
        else
            start = 2;
        in2.close();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        File f = new File("logs/" + dtf.format(now) + ".log");
        f.createNewFile();

        try (PrintWriter out = new PrintWriter(f)) {
            out.println("Algorithm: " + algorithm + "\nStart: " + start + "\nEnd: " + numCheques + "\nStep: " + step);
        }

        for (int i = Math.max(2, start); i <= numCheques; i += step) {
            long startTime = System.currentTimeMillis();

            Game game = new Game(i);
            Game bestGame = switch (algorithm) {
                case 1 -> bruteForceBestGame(game);
                case 2 -> algo1(game);
                default -> game;
            };

            // Print and log output
            try (PrintWriter logger = new PrintWriter(new FileOutputStream(f, true))) {
                String output = verbose ? i + " cheques: Profit of $" + bestGame.playerProfit + ", using moves: " + bestGame.moves + " (" + (System.currentTimeMillis() - startTime) + "ms)" : String.format("%-4s| $%s", i, bestGame.playerProfit);

                logger.println(output);
                System.out.println(output);
            }
        }
    }

    public static Game algo1(Game game) {
        // Take the largest prime first
        int largestPrime = 2;
        for (int i = game.numOfCheques; i > 2; i--) {
            if (prime(i)) {
                largestPrime = i;
                break;
            }
        }
        game.take(largestPrime);

        // Then take cheques with only 1 remaining factor. Choose cheque with the largest difference between user's cheque and tax collector's cheque(s)
        ArrayList<ArrayList<Integer>> moves;
        while ((moves = game.possibleMoves()).size() > 0) {
            // Sort moves from best to worst according to the above described algorithm
            moves.sort((o1, o2) -> {
                if (o1.size() != o2.size()) {
                    // Sort moves that have the tax collector taking less cheques to the top
                    return o1.size() - o2.size();
                } else {
                    // Sort moves that have a greater difference between user and tax collector payouts to the top
                    int diff1 = o1.get(0) - sumOfFactors(o1);
                    int diff2 = o2.get(0) - sumOfFactors(o2);
                    return diff2 - diff1;
                }
            });

            // Make the best move
            game.take(moves.get(0).get(0));
        }

        return game;
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
        } else {
            return game;
        }
    }

    public static ArrayList<Integer> factors(int i) {
        ArrayList<Integer> factorsList = new ArrayList<>();
        for (int j = 1; j <= i / 2; j++)
            if (i % j == 0)
                factorsList.add(j);
        return factorsList;
    }

    public static boolean prime(int i) {
        return factors(i).size() < 2;
    }

    public static int sumOfFactors(ArrayList<Integer> move) {
        int sum = 0;
        for (int i = 1; i < move.size(); i++)
            sum += move.get(i);
        return sum;
    }
}
