# Programación de Objetos Distribuidos

### Primeros pasos
Es necesario correr la línea `mvn clean package install`, posteriormente descomprimir los archivos .tar.gz que se generan en las carpetas /client/target y /server/target.

### Levantando el RMI y el servidor
Primero hay que dirigirse a la carpeta /server/target/POD-client-1.0-SNAPSHOT y correr el archivo `run-registry.sh`. Una vez levantado el registry, levantamos el servidor corriendo el archivo `run-server.sh`. Ambos archivos deben tener permisos de ejecución. En el caso que se desee probar en Windows, también está el archivo `rmi-registry-windows.sh`.

### Utilizando los clientes
Hay que dirigirse a la carpeta /client/target/POD-client-1.0-SNAPSHOT. Allí tenemos cuatro archivos bash para correr, con el nombre en concordancia con los solicitados por la cátedra. A continuación algunos usos de los mismos:

#### Para iniciar las elecciones con el servidor en localhost:
./run-management.sh -DserverAddress="127.0.0.1" -Daction="open"

#### Para cerrar las elecciones con el servidor en localhost:
./run-management.sh -DserverAddress="127.0.0.1" -Daction="close"

#### Para consultar el estado de las elecciones con el servidor en localhost:
./run-management.sh -DserverAddress="127.0.0.1" -Daction="state"

#### Para agregar un fiscal del partido "TIGER" en la mesa 1000 y con el servidor en localhost:
./run-fiscal.sh -DserverAddress="127.0.0.1" -Dparty="TIGER" -Did=1000

#### Para cargar un archivo de votos llamado votes.csv que se encuentra a la misma altura de la terminal y con el servidor en localhost:
./run-vote.sh -DserverAddress="127.0.0.1" -DvotesPath="votes.csv"

#### Para consultar los resultados de la votación en la provincia "JUNGLE" y generando el archivo de resultados llamado "res.csv" con el servidor en localhost:
./run-query.sh -DserverAddress="127.0.0.1" -Dstate="JUNGLE" -DoutPath="res.csv"

#### Para consultar los resultados de la votación en la mesa 1000 y generando el archivo de resultados llamado "res.csv" con el servidor en localhost:
./run-query.sh -DserverAddress="127.0.0.1" -Did=1000 -DoutPath="res.csv"

#### Para consultar los resultados de la votación a nivel nacional y generando el archivo de resultados llamado "res.csv" con el servidor en localhost:
./run-query.sh -DserverAddress="127.0.0.1" -DoutPath="res.csv"

### Notas generales
Si bien probando en local el archivo de resultados en el path que se provee en el run-query.sh se crea correctamente solo pasando el nombre del archivo, en Pampero fue necesario dar un path absoluto para crearlo correctamente. 
# pod-tpe-1
