import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
data class PeliculaBinaria(val idPeliculaJSON: Int, val tituloPeliJSON: String, val directorJSON: String, val generoJSON: String, val duracionHorasJSON: Double)

const val TAMANO_ID = Int.SIZE_BYTES // 4 bytes
const val TAMANO_TITULO = 40 // String de tamaño fijo 20 bytes
const val TAMANO_DIECTOR = 25 // 8 bytes
const val TAMANO_GENERO = 10
const val TAMANO_DURACION = Double.SIZE_BYTES
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_TITULO + TAMANO_DIECTOR + TAMANO_GENERO + TAMANO_GENERO

//Función que crea un fichero (si no existe) o lo vacía (si existe)
//Si el fichero existe: CREATE se ignora. TRUNCATE_EXISTING se activa y vacía el fichero en el momento de abrirlo.
//Si el fichero no existe: CREATE se activa y crea un fichero nuevo y vacío. TRUNCATE_EXISTING se ignora porque el fichero no existía previamente a la operación open.

fun vaciarCrearFichero(path: Path) {
    try {
// AÑADIMOS StandardOpenOption.CREATE
// WRITE: Permite la escritura.
// CREATE: Crea el fichero si no existe.
// TRUNCATE_EXISTING: Si el fichero ya existía, lo vacía. Si fue recién creado, esta opción se ignora.
        FileChannel.open(path, StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).close()
        println("El fichero '${path.fileName}' existe y está vacío.")
    } catch (e: Exception) {
        println("Error al vaciar o crear el fichero: ${e.message}")
    }
}
fun anadirPelicula(path: Path, idPeliculaJSON: Int, tituloPeliJSON: String, directorJSON: String, generoJSON: String, duracionHorasJSON: Double) {
    val nuevaPeli = PeliculaBinaria(idPeliculaJSON, tituloPeliJSON, directorJSON, generoJSON, duracionHorasJSON)
// Abrimos el canal en modo APPEND.
// CREATE: crea el fichero si no existe.
// WRITE: es necesario para poder escribir.
// APPEND: asegura que cada escritura se haga al final del fichero.
    try {
        FileChannel.open(path, StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { canal ->
// Creamos un buffer para la nueva planta
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
// Llenamos el buffer con los datos de la nueva planta
// (misma lógica que en la función de escritura inicial)
            38
            buffer.putInt(nuevaPeli.idPeliculaJSON)
            val nombreBytes = nuevaPeli.tituloPeliJSON
                .padEnd(20, ' ')
                .toByteArray(Charset.defaultCharset())
            buffer.put(nombreBytes, 0, 20)
            buffer.putDouble(nuevaPeli.duracionHorasJSON)
            buffer.flip()
// Escribimos el buffer en el canal. Gracias a APPEND,
// se escribirá al final del fichero.
            while (buffer.hasRemaining()) {
                canal.write(buffer)
            }
            println("Pelicula '${nuevaPeli.tituloPeliJSON}' añadida con éxito.")
        }
    } catch (e: Exception) {
        println("Error al añadir la Pelicula: ${e.message}")
    }
}
fun leerPeliculas(path: Path): List<PeliculaBinaria> {
    val plantas = mutableListOf<PeliculaBinaria>()
// Abrimos un canal de solo lectura al fichero
    FileChannel.open(path, StandardOpenOption.READ).use { canal ->
// Buffer para leer un registro a la vez
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        while (canal.read(buffer) > 0) {
            buffer.flip()
            val id = buffer.getInt()
            val nombreBytes = ByteArray(TAMANO_TITULO)
            buffer.get(nombreBytes)
            val nombre = String(nombreBytes,
                Charset.defaultCharset()).trim()
            val altura = buffer.getDouble()
            plantas.add(PlantaBinaria(id, nombre, altura))
            buffer.clear()
        }
    }
    return plantas
}

fun modificarAlturaPlanta(path: Path, idPlanta: Int, nuevaAltura: Double)
{
// Abrimos un canal con permisos de lectura y escritura
    FileChannel.open(path, StandardOpenOption.READ,
        StandardOpenOption.WRITE).use { canal ->
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        var encontrado = false
        while (canal.read(buffer) > 0 && !encontrado) {
            /*canal.read(buffer) lee un bloque completo de 32 bytes
            y los guarda en el buffer. Después de esta operación,
            el "puntero" o "cursor" interno del canal (canal.position())
            ha avanzado 32 bytes y ahora se encuentra al final
            del registro que acabamos de leer.*/
            val posicionActual = canal.position()
            buffer.flip()
            val id = buffer.getInt()
            if (id == idPlanta) {
// Hemos encontrado el registro. Calculamos la posición del campo 'altura'.
// Posición de inicio del registro (actual - registro) + 4 bytes (id) + 20 bytes (nombre)
                val posicionAltura = posicionActual - TAMANO_REGISTRO +
                        TAMANO_ID + TAMANO_NOMBRE
// Creamos un buffer solo para el double
                val bufferAltura = ByteBuffer.allocate(TAMANO_ALTURA)
                bufferAltura.putDouble(nuevaAltura)
                bufferAltura.flip()
// Escribimos el nuevo valor en la posición exacta del fichero
                canal.write(bufferAltura, posicionAltura)
                encontrado = true
            }
            buffer.clear()
        }
        if (encontrado) {
            println("Altura de la planta con ID $idPlanta modificada a$nuevaAltura")
        } else {
            println("No se encontró la planta con ID $idPlanta")
        }
    }
}

fun eliminarPlanta(path: Path, idPlanta: Int) {
// Creamos un fichero temporal en el mismo directorio
    val pathTemporal = Paths.get(path.toString() + ".tmp")
    var plantaEncontrada = false
// Abrimos el canal de lectura para el fichero original
    FileChannel.open(path, StandardOpenOption.READ).use { canalLectura ->
// Abrimos el canal de escritura para el fichero temporal
        FileChannel.open(pathTemporal, StandardOpenOption.WRITE,
            StandardOpenOption.CREATE).use { canalEscritura ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
// Leemos el fichero original registro a registro
            while (canalLectura.read(buffer) > 0) {
                buffer.flip()
                val id = buffer.getInt() // Solo necesitamos el ID
                if (id == idPlanta) {
                    plantaEncontrada = true
// Si es la planta a eliminar, no hacemos nada.
// El buffer se limpiará para la siguiente lectura.
                } else {
// Si NO es la planta a eliminar, escribimos el registro
// completo en el fichero temporal.
                    buffer.rewind() // Rebobinamos para ir al principio
                    canalEscritura.write(buffer)
                }
                buffer.clear() // Preparamos para siguiente iteración
            }
        }
    }
    if (plantaEncontrada) {
        44
// Si se encontró y eliminó, reemplazar fichero original con el temporal
        Files.move(pathTemporal, path, StandardCopyOption.REPLACE_EXISTING)
        println("Planta con ID $idPlanta eliminada con éxito.")
    } else {
// Si no se encontró, no hace falta hacer nada, borramos el temporal
        Files.delete(pathTemporal)
        println("No se encontró ninguna planta con ID $idPlanta.")
    }
}


fun main() {
    val archivoPath: Path = Paths.get("datos_fin/binario/datos.bin")
    val lista = listOf(
        PlantaBinaria(1, "El Origen", "Christopher Nolan","Ciencia Ficción", 2.48 ),
        PlantaBinaria(2, "El Padrino", "Francis Ford Coppola","Crimen", 2.92),
        PlantaBinaria(3, "Tiempos Violentos", "Quentin Tarantino", "Crimen",2.54),
        PlantaBinaria(3, "Sueños de Fuga", "Frank Darabont", "Drama", 2.37),
        PlantaBinaria(3, "Forrest Gump", "Robert Zemeckis", "Drama", 2.33)
    )
// vaciar o crear el fichero
    vaciarCrearFichero(archivoPath)
// --- Añadir las plantas al fichero
    lista.forEach { pelicula ->
        anadirPelicula(archivoPath, pelicula.idPeliculaJSON, pelicula.tituloPeliJSON, pelicula.directorJSON, pelicula.generoJSON, pelicula.duracionHorasJSON)
    }
// --- Leer el fichero binario ---
    val leidas = leerPeliculas(archivoPath)
    println("Peliculas leídas del fichero:")
    for (dato in leidas) {
        println(" - ID: ${dato.id_planta}, Nombre común:${dato.nombre_comun}, Altura: ${dato.altura_maxima} metros")
    }
    modificarAlturaPlanta(archivoPath, 2, 5.5)
// --- Volver a leer para verificar el cambio ---
    val leidasDespuesDeModificar = leerPlantas(archivoPath)
    println("Plantas leídas después de la modificación:")
    for (dato in leidasDespuesDeModificar) {
        println(" - ID: ${dato.id_planta}, Nombre común: ${dato.nombre_comun}, Altura: ${dato.altura_maxima} metros")
    }
    // --- Eliminar una planta
    eliminarPlanta(archivoPath, 3)
// --- Volver a leer para verificar el cambio ---
    val leidasDespuesDeEliminar = leerPlantas(archivoPath)
    println("Plantas leídas después de la modificación:")
    for (dato in leidasDespuesDeEliminar) {
        println(" - ID: ${dato.id_planta}, Nombre común: ${dato.nombre_comun}, Altura: ${dato.altura_maxima} metros")
    }
}