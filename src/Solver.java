import java.io.*;
import java.util.*;

/**
 * Created by cedric on 11/04/2017.
 */
public class Solver {
    private static class SearchNode implements Comparable<SearchNode> {
        private final Plateau plateau;
        private final int moves;
        private final int priority;
        private final SearchNode parent;

        public SearchNode(SearchNode parent, Plateau plateau, int moves,
                          int priority) {
            this.parent = parent;
            this.plateau = plateau;
            this.moves = moves;
            this.priority = priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            //if (o == null || getClass() != o.getClass()) return false;
            SearchNode that = (SearchNode) o;
            return plateau.equals(that.plateau);
        }

        @Override
        public String toString() {
            return plateau.toString();
        }

        @Override
        public int hashCode() {
            return plateau.hashCode();
        }

        @Override
        public int compareTo(SearchNode searchNode) {
           return this.priority - searchNode.priority;
        }
    }

        private Plateau initialBoard;
        private boolean solvable = true;
        private SearchNode solution = null;

        public Solver(Plateau initial) {
            this.initialBoard = initial;
        }

        private void aStar() {
        Set<SearchNode> exploreList = new HashSet<>();
        PriorityQueue<SearchNode> searchNodes = new PriorityQueue<>();

        searchNodes.add(new SearchNode(null, initialBoard, 0, heuristic(initialBoard)));

        int i = 0;
        SearchNode current;
        while (!searchNodes.peek().plateau.isGoal() && i<100000) {
            i++;
            // on récupère le noeud le plus prioritaire
            current = searchNodes.poll();
            if(i%50 == 0) System.out.println(i+" noeuds explorés");

            for (Plateau next : current.plateau.neighbors()) {
                SearchNode sn = new SearchNode(current, next, current.moves + 1,
                        current.moves + 1 + heuristic(next));
                if (!searchNodes.contains(sn) && !exploreList.contains(sn)) searchNodes.add(sn);
            }
            exploreList.add(current);
        }
            SearchNode goal = searchNodes.peek();
            solution = goal;
            System.out.println("Goal ! "+"\nNb moves : "+ goal.moves+"\nNb noeuds explorés : "+exploreList.size());
        }

        private int heuristic(Plateau plateau) {
            return plateau.heuristicv1();
        }

        public Stack<Plateau> solution() {
            if (!solvable)
                return null;
            Stack<Plateau> res = new Stack<>();
            SearchNode current = solution;
            while (current != null) {
                res.push(current.plateau);
                current = current.parent;
            }
            return res;
        }


        public static void runSolver(String file) throws IOException {
            Case[][] blocks;
            blocks = Utils.readFile(file);
            Plateau initial = new Plateau(blocks);
            Solver solver = new Solver(initial);
            long startTime = System.currentTimeMillis();
            solver.aStar();
            long stopTime = System.currentTimeMillis();
            long millis = stopTime - startTime;
            System.out.println("Temps : "+Utils.formatTime(millis));

            // print solution to standard output
            System.out.println("---- Solution ---- ");
            System.out.println("Press \"ENTER\" to continue..."+"\n");

            Stack<Plateau> stack = solver.solution();
            // affichage 1 : dans le terminal en ASCII
             /* while (!stack.empty()){
                    Scanner scanner = new Scanner(System.in);
                    scanner.nextLine();
                    System.out.print(stack.pop());
                }*/
            // affichage 2 : UI
            new Ui().showSolution(stack);
        }

        public static void main(String[] args) {
            if(args.length != 3)
            {
                System.out.println("Proper Usage is: java program filename mode pondération_par_caisse");
                System.exit(0);
            }
            Plateau.SWITCH = Integer.parseInt(args[1]);
            Plateau.NB_CAISSE_HEURI = Integer.parseInt(args[2]);
            try {
                runSolver(args[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
