import java.util.*;

public class Plateau {
	public static int SWITCH = 2;
	public static int NB_CAISSE_HEURI = 0;

	private Case[][] plateau = new Case[16][19];
	private int nbLignes, nbColonnes;

	private Position posZoneR;
	private Position posPerso;

	public static final int MOVE_UP = 0;
	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_DOWN = 2;
	public static final int MOVE_LEFT = 3;

	public Plateau(Case[][] p)  {
		this.plateau= p;
		initialiserPlateau(p);
	}

	public Plateau() {
	}

	public void initialiserPlateau(Case[][] p) {
		nbLignes = 0;
		nbColonnes = 0;
		for(int i =0; i < 16; i++) {
			for(int j=0; j < 19;j++) {
			   if (p[i][j].getStatut() == Case.MUR) {
					nbLignes = i;
					nbColonnes = (j >= nbColonnes)?j:nbColonnes;
			    }
			    if (p[i][j].getStatut() == Case.PERSO || p[i][j].getStatut() == Case.PERSO_SUR_ZONE) posPerso = new Position(i,j);
				if(plateau[i][j].getStatut() == Case.ZONE_RANGEMENT) posZoneR = new Position(i,j);
				this.plateau[i][j] = new Case(p[i][j]);
			}
		}
	}

    // s'assure que le mouvement que l'ou souhaite faire est possible et que n'est pas une impasse pour la
    // résolution de la partie
	public boolean checkMovePerso(int typeMove){
		Position posMove;
		if(typeMove == MOVE_UP) posMove = new Position(posPerso.x - 1, posPerso.y);
		else if(typeMove == MOVE_RIGHT) posMove = new Position(posPerso.x, posPerso.y + 1);
		else if(typeMove == MOVE_DOWN) posMove = new Position(posPerso.x + 1, posPerso.y);
		else if(typeMove == MOVE_LEFT) posMove = new Position(posPerso.x, posPerso.y - 1);
		// unknown move
		else return false;
		// On s'assure qu'on veut déplacer le perso dans une case existante
		if(!(posMove.x >=0 && posMove.y >= 0 && posMove.x <= nbLignes && posMove.y <= nbColonnes)) return false;
        // Récupère le statut de la case sur laquelle on veut se déplacer
		int destStatus = this.plateau[posMove.x][posMove.y].getStatut();
        // On s'assure que la case sur laquelle on tente de déplacer le perso n'est pas un mur ou une caisse
		if (destStatus != Case.MUR) {
			if (destStatus != Case.CAISSE) return true;
			// cas où on doit pousser une caisse
			else {
                // On récupère la case se situant derrière la caisse
				Position posAfterCaisse;
				if(typeMove == MOVE_UP) posAfterCaisse = new Position(posPerso.x -2, posPerso.y);
				else if(typeMove == MOVE_RIGHT) posAfterCaisse = new Position(posPerso.x, posPerso.y + 2);
				else if(typeMove == MOVE_DOWN) posAfterCaisse = new Position(posPerso.x + 2, posPerso.y);
				else if(typeMove == MOVE_LEFT) posAfterCaisse = new Position(posPerso.x, posPerso.y - 2);
				// unknown move
				else return false;

				// On s'assure qu'on veut dépasser la caisse dans une case existante
				if(!(posAfterCaisse.x >=0 && posAfterCaisse.y >= 0 && posAfterCaisse.x <= nbLignes && posAfterCaisse.y <= nbColonnes)) return false;

                // Récupère le statut de la case se situant derrière la caisse
				int afterCaisseStatut = plateau[posAfterCaisse.x][posAfterCaisse.y].getStatut();

				// on verifie s'il la caisse est entourée de 2 murs ou plus : dans un coin => impasse
				if(afterCaisseStatut == Case.CASE_VIDE){
					// on veut check le nb de murs entourant la caisse plutôt
					int cmp = 0;
					boolean up = false, right = false, down = false, left = false;
					if(posAfterCaisse.x - 1 >= 0) {
						//en haut
						int upStatut = plateau[posAfterCaisse.x - 1][posAfterCaisse.y].getStatut();
						if (upStatut == Case.MUR) {up = true; cmp++;}
					}
					if(posAfterCaisse.x + 1 <= nbLignes) {
						//en bas
						int downStatut = plateau[posAfterCaisse.x + 1][posAfterCaisse.y].getStatut();
						if (downStatut == Case.MUR) {down = true; cmp++;}
					}
					if(posAfterCaisse.y - 1 >= 0) {
						//a gauche
						int leftStatut = plateau[posAfterCaisse.x][posAfterCaisse.y - 1].getStatut();
						if (leftStatut == Case.MUR) {left = true; cmp++;}
					}
					if(posAfterCaisse.y + 1 <= nbColonnes) {
						//a droite
						int rightStatut = plateau[posAfterCaisse.x][posAfterCaisse.y + 1].getStatut();
						if (rightStatut == Case.MUR) {right = true; cmp++;}
					}

					// cas ou le perso est entouré de 2 murs mais il peut passer
					if(cmp==2){
					if(typeMove == MOVE_LEFT || typeMove == MOVE_RIGHT) {if(up && down && !right && !left){
						return true;
					}}
					else if(typeMove == MOVE_UP || typeMove == MOVE_DOWN) {if(!up && !down && right && left){
						return true;
					}}}
					// return true si on a moins de 2 murs autour de la position.
					return cmp < 2;
				}
				// la caisse est arrivé sur la zone de rangement
				else if(afterCaisseStatut == Case.ZONE_RANGEMENT) return true;
			}
		}
		return false;
	}

	public void movePerso(int typeMove){
		Position posMove;
		if(typeMove == MOVE_UP) posMove = new Position(posPerso.x - 1, posPerso.y);
		else if(typeMove == MOVE_RIGHT) posMove = new Position(posPerso.x, posPerso.y + 1);
		else if(typeMove == MOVE_DOWN) posMove = new Position(posPerso.x + 1, posPerso.y);
		else if(typeMove == MOVE_LEFT) posMove = new Position(posPerso.x, posPerso.y - 1);
		// unknown move
		else return;
		int destStatus = this.plateau[posMove.x][posMove.y].getStatut();

		boolean mur = (destStatus == Case.MUR);
		boolean zone = (destStatus == Case.ZONE_RANGEMENT);
		boolean caisse = (destStatus == Case.CAISSE);
		boolean surZone =  (plateau[posPerso.x][posPerso.y].getStatut() == Case.PERSO_SUR_ZONE);

		if (destStatus != Case.MUR) {
			// on se déplace sur une zone de rangement ou une case vide
			if (destStatus != Case.CAISSE) {
				plateau[posPerso.x][posPerso.y].setStatut((surZone)? Case.ZONE_RANGEMENT: Case.CASE_VIDE);
				plateau[posMove.x][posMove.y].setStatut((zone)? Case.PERSO_SUR_ZONE: Case.PERSO);
				posPerso = posMove;
			}
			else {
				// position à laquelle la caisse va être déplacée
				Position posAfterCaisse;
				if(typeMove == MOVE_UP) posAfterCaisse = new Position(posPerso.x -2, posPerso.y);
				else if(typeMove == MOVE_RIGHT) posAfterCaisse = new Position(posPerso.x, posPerso.y + 2);
				else if(typeMove == MOVE_DOWN) posAfterCaisse = new Position(posPerso.x + 2, posPerso.y);
				else if(typeMove == MOVE_LEFT) posAfterCaisse = new Position(posPerso.x, posPerso.y - 2);
				// unknown move
				else return;

				// type de case à laquelle la caisse va être déplacée
				int afterCaisseStatut = plateau[posAfterCaisse.x][posAfterCaisse.y].getStatut();

				switch(afterCaisseStatut) {
					case Case.CASE_VIDE :
						plateau[posPerso.x][posPerso.y].setStatut((surZone)? Case.ZONE_RANGEMENT: Case.CASE_VIDE);
						plateau[posMove.x][posMove.y].setStatut((zone)? Case.PERSO_SUR_ZONE: Case.PERSO);
						plateau[posAfterCaisse.x][posAfterCaisse.y].setStatut(Case.CAISSE);
						posPerso = posMove;
						break;
					case Case.ZONE_RANGEMENT :
						plateau[posPerso.x][posPerso.y].setStatut((surZone)? Case.ZONE_RANGEMENT: Case.CASE_VIDE);
						plateau[posMove.x][posMove.y].setStatut((zone)? Case.PERSO_SUR_ZONE: Case.PERSO);
						plateau[posAfterCaisse.x][posAfterCaisse.y].setStatut(Case.ZONE_RANGEMENT);
						posPerso = posMove;
						break;
					default:
						return;
				}
			}
		}
	}

	// retourne la liste des voisons possibles
	public List<Plateau> neighbors() {
		List<Plateau> res = new ArrayList<Plateau>();
		Plateau next;

		// Haut
		if (checkMovePerso(MOVE_UP)) {
			next = this.clone();
			next.movePerso(MOVE_UP);
			res.add(next);
		}
		// Bas
		if (checkMovePerso(MOVE_RIGHT)) {
			next = this.clone();
			next.movePerso(MOVE_RIGHT);
			res.add(next);
		}
		// Droite
		if (checkMovePerso(MOVE_DOWN)) {
			next = this.clone();
			next.movePerso(MOVE_DOWN);
			res.add(next);
		}
		// Gauche
		if (checkMovePerso(MOVE_LEFT)) {
			next = this.clone();
			next.movePerso(MOVE_LEFT);
			res.add(next);
		}
		return res;
	}

	// distance de Manhattan : Heuristique 1 & 2
	public int calculDistance(Position d, Position p){
		return Math.abs(d.getX() - p.getX()) + Math.abs(d.getY() - p.getY());
	}

	// retourne une map (clé : distance de la caisse à la zone de rangement, valeur : liste des positions des caisses correspondantes)
	public Map<Integer, List<Position>> caisseProcheZoneR(){
		Map<Integer,List<Position>> map = new TreeMap<>();
		for(int i=0; i< nbLignes ; i++){
			for (int j=0 ; j< nbColonnes; j++){
                if(plateau[i][j].getStatut() == Case.CAISSE){
                    Position pos = new Position(i,j);
                    // distance de la caisse jusqu'à la zone de rangement
					int dist = 0;
					if(SWITCH==1) dist = calculDistance(posZoneR, pos);
					else if(SWITCH==2) dist = AStar.getShortestPathLength(nbLignes,nbColonnes,posZoneR,pos,plateau);

                    if(map.containsKey(dist)) map.get(dist).add(pos);
                    else{
                        List<Position> l = new ArrayList<>();
                        l.add(pos);
                        map.put(dist,l);
                    }
                }
			}
		}
		return map;
	}

	public int countCaisse() {
		int cmp=0;
		for(int i=0; i< nbLignes ; i++) {
			for (int j = 0; j < nbColonnes; j++) {
				if(plateau[i][j].getStatut() == Case.CAISSE) cmp++;
			}
		}
		return cmp;
	}

	//cherche la meilleure position où le perso doit se placer pour déplacer la caisse
	public Position searchOpposite(Position posCaisse){
		if(SWITCH==1){
       // si caisse à gauche de la zone
		if(posCaisse.y < posZoneR.y){
			return new Position(posCaisse.x, posCaisse.y-1);
        }
        // si caisse à droite de la zone
        else if(posCaisse.y > posZoneR.y){
			return new Position(posCaisse.x, posCaisse.y+1);
        }
        // si caisse au dessus de la zone (même colonne)
        else if(posCaisse.y == posZoneR.y && posCaisse.x < posZoneR.x){
			return new Position(posCaisse.x - 1, posCaisse.y);// si caisse à gauche de la zone
        }
        // si caisse en dessous de la zone (même colonne)
        else if (posCaisse.y ==posZoneR.y && posCaisse.x > posZoneR.x) {
			return new Position(posCaisse.x + 1, posCaisse.y);
		}
		}
		else if(SWITCH==2) {
			Position up = new Position(posCaisse.x - 1, posCaisse.y);
			Position down = new Position(posCaisse.x + 1, posCaisse.y);
			Position right = new Position(posCaisse.x, posCaisse.y + 1);
			Position left = new Position(posCaisse.x, posCaisse.y - 1);

			// caisse en haut à gauche de la zone
			if (posCaisse.y < posZoneR.y && posCaisse.x < posZoneR.x) {
				if (getCaseStatut(up) != Case.MUR) return up;
				else if (getCaseStatut(left) != Case.MUR) return left;
				else if (getCaseStatut(right) != Case.MUR) return right;
				return down;
			}
			// caisse en bas à droite de la zone
			else if (posCaisse.y > posZoneR.y && posCaisse.x > posZoneR.x) {
				if (getCaseStatut(down) != Case.MUR) return down;
				else if (getCaseStatut(right) != Case.MUR) return right;
				else if (getCaseStatut(left) != Case.MUR) return left;
				return up;
			}
			// caisse en bas à gauche de la zone
			else if (posCaisse.y < posZoneR.y && posCaisse.x > posZoneR.x) {
				if (getCaseStatut(down) != Case.MUR) return down;
				else if (getCaseStatut(left) != Case.MUR) return left;
				else if (getCaseStatut(right) != Case.MUR) return right;
				return up;
			}
			// caisse en haut à droite de la zone
			else if (posCaisse.y > posZoneR.y && posCaisse.x < posZoneR.x) {
				if (getCaseStatut(up) != Case.MUR) return up;
				else if (getCaseStatut(right) != Case.MUR) return right;
				else if (getCaseStatut(left) != Case.MUR) return left;
				return down;
			}
			// caisse sur la meme colonne en haut
			else if (posCaisse.y == posZoneR.y && posCaisse.x < posZoneR.x) {
				if (getCaseStatut(up) != Case.MUR) return up;
				else if (getCaseStatut(left) != Case.MUR) return left;
				else if (getCaseStatut(right) != Case.MUR) return right;
				return down;
			}
			// caisse sur la meme colonne en bas
			else if (posCaisse.y == posZoneR.y && posCaisse.x > posZoneR.x) {
				if (getCaseStatut(down) != Case.MUR) return down;
				else if (getCaseStatut(left) != Case.MUR) return left;
				else if (getCaseStatut(right) != Case.MUR) return right;
				return up;
			}
			// caisse sur la meme ligne à gauche
			else if (posCaisse.y < posZoneR.y && posCaisse.x == posZoneR.x) {
				if (getCaseStatut(left) != Case.MUR) return left;
				else if (getCaseStatut(up) != Case.MUR) return up;
				else if (getCaseStatut(down) != Case.MUR) return down;
				return right;
			}
			// caisse sur la meme ligne à droite
			else if (posCaisse.y > posZoneR.y && posCaisse.x == posZoneR.x) {
				if (getCaseStatut(right) != Case.MUR) return right;
				else if (getCaseStatut(up) != Case.MUR) return up;
				else if (getCaseStatut(down) != Case.MUR) return down;
				return left;
			}
		}
		return posCaisse;
	}

	public int heuristicv1(){
		int cmp = 0;
		Position posPersoTemp = posPerso;
        Map<Integer, List<Position>> map = caisseProcheZoneR();
        for(Map.Entry<Integer,List<Position>> entry : map.entrySet()){
            for(Position posCaisse : entry.getValue()){
                // on recherche où le perso doit se placer pour amener la caisse sur la zone
                Position bestPosToMove = searchOpposite(posCaisse);
                // déplacement du perso jusqu'à la caisse
				if(SWITCH==1) cmp += (calculDistance(posPersoTemp,bestPosToMove));
				else if(SWITCH==2)cmp += (AStar.getShortestPathLength(nbLignes,nbColonnes,posPersoTemp,bestPosToMove,plateau));
				cmp+=NB_CAISSE_HEURI;

				// déplacement de la caisse jusqu'à la zone de rangement
                cmp += entry.getKey();
                posPersoTemp = posZoneR;
            }
        }
		return cmp;
	}

	public Plateau clone() {
		Plateau clone = new Plateau();
		// Clone du plateau
		for(int i = 0; i< 16; i++)
			for(int j = 0; j< 19; j++)
				clone.plateau[i][j] = new Case(plateau[i][j].getStatut());
		clone.nbLignes = nbLignes;
		clone.nbColonnes = nbColonnes;
		clone.posPerso = posPerso.clone();
		clone.posZoneR = posZoneR.clone();
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		// if (o == null || getClass() != o.getClass()) return false;

		Plateau plateau1 = (Plateau) o;
		//if (!Arrays.deepEquals(plateau, plateau1.plateau)) return false;
		// les champs nbLignes, nbColonnes, posZoneR sont constants dans une partie
		for(int i = 0; i<nbLignes+1; i++)
			for(int j = 0;j<nbColonnes+1; j++)
				if(!plateau1.plateau[i][j].equals(this.plateau[i][j])) return false;

		return posPerso.equals(plateau1.posPerso);
	}
	
	@Override
	public int hashCode() {
		return Arrays.deepHashCode(plateau);
	}

	public Case getCase(int ligne, int colonne) {
		return plateau[ligne][colonne];
	}

	public Case getCase(Position p) {
		return plateau[p.x][p.y];
	}

	public int getCaseStatut(int ligne, int colonne) {
		return plateau[ligne][colonne].getStatut();
	}

	public int getCaseStatut(Position p) {
		return plateau[p.x][p.y].getStatut();
	}

	public int getNbLignes() {
		return nbLignes;
	}

	public int getNbColonnes() {
		return nbColonnes;
	}

	// retourne true si la partie est finie
	public boolean isGoal(){
		return countCaisse() == 0;
	}

	public Case[][] getPlateau() {
		return plateau;
	}

	public Position getPosZoneR() {
		return posZoneR;
	}

	public Position getPosPerso() {
		return posPerso;
	}

	@Override
	public String toString() {
		String s = "";
		for(int i=0;i<nbLignes+1;i++) {
			for (int j = 0; j <nbColonnes+1;j++) {
				switch(plateau[i][j].getStatut()) {
					case Case.PERSO :
						s += "@";
						break;
					case Case.CASE_VIDE :
						s += " ";
						break;
					case Case.MUR :
						s += "#";
						break;
					case Case.CAISSE_SUR_ZONE :
						s += "*";
						break;
					case Case.CAISSE :
						s += "$";
						break;
					case Case.ZONE_RANGEMENT :
						s += ".";
						break;
					case Case.PERSO_SUR_ZONE:
						s += "+";
						break;
					default:
						s += "_";
				}
			}
			s += "\n";
		}
		return s;
	}

	public class Position{
		private int x, y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Position position = (Position) o;
			if (x != position.x) return false;
			return y == position.y;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}

		public Position clone(){
			return new Position(x,y);
		}
	}
}
