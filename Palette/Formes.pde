
enum Formes{
	NONE,
	RECTANGLE,
	CERCLE,
	TRIANGLE;

	public static Formes parseFormes(String s) {
		switch (s) {
			case "RECTANGLE" :
				return Formes.RECTANGLE;
				
			case "CERCLE" :
				return Formes.CERCLE;
				
			case "TRIANGLE" :
				return Formes.TRIANGLE;

			default :
				return Formes.NONE;
		}
	}
}
