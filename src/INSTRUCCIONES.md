# SISTEMA DE ARCHIVOS CLIENTE-SERVIDOR EN JAVA
# INSTRUCCIONES COMPLETAS

## PARTE 1: CONFIGURACIÓN INICIAL
IMPORTANTE: Ambas computadoras deben estar conectadas a la MISMA RED WiFi

1. CONFIGURAR EL FIREWALL DE WINDOWS (EN AMBAS COMPUTADORAS)

   Opción A - Desactivar temporalmente (MÁS FÁCIL):
    - Panel de Control
    - Sistema y seguridad
    - Firewall de Windows Defender
    - Activar o desactivar Firewall de Windows Defender
    - Desactivar para redes privadas

   Opción B - Crear regla (MÁS SEGURO):
    - Panel de Control
    - Firewall de Windows Defender
    - Configuración avanzada
    - Reglas de entrada
    - Nueva regla
    - Puerto → TCP → Puerto específico: 5000
    - Permitir la conexión
    - Nombre: "Java Sistema Archivos"

## PARTE 2: PREPARAR LOS ARCHIVOS

COMPUTADORA SERVIDOR:
1. Crea una carpeta: C:\SistemaArchivos
2. Guarda el archivo ServidorArchivos.java en esa carpeta

COMPUTADORA CLIENTE:
1. Crea una carpeta: C:\SistemaArchivos
2. Guarda el archivo ClienteArchivos.java en esa carpeta

## PARTE 3: EJECUTAR EL SERVIDOR

EN LA COMPUTADORA SERVIDOR:

1. Abre el "Símbolo del sistema" (CMD)
    - Presiona Windows + R
    - Escribe: cmd
    - Enter

2. Ve a la carpeta donde guardaste el archivo:
   cd C:\SistemaArchivos

3. Compila el servidor:
   javac ServidorArchivos.java

4. Ejecuta el servidor:
   java ServidorArchivos

5. ANOTA LA IP QUE APARECE EN PANTALLA
    - Ejemplo: 192.168.1.100
    - Esta IP se la darás al cliente

6. DÉJALO EJECUTÁNDOSE
    - No cierres esta ventana
    - El servidor debe estar corriendo cuando el cliente se conecte

NOTA: Si te da error de que Java no se reconoce:
- Asegúrate de tener Java JDK instalado
- Descárgalo de: https://www.oracle.com/java/technologies/downloads/

## PARTE 4: EJECUTAR EL CLIENTE

EN LA COMPUTADORA CLIENTE:

1. Abre el "Símbolo del sistema" (CMD)
    - Presiona Windows + R
    - Escribe: cmd
    - Enter

2. Ve a la carpeta donde guardaste el archivo:
   cd C:\SistemaArchivos

3. Compila el cliente:
   javac ClienteArchivos.java

4. Ejecuta el cliente:
   java ClienteArchivos

5. Cuando te pida la IP:
    - Escribe la IP que te dio el servidor
    - Ejemplo: 192.168.1.100
    - Presiona Enter

6. Si todo salió bien, verás "CONECTADO!"

## PARTE 5: USAR EL SISTEMA

EJEMPLO PASO A PASO:

1. CONECTAR AL SERVIDOR
    - Selecciona opción: 1
    - Verás: "OK: Conectado al servidor"

2. ABRIR UN ARCHIVO
    - Selecciona opción: 2
    - Nombre del archivo: miarchivo.txt
    - Verás: "OK: Archivo 'miarchivo.txt' abierto"

3. ESCRIBIR EN EL ARCHIVO
    - Selecciona opción: 3
    - Archivo: miarchivo.txt
    - Contenido: Hola, este es mi primer mensaje
    - Verás: "OK: Escrito en 'miarchivo.txt'"

4. LEER EL ARCHIVO
    - Selecciona opción: 4
    - Archivo a leer: miarchivo.txt
    - Verás el contenido que escribiste

5. CERRAR EL ARCHIVO
    - Selecciona opción: 5
    - Archivo a cerrar: miarchivo.txt
    - Verás: "OK: Archivo 'miarchivo.txt' cerrado"

6. DESCONECTARSE
    - Selecciona opción: 6
    - Se cierra la conexión

COMANDOS DISPONIBLES:
- CONNECT: Verifica que estás conectado
- OPEN: Abre un archivo (lo crea si no existe)
- WRITE: Escribe contenido en un archivo
- READ: Lee el contenido de un archivo
- CLOSE: Cierra un archivo
- DISCONNECT: Desconecta del servidor

## PARTE 6: DÓNDE SE GUARDAN LOS ARCHIVOS

Los archivos se guardan en la COMPUTADORA SERVIDOR en:
C:\SistemaArchivos\archivos\

Ejemplo: Si creaste "miarchivo.txt", estará en:
C:\SistemaArchivos\archivos\miarchivo.txt

## PARTE 7: SOLUCIÓN DE PROBLEMAS

PROBLEMA: "No se pudo conectar"

SOLUCIÓN 1 - Verifica que ambas computadoras estén en la misma WiFi:
- En ambas, abre CMD y escribe: ipconfig
- Verifica que la "Puerta de enlace predeterminada" sea la misma
- Ejemplo: 192.168.1.1 (debe ser igual en ambas)

SOLUCIÓN 2 - Verifica que el servidor esté corriendo:
- La ventana del servidor debe estar abierta
- Debe decir "Esperando cliente..."

SOLUCIÓN 3 - Verifica la IP del servidor:
- En el servidor, abre CMD
- Escribe: ipconfig
- Busca "Dirección IPv4" del adaptador WiFi
- Usa esa IP en el cliente

SOLUCIÓN 4 - Prueba la conexión:
- En el cliente, abre CMD
- Escribe: ping [IP_DEL_SERVIDOR]
- Ejemplo: ping 192.168.1.100
- Si dice "Tiempo de espera agotado" = hay problemas de red
- Si da respuesta = la red funciona

SOLUCIÓN 5 - Desactiva el Firewall temporalmente:
- En ambas computadoras
- Panel de Control → Firewall de Windows
- Desactivar para redes privadas

## PARTE 8: CONSEJOS IMPORTANTES

1. SIEMPRE ejecuta el SERVIDOR primero, luego el CLIENTE

2. NO cierres la ventana del servidor mientras el cliente esté conectado

3. Si necesitas RECONECTAR el cliente:
    - Cierra el cliente (opción 6 o Ctrl+C)
    - Vuelve a ejecutar: java ClienteArchivos

4. Si cambias el código:
    - Cierra servidor y cliente
    - Vuelve a compilar con javac
    - Vuelve a ejecutar

5. La IP del servidor puede cambiar si reinicias el router
    - Si eso pasa, vuelve a verificar la IP con ipconfig

## PARTE 9: COMANDOS RÁPIDOS DE REFERENCIA

COMPILAR:
javac ServidorArchivos.java
javac ClienteArchivos.java

EJECUTAR:
java ServidorArchivos
java ClienteArchivos

VER IP:
ipconfig

PROBAR CONEXIÓN:
ping [IP_del_servidor]

CAMBIAR DE CARPETA EN CMD:
cd C:\SistemaArchivos

## FIN DE LAS INSTRUCCIONES

Si tienes problemas, verifica:
- ✓ Misma red WiFi
- ✓ Servidor corriendo
- ✓ IP correcta
- ✓ Firewall desactivado o configurado
- ✓ Java instalado