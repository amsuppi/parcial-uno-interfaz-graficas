package interfaz.grafica;

import interfaz.grafica.Modelos.Clima;
import interfaz.grafica.Modelos.Ecosistema;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===== SIMULADOR DE ECOSISTEMA =====");

        // configuracion inicial (se repite hasta que el jugador confirme)
        int cantPlantas, cantConejos, cantLobos, cantTurnos;
        Clima clima;
        do {
            cantPlantas = leerEntero("Cantidad inicial de plantas (5-30): ", 5, 30);
            cantConejos = leerEntero("Cantidad inicial de conejos (2-15): ", 2, 15);
            cantLobos = leerEntero("Cantidad inicial de lobos (1-" + Ecosistema.MAX_LOBOS + "): ", 1, Ecosistema.MAX_LOBOS);
            clima = elegirClima();
            cantTurnos = leerEntero("Cantidad de turnos (10-50): ", 10, 50);

            System.out.println();
            System.out.println("-- Resumen --");
            System.out.println("Plantas: " + cantPlantas + " | Conejos: " + cantConejos
                    + " | Lobos: " + cantLobos + " | Clima: " + clima + " | Turnos: " + cantTurnos);
        } while (!confirmar("¿Iniciar con esta configuración?"));

        Ecosistema eco = new Ecosistema(clima);
        eco.inicializar(cantPlantas, cantConejos, cantLobos);

        // loop principal
        while (eco.getTurnoActual() < cantTurnos && !eco.ecosistemaColapsado()) {
            eco.procesarTurno();

            if (eco.ecosistemaColapsado()) {
                break; // no tiene sentido intervenir si ya se extinguio algo
            }

            System.out.print(">>> Presione Enter para continuar...");
            sc.nextLine();

            // cada 3 turnos puede intervenir (menos en el ultimo)
            if (eco.getTurnoActual() % 3 == 0 && eco.getTurnoActual() < cantTurnos) {
                intervenir(eco);
            }
        }

        eco.generarReporteFinal();
    }

    private static void intervenir(Ecosistema eco) {
        System.out.println();
        System.out.println("=== INTERVENCIÓN (cada 3 turnos) ===");
        System.out.println("1. Cambiar clima (actual: " + eco.getClimaActual() + ")");
        System.out.println("2. Agregar entidad");
        System.out.println("3. Solo avanzar");
        int opcion = leerEntero("Opción: ", 1, 3);

        switch (opcion) {
            case 1:
                Clima nuevo = elegirClima();
                if (confirmar("¿Cambiar el clima a " + nuevo + "?")) {
                    eco.cambiarClima(nuevo);
                }
                break;
            case 2:
                String tipo = leerTipoEntidad(eco);
                if (tipo != null && confirmar("¿Agregar un/a " + tipo + "?")) {
                    eco.agregarEntidad(tipo);
                }
                break;
            default:
                System.out.println("Se avanza sin intervenir.");
        }
    }

    private static String leerTipoEntidad(Ecosistema eco) {
        while (true) {
            System.out.print("¿Qué entidad agregar? (planta/conejo/lobo): ");
            String tipo = sc.nextLine().trim().toLowerCase();
            if (tipo.equals("planta") || tipo.equals("conejo")) {
                return tipo;
            }
            if (tipo.equals("lobo")) {
                // el limite es de toda la simulacion, no de los vivos
                if (!eco.puedeAgregarLobo()) {
                    System.out.println("Ya hubo " + Ecosistema.MAX_LOBOS + " lobos en la simulación, no se pueden agregar más.");
                    return null;
                }
                return tipo;
            }
            System.out.println("Tipo inválido, escribí planta, conejo o lobo.");
        }
    }

    private static Clima elegirClima() {
        System.out.println("Climas: 1) Soleado  2) Lluvioso  3) Sequía  4) Invierno");
        int op = leerEntero("Elegí el clima (1-4): ", 1, 4);
        return Clima.desdeOpcion(op);
    }

    // pide un numero hasta que este dentro del rango
    private static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("Tiene que estar entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Eso no es un número válido.");
            }
        }
    }

    private static boolean confirmar(String pregunta) {
        while (true) {
            System.out.print(pregunta + " (s/n): ");
            String r = sc.nextLine().trim().toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) return true;
            if (r.equals("n") || r.equals("no")) return false;
            System.out.println("Respondé s o n.");
        }
    }
}
