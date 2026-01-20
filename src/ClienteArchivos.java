import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteArchivos {
    private Socket socket;

    // Para recibir respuestas del servidor
    private BufferedReader entrada;
    // Para enviar comandos al servidor
    private PrintWriter salida;

    private Scanner sc;

    public static void main(String[] args) {
        ClienteArchivos cliente = new ClienteArchivos();
        cliente.iniciar();
    }

    public void iniciar() {
        sc = new Scanner(System.in);

        System.out.println("╔══• CLIENTE DE ARCHIVOS •══╗\n");

        System.out.print("IP del servidor: ");
        String ip = sc.nextLine();

        conectar(ip);
    }

    private void conectar(String ip) {
        try {
            System.out.println("\nConectando a " + ip + "...");

            // Socket crea la conexión al servidor (IP y puerto)
            socket = new Socket(ip, 5000);

            // Configurar canales de comunicación
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            // Leer mensaje de bienvenida del servidor
            System.out.println(entrada.readLine());
            System.out.println("CONECTADO!\n");

            menu();

        } catch (Exception e) {
            System.out.println("\nERROR: No se pudo conectar");
            System.out.println("Verifica:");
            System.out.println("1. Que el servidor esté ejecutándose");
            System.out.println("2. Que la IP sea correcta");
            System.out.println("3. Que estén en la misma red WiFi");
        }
    }

    private void menu() {
        // Ciclo principal del menú
        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. CONNECT");
            System.out.println("2. OPEN");
            System.out.println("3. WRITE");
            System.out.println("4. READ");
            System.out.println("5. CLOSE");
            System.out.println("6. DISCONNECT");
            System.out.println("================");
            System.out.print("Opción: ");

            String opcion = sc.nextLine();

            try {
                switch (opcion) {
                    case "1":
                        enviar("CONNECT");
                        break;

                    case "2":
                        System.out.print("Nombre del archivo: ");
                        String archivo = sc.nextLine();
                        enviar("OPEN " + archivo);
                        break;

                    case "3":
                        System.out.print("Archivo: ");
                        String archivoW = sc.nextLine();
                        System.out.print("Contenido: ");
                        String contenido = sc.nextLine();
                        enviar("WRITE " + archivoW + "|" + contenido);
                        break;

                    case "4":
                        System.out.print("Archivo a leer: ");
                        String archivoR = sc.nextLine();
                        leerArchivo(archivoR);
                        break;

                    case "5":
                        System.out.print("Archivo a cerrar: ");
                        String archivoC = sc.nextLine();
                        enviar("CLOSE " + archivoC);
                        break;

                    case "6":
                        enviar("DISCONNECT");
                        System.out.println("Desconectado");
                        socket.close();
                        return;

                    default:
                        System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Función envíar comando y mostrar respuesta
    private void enviar(String comando) throws Exception {
        salida.println(comando);
        String respuesta = entrada.readLine();
        System.out.println("Servidor: " + respuesta);
    }

    // Función especial para leer archivos (maneja múltiples líneas)
    private void leerArchivo(String archivo) throws Exception {
        salida.println("READ " + archivo);
        String linea;

        // Leer hasta encontrar el marcador "FIN"
        while ((linea = entrada.readLine()) != null) {
            if (linea.equals("INICIO")) {
                System.out.println("\n--- Contenido del archivo ---");
            } else if (linea.equals("FIN")) {
                System.out.println("--- Fin ---");
                break;
            } else {
                System.out.println(linea);
            }
        }
    }
}