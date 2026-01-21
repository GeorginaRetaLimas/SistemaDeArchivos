import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServidorArchivo {
    // Puerto donde el servidor escuchará conexiones
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("*** SERVIDOR DE ARCHIVOS ***");
        System.out.println("Iniciando servidor...\n");

        try {
            // Creamos el servidor en el puerto especificado
            ServerSocket servidor = new ServerSocket(PUERTO);

            // Obtenemos la IP de la lap
            String ip = InetAddress.getLocalHost().getHostAddress();

            System.out.println("SERVIDOR LISTO <3!");
            System.out.println("IP del servidor: " + ip);
            System.out.println("Puerto: " + PUERTO);
            System.out.println("- Esperando cliente...\n");

            Socket cliente = servidor.accept();
            System.out.println("- ¡Cliente conectado!");

            atenderCliente(cliente);

            System.out.println("\n*-*- CLIENTE DESCONECTADO -*-*");
            System.out.println("Bye bye desde el servidor");
            servidor.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Método que atiende al cliente
    private static void atenderCliente(Socket socket) {
        BufferedReader entrada = null;
        PrintWriter salida = null;

        // HashMap para manejar los archivos que estan abiertos
        HashMap<String, ArchivoAbierto> archivosAbiertos = new HashMap<>();

        try {
            // Configuramos canales de entrada y salida
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            salida.println("Bienvenida dulcinea!");

            // Leer comandos del cliente continuamente
            String comando;
            while ((comando = entrada.readLine()) != null) {
                System.out.println("- Comando recibido: " + comando);

                // Si el cliente se desconecta, salir del ciclo
                if (procesarComando(comando, salida, socket, archivosAbiertos)) {
                    break;
                }
            }

            System.out.println("- Cliente desconectado");

        } catch (Exception e) {
            System.out.println("Error atendiendo cliente: " + e.getMessage());
        } finally {
            // Cerrar todos los archivos abiertos antes de cerrar la conexión
            for (ArchivoAbierto arch : archivosAbiertos.values()) {
                try {
                    arch.cerrar();
                } catch (Exception e) {}
            }
            archivosAbiertos.clear();

            try {
                if (socket != null) socket.close();
            } catch (Exception e) {}
        }
    }

    // Procesa comandos y retorna true si el cliente da a desconectar
    private static boolean procesarComando(String comando, PrintWriter salida, Socket socket,
                                           HashMap<String, ArchivoAbierto> archivosAbiertos) {
        // Separamos el comando en acción y parámetros
        String[] partes = comando.split(" ", 2);
        String accion = partes[0].toUpperCase();

        try {
            switch (accion) {
                case "CONNECT":
                    salida.println("OK: Ya estas conectado");
                    break;

                case "OPEN":
                    String archivo = partes[1];

                    // Verificamos si ya está abierto
                    if (archivosAbiertos.containsKey(archivo)) {
                        salida.println("ERROR: Archivo '" + archivo + "' ya está abierto");
                        break;
                    }

                    File f = new File("archivos/" + archivo);
                    f.getParentFile().mkdirs();
                    if (!f.exists()) f.createNewFile();

                    // Creamos y guardar el archivo abierto en el HashMap
                    archivosAbiertos.put(archivo, new ArchivoAbierto(f));
                    salida.println("OK: Archivo '" + archivo + "' abierto");
                    break;

                case "WRITE":
                    // Formato recibido "archivo.txt|contenido a escribir"
                    String[] datos = partes[1].split("\\|", 2);
                    String nombreArchivo = datos[0];
                    String contenido = datos[1];

                    // Verificamos que el archivo esté abierto
                    ArchivoAbierto archivoAbierto = archivosAbiertos.get(nombreArchivo);
                    if (archivoAbierto == null) {
                        salida.println("ERROR: Debe abrir el archivo primero con OPEN");
                        break;
                    }

                    // Escribimos usando el writer ya abierto
                    archivoAbierto.writer.write(contenido + "\n");
                    archivoAbierto.writer.flush();  // Nos aseguramos que se escriba

                    salida.println("OK: Escrito en '" + nombreArchivo + "'");
                    break;

                case "READ":
                    String archivoLeer = partes[1];

                    // Verificamos que el archivo esté abierto
                    ArchivoAbierto archivoLectura = archivosAbiertos.get(archivoLeer);
                    if (archivoLectura == null) {
                        salida.println("ERROR: Debe abrir el archivo primero con OPEN ");
                        break;
                    }

                    // Leemos el archivo desde el File guardado
                    BufferedReader br = new BufferedReader(new FileReader(archivoLectura.archivo));
                    salida.println("INICIO");
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        salida.println(linea);
                    }
                    salida.println("FIN");
                    br.close();
                    break;

                case "CLOSE":
                    String archivoCerrar = partes[1];

                    // Verificamos que el archivo esté abierto
                    ArchivoAbierto archivoCerrado = archivosAbiertos.get(archivoCerrar);
                    if (archivoCerrado == null) {
                        salida.println("ERROR: Archivo '" + archivoCerrar + "' no está abierto");
                        break;
                    }

                    // Cerramos realmente el archivo
                    archivoCerrado.cerrar();
                    archivosAbiertos.remove(archivoCerrar);

                    salida.println("OK: Archivo '" + archivoCerrar + "' cerrado");
                    break;

                case "DISCONNECT":
                    // Cerramos todos los archivos abiertos
                    for (ArchivoAbierto arch : archivosAbiertos.values()) {
                        arch.cerrar();
                    }
                    archivosAbiertos.clear();

                    salida.println("OK: Hasta luego!");
                    socket.close();
                    return true;  // Indicamos que el cliente se desconectó

                default:
                    salida.println("ERROR: Comando desconocido");
            }
        } catch (Exception e) {
            salida.println("ERROR: " + e.getMessage());
        }

        return false;  // El cliente sigue conectado
    }

    // Para mantener información de archivos abiertos
    static class ArchivoAbierto {
        File archivo;
        FileWriter writer;

        public ArchivoAbierto(File archivo) throws IOException {
            this.archivo = archivo;
            // Creamos el writer cuando se abre
            this.writer = new FileWriter(archivo, true);
        }

        public void cerrar() throws IOException {
            if (writer != null) {
                writer.close();
            }
        }
    }
}