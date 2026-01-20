import java.io.*;
import java.net.*;

public class ServidorArchivo {
    // Puerto donde el servidor escuchará conexiones
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR DE ARCHIVOS (SIN HILOS) ===");
        System.out.println("NOTA: Solo puede atender UN cliente a la vez\n");
        System.out.println("Iniciando servidor...\n");

        try {
            // Crear el servidor en el puerto especificado
            ServerSocket servidor = new ServerSocket(PUERTO);

            // Obtener la IP de esta computadora para dársela al cliente
            String ip = InetAddress.getLocalHost().getHostAddress();

            System.out.println("SERVIDOR LISTO!");
            System.out.println("IP del servidor: " + ip);
            System.out.println("Puerto: " + PUERTO);
            System.out.println("\nDA ESTA IP AL CLIENTE: " + ip);
            System.out.println("Esperando cliente...\n");

            // Ciclo infinito para aceptar al cliente
            while (true) {
                // accept() hasta que haya una señal
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado!");

                atenderCliente(cliente);
                System.out.println("\nEsperando al cliente...\n");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Método que atiende al cliente
    private static void atenderCliente(Socket socket) {
        BufferedReader entrada = null;
        PrintWriter salida = null;

        try {
            // Configurar canales de entrada y salida
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            salida.println("Bienvenida dulcinea!");

            // Leer comandos del cliente continuamente
            String comando;
            while ((comando = entrada.readLine()) != null) {
                System.out.println("Comando recibido: " + comando);

                // Si el cliente se desconecta, salir del ciclo
                if (procesarComando(comando, salida, socket)) {
                    break;
                }
            }

            System.out.println("Cliente desconectado");

        } catch (Exception e) {
            System.out.println("Error atendiendo cliente: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {}
        }
    }

    // Procesa comandos y retorna true si el cliente da a desonectar
    private static boolean procesarComando(String comando, PrintWriter salida, Socket socket) {
        // Separamos el comando en acción y parámetros
        String[] partes = comando.split(" ", 2);
        String accion = partes[0].toUpperCase();

        try {
            switch (accion) {
                case "CONNECT":
                    salida.println("OK: Conectado al servidor");
                    break;

                case "OPEN":
                    String archivo = partes[1];
                    File f = new File("archivos/" + archivo);
                    f.getParentFile().mkdirs();
                    if (!f.exists()) f.createNewFile();
                    salida.println("OK: Archivo '" + archivo + "' abierto");
                    break;

                case "WRITE":
                    // Formato esperado: "archivo.txt|contenido a escribir"
                    String[] datos = partes[1].split("\\|", 2);
                    String nombreArchivo = datos[0];
                    String contenido = datos[1];

                    FileWriter fw = new FileWriter("archivos/" + nombreArchivo, true);
                    fw.write(contenido + "\n");
                    fw.close();

                    salida.println("OK: Escrito en '" + nombreArchivo + "'");
                    break;

                case "READ":
                    String archivoLeer = partes[1];
                    File fl = new File("archivos/" + archivoLeer);

                    if (!fl.exists()) {
                        salida.println("ERROR: Archivo no existe");
                        break;
                    }

                    BufferedReader br = new BufferedReader(new FileReader(fl));
                    salida.println("INICIO");
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        salida.println(linea);
                    }
                    salida.println("FIN");
                    br.close();
                    break;

                case "CLOSE":
                    salida.println("OK: Archivo '" + partes[1] + "' cerrado");
                    break;

                case "DISCONNECT":
                    salida.println("OK: Hasta luego!");
                    socket.close();
                    return true;  // Indicar que el cliente se desconectó

                default:
                    salida.println("ERROR: Comando desconocido");
            }
        } catch (Exception e) {
            salida.println("ERROR: " + e.getMessage());
        }

        return false;  // El cliente sigue conectado
    }
}