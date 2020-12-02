package sra.interpreter;

public enum Forme{
	NONE,
	RECTANGLE,
	CERCLE,
	TRIANGLE;

	public static Forme parseFormes(String s) {
		switch (s) {
			case "RECTANGLE" :
				return Forme.RECTANGLE;
				
			case "CERCLE" :
				return Forme.CERCLE;
				
			case "TRIANGLE" :
				return Forme.TRIANGLE;

			default :
				return Forme.NONE;
		}
	}
}
