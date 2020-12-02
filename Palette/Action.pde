
enum Action {
	NONE,
	CREER,
	SUPPRIMER,
	DEPLACER;

	public static Action parseAction(String s) {
		switch (s) {
			case "CREER" :
				return Action.CREER;
				
			case "SUPPRIMER" :
				return Action.SUPPRIMER;
				
			case "DEPLACER" :
				return Action.DEPLACER;

			default :
				return Action.NONE;
		}
	}
}
