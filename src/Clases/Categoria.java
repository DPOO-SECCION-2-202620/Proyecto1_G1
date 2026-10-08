package Clases;

public enum Categoria {

	SUB13(0, 12),
    SUB15(13, 14),
    SUB17(15, 16),
    MAYORES(17, 150);
	
	
	private final int edadMinima;
    private final int edadMaxima;
 
    Categoria(int edadMinima, int edadMaxima) {
        this.edadMinima = edadMinima;
        this.edadMaxima = edadMaxima;
    }
 
    public boolean admiteEdad(int edad) {
        return edad >= edadMinima && edad <= edadMaxima;
    }
 
    public int getEdadMinima() {
        return edadMinima;
    }
 
    public int getEdadMaxima() {
        return edadMaxima;
    }
}
