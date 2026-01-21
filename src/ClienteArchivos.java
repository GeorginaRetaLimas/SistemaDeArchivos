import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteArchivos {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Scanner sc;

    // Variable para saber si hay un archivo abierto
    private String archivoActual = null;

    public static void main(String[] args) {
        ClienteArchivos cliente = new ClienteArchivos();
        cliente.iniciar();
    }

    public void iniciar() {
        sc = new Scanner(System.in);

        System.out.println("*** CLIENTES DE ARCHIVOS ***");

        System.out.print("IP del servidor: ");
        String ip = sc.nextLine();

        conectar(ip);
    }

    private void conectar(String ip) {
        try {
            System.out.println("\nConectando a " + ip + "...");

            // Socket creamos la conexión al servidor (IP y puerto)
            socket = new Socket(ip, 5000);

            // Configuramos canales de comunicación
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            // Leemos mensaje de bienvenida del servidor
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
        while (true) {
            // Mostramos menú diferente según si hay archivo abierto o no
            if (archivoActual == null) {
                menuSinArchivo();
            } else {
                menuConArchivo();
            }
        }
    }

    // Menú cuando NO hay archivo abierto
    private void menuSinArchivo() {
        System.out.println("\n===== MENU PRINCIPAL =====");
        System.out.println("1. CONNECT    - Verificar conexión");
        System.out.println("2. OPEN       - Abrir archivo");
        System.out.println("3. DISCONNECT - Salir");
        System.out.println("==========================");
        System.out.print("Opción: ");

        String opcion = sc.nextLine();

        try {
            switch (opcion) {
                case "1":
                    enviar("CONNECT");
                    break;

                case "2":
                    abrirArchivo();
                    break;

                case "3":
                    enviar("DISCONNECT");
                    System.out.println("Desconectado");
                    socket.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Opción no válida");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Menú cuando SÍ hay archivo abierto
    private void menuConArchivo() {
        System.out.println("\n===== ARCHIVO ABIERTO: " + archivoActual + " =====");
        System.out.println("1. WRITE - Escribir en el archivo");
        System.out.println("2. READ  - Leer el archivo");
        System.out.println("3. CLOSE - Cerrar el archivo");
        System.out.println("=========================================");
        System.out.print("Opción: ");

        String opcion = sc.nextLine();

        try {
            switch (opcion) {
                case "1":
                    escribirArchivo();
                    break;

                case "2":
                    leerArchivo(archivoActual);
                    break;

                case "3":
                    cerrarArchivo();
                    break;

                default:
                    System.out.println("Opción no válida");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Método para abrir archivo
    private void abrirArchivo() throws Exception {
        System.out.print("- Nombre del archivo a abrir: ");
        String archivo = sc.nextLine();

        salida.println("OPEN " + archivo);
        String respuesta = entrada.readLine();
        System.out.println("Servidor: " + respuesta);

        // Si se abrió correctamente guardamos el nombre
        if (respuesta.startsWith("OK")) {
            archivoActual = archivo;
            System.out.println("\n¡Archivo abierto! Ahora puedes leer ó escribir :D");
        }
    }

    // Método para escribir
    private void escribirArchivo() throws Exception {
        System.out.print("- Contenido a escribir: ");
        String contenido = sc.nextLine();

        salida.println("WRITE " + archivoActual + "|" + contenido);
        String respuesta = entrada.readLine();
        System.out.println("Servidor: " + respuesta);
    }

    // Método para cerrar archivo
    private void cerrarArchivo() throws Exception {
        salida.println("CLOSE " + archivoActual);
        String respuesta = entrada.readLine();
        System.out.println("Servidor: " + respuesta);

        // Si se cerró correctamente limpiamos la variable
        if (respuesta.startsWith("OK")) {
            System.out.println("Volviendo al menú principal...");
            archivoActual = null;
        }
    }

    // Enviar comando y mostrar respuesta
    private void enviar(String comando) throws Exception {
        salida.println(comando);
        String respuesta = entrada.readLine();
        System.out.println("Servidor: " + respuesta);
    }

    // Leer archivos
    private void leerArchivo(String archivo) throws Exception {
        salida.println("READ " + archivo);
        String linea;

        // Leer hasta encontrar el marcador "FIN"
        while ((linea = entrada.readLine()) != null) {
            if (linea.equals("INICIO")) {
                System.out.println("\n|=== " + archivo + " ===|");
            } else if (linea.equals("FIN")) {
                System.out.println("|===---===|");
                break;
            } else {
                System.out.println(linea);
            }
        }
    }
}