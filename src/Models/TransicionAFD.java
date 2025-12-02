package Models;

public class TransicionAFD {
    int origen;
    char simbolo;
    int destino;

    TransicionAFD(int origen, char simbolo, int destino) {
        this.origen = origen;
        this.simbolo = simbolo;
        this.destino = destino;
    }
}
