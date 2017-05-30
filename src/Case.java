
public class Case {
	// Declarations de champs entier statiques pour chaque statut possible d'une case
	public static final int CASE_VIDE = 0;
	public static final int MUR = 1;
	public static final int CAISSE = 2;
	public static final int PERSO = 3;
	public static final int ZONE_RANGEMENT = 4;
	public static final int PERSO_SUR_ZONE = 5;
	public static final int CAISSE_SUR_ZONE = 6;
	
	private int statut;
	
    public Case(int statut) {
		this.statut = statut;
	}

    public Case(Case c) {
		setStatut(c.getStatut());
    }
	
	public int getStatut() {
		return statut;
	}
	
	public void setStatut(int newStatut) {
		this.statut = newStatut;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		// if (o == null || getClass() != o.getClass()) return false;
		Case aCase = (Case) o;
		return statut == aCase.statut;
	}

	@Override
	public int hashCode() {
		return statut;
	}

}
