import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorArchivo {
    // Puerto donde el servidor escuchará las conexiones
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("╔══• Servidor •══╗");
        System.out.println("Iniciando servidor...\n");

        try{
            // Crear el gina servidor en el puerto especificado
            ServerSocket servidor = new ServerSocket(PUERTO);

            // Obtener la IP de esta computadora para dársela al cliente
            String ip = InetAddress.getLocalHost().getHostAddress();

            System.out.println("¡Servidor listo! (˶ᵔ ᵕ ᵔ˶)");
            System.out.println("IP del Servidor: " + ip);
            System.out.println("Puerto: " + PUERTO + "\n");
            System.out.println("Damos esta ip al cliente: " + ip);
            System.out.println("Esperando al cliente...\n");

            // Ciclo infinito para aceptar a multiples clientes
            while(true){
                // Con acept() esperamos a que un cliente se conecte
                Socket cliente = servidor.accept();

                // Obtener la IP de esta computadora para darsela a dulce cliente
                System.out.println("¡Cliente conectado! ( ˶ˆᗜˆ˵ )");

                // Crear un nuevo hilo para manejar a este cliente
                // Nos ayudaea a manejar multiples clientes que se conectan al mismo tiempo
                new Thread(new ManejadorCliente(cliente)).start();

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Clase con la que manejaremos la comunicacion del cliente
    static class ManejadorCliente implements Runnable{
        private Socket socket;

        // Para recibir datos del cliente
        private BufferedReader entrada;

        // Para enviar los datos al cliente
        private PrintWriter salida;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try{
                // Configurar canales de entrada y salida
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(socket.getOutputStream(), true);

                // Enviar mensaje de bienvenida al cliente
                salida.println("Bienvenida al servidor Dulcin (≧◡≦)");

                // Leer comandos del cliente continuamente
                String comando;
                while((comando = entrada.readLine()) != null) {
                    System.out.println("Comando recibido: " + comando);

                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        private void procesarComando(String comando){
            // Vamos a separar los comandos en acciones y parametros
            // De "OPEN archivo.txt" tendremos ["OPEN", "archivo.txt"]
            String[] partes = comando.split(" ", 2);
            String accion = partes[0].toUpperCase();

            try{
                switch (accion) {
                    case "CONNECT":
                        salida.println("Conectado al servior");
                        break;

                    case "OPEN":
                        String archivo = partes[1];

                        File f = new File("archivos/" + archivo);
                        f.getParentFile().mkdirs();

                        if(!f.exists()){
                            f.createNewFile();
                        }
                        salida.println("Archivo: " + archivo + " abierto");
                        break;

                    case "WRITE":
                        String[] datos = partes[1].split("\\|", 2);
                        String nombreArchivo = datos[0];
                        String contenido = datos[1];

                        // FileWriter con 'true' agrega contenido al final (append)
                        FileWriter fw = new FileWriter("archivos/" + nombreArchivo, true);
                        fw.write(contenido + "\n");
                        fw.close();

                        salida.println("Escrito en '" + nombreArchivo + "'");
                        break;

                    case "READ":
                        String archivoLeer = partes[1];
                        File fl = new File("archivos/" + archivoLeer);

                        if (!fl.exists()) {
                            salida.println("ERROR: Archivo no encontrado");
                            break;
                        }

                        // Leer archivo línea por línea
                        BufferedReader br = new BufferedReader(new FileReader(fl));
                        salida.println("Inicio...");
                        String linea;

                        while ((linea = br.readLine()) != null) {
                            salida.println(linea);
                        }
                        salida.println("Fin...");
                        br.close();
                        break;

                    case "CLOSE":
                        salida.println("Archivo '" + partes[1] + "' cerrado");
                        break;

                    case "DISCONNECT":
                        salida.println("Adios Dulce");
                        // Cerrar la conexión
                        socket.close();
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }

}
