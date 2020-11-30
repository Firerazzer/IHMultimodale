package sra.interpreter;

public class TestInterpreter {
	public static void main(String[] args) {
		ObjType obj = Interpreter.getType("creer");
		System.out.println(obj.getObj());
		System.out.println(obj.getType());
	}
}
