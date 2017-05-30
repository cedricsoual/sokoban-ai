import java.util.*;

public class AStar {
    static class Cell{
        int heuristicCost = 0; //Heuristic cost
        int finalCost = 0; //G+H
        int depth = 0;
        int i, j;
        Cell parent;

        Cell(int i, int j){
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString(){
            return "["+this.i+", "+this.j+"]";
        }
    }

    // contient les heuristiques (distance de manhattan jusqu'à la destination) pr chaque case ou null si c'est un mur
    static Cell [][] grid;
    // priorityqueue contenant les searchnodes
    static PriorityQueue<Cell> open;

    // exploreList
    static boolean closed[][];
    static int startI, startJ;
    static int endI, endJ;

    public static void setBlocked(int i, int j){
        grid[i][j] = null;
    }

    public static void setStartCell(int i, int j){
        startI = i;
        startJ = j;
    }

    public static void setEndCell(int i, int j){
        endI = i;
        endJ = j;
    }

    static void checkAndUpdateCost(Cell current, Cell t, int cost){
        if(t == null || closed[t.i][t.j])return;
        int t_final_cost = t.heuristicCost+cost;

        boolean inOpen = open.contains(t);
        if(!inOpen || t_final_cost<t.finalCost){
            t.finalCost = t_final_cost;
            t.parent = current;
            t.depth = current.depth + 1;
            if(!inOpen)open.add(t);
        }
    }

    public static void AStar(){
        //add the start location to open list.
        open.add(grid[startI][startJ]);

        Cell current;

        while(true){
            current = open.poll();
            if(current==null)break;
            // position vue (explorelist)
            closed[current.i][current.j]=true;

            if(current.equals(grid[endI][endJ])){
                return;
            }

            Cell t;
            // haut
            if(current.i-1>=0){
                t = grid[current.i-1][current.j];
                checkAndUpdateCost(current, t, current.finalCost);
            }

            // gauche
            if(current.j-1>=0){
                t = grid[current.i][current.j-1];
                checkAndUpdateCost(current, t, current.finalCost);
            }

            // droite
            if(current.j+1<grid[0].length){
                t = grid[current.i][current.j+1];
                checkAndUpdateCost(current, t, current.finalCost);
            }

            // bas
            if(current.i+1<grid.length){
                t = grid[current.i+1][current.j];
                checkAndUpdateCost(current, t, current.finalCost);
            }
        }
    }


    public static int getShortestPathLength(int nbRow, int nbCol, Plateau.Position source, Plateau.Position dest, Case[][] plateau){
        // case départ
        int si = source.getX();
        int sj = source.getY();
        // case destination
        int ei = dest.getX();
        int ej = dest.getY();
        grid = new Cell[nbRow][nbCol];
        closed = new boolean[nbRow][nbCol];
        open = new PriorityQueue<>((Object o1, Object o2) -> {
            Cell c1 = (Cell)o1;
            Cell c2 = (Cell)o2;

            return c1.finalCost-c2.finalCost;
        });

        setStartCell(si, sj);
        setEndCell(ei, ej);

        // calcul de l'heuristique pour chaque case : distance de Manhattan de la position (i,j) à la case destination
        for(int i=0;i<nbRow;++i){
            for(int j=0;j<nbCol;++j){
                grid[i][j] = new Cell(i, j);
                grid[i][j].heuristicCost = Math.abs(i-endI)+Math.abs(j-endJ);
            }
        }
        // on initialise à 0 la case de départ
        grid[si][sj].finalCost = 0;

        // on met les case de type MUR à null
        for(int i = 0; i<nbRow; i++)
            for(int j = 0;j<nbCol; j++)
                if(plateau[i][j].getStatut() == Case.MUR) setBlocked(i,j);;
        AStar();

        if(closed[endI][endJ]){
            return grid[endI][endJ].depth;
        }else {
            return 500;//No possible path;
        }
    }
}

