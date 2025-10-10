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
const val TAMANO_DIRECTOR = 25 // 8 bytes
const val TAMANO_GENERO = 10
const val TAMANO_DURACION = Double.SIZE_BYTES
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_TITULO + TAMANO_DIRECTOR + TAMANO_GENERO + TAMANO_DURACION

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
    try {
        FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            buffer.putInt(nuevaPeli.idPeliculaJSON)
            buffer.put(nuevaPeli.tituloPeliJSON.padEnd(TAMANO_TITULO, ' ').toByteArray(Charset.defaultCharset()), 0, TAMANO_TITULO)
            buffer.put(nuevaPeli.directorJSON.padEnd(TAMANO_DIRECTOR, ' ').toByteArray(Charset.defaultCharset()), 0, TAMANO_DIRECTOR)
            buffer.put(nuevaPeli.generoJSON.padEnd(TAMANO_GENERO, ' ').toByteArray(Charset.defaultCharset()), 0, TAMANO_GENERO)
            buffer.putDouble(nuevaPeli.duracionHorasJSON)

            buffer.flip()

            while (buffer.hasRemaining()) {
                canal.write(buffer)
            }
            println("Pelicula '${nuevaPeli.tituloPeliJSON}' añadida con éxito.")
        }
    } catch (e: Exception) {
        e.printStackTrace() // 👈 así verás el error real si vuelve a fallar
        println("Error al añadir la Pelicula: ${e.message}")
    }
}

fun leerPeliculas(path: Path): List<PeliculaBinaria> {
    val peliculas = mutableListOf<PeliculaBinaria>()
// Abrimos un canal de solo lectura al fichero
    FileChannel.open(path, StandardOpenOption.READ).use { canal ->
// Buffer para leer un registro a la vez
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        while (canal.read(buffer) > 0) {
            buffer.flip()
            val id = buffer.getInt()
            val nombreBytes = ByteArray(TAMANO_TITULO)
            buffer.get(nombreBytes)
            val titulo = String(nombreBytes,
                Charset.defaultCharset()).trim()
            val directorBytes = ByteArray(TAMANO_DIRECTOR)
            buffer.get(directorBytes)
            val director = String(directorBytes,
                Charset.defaultCharset()).trim()
            val generoBytes = ByteArray(TAMANO_GENERO)
            buffer.get(generoBytes)
            val genero = String(generoBytes,
                Charset.defaultCharset()).trim()
            val duracion = buffer.getDouble()
            peliculas.add(PeliculaBinaria(id, titulo, director,genero, duracion ))
            buffer.clear()
        }
    }
    return peliculas
}

fun modificarAlturaPelicula(path: Path, idPeliculaJSON: Int, nuevaDuracion: Double)
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
            if (id == idPeliculaJSON) {
// Hemos encontrado el registro. Calculamos la posición del campo 'altura'.
// Posición de inicio del registro (actual - registro) + 4 bytes (id) + 20 bytes (nombre)
                val posicionDuracion = posicionActual - TAMANO_REGISTRO +
                        TAMANO_ID + TAMANO_TITULO + TAMANO_DIRECTOR + TAMANO_GENERO
// Creamos un buffer solo para el double
                val bufferDuracion = ByteBuffer.allocate(TAMANO_DURACION)
                bufferDuracion.putDouble(nuevaDuracion)
                bufferDuracion.flip()
// Escribimos el nuevo valor en la posición exacta del fichero
                canal.write(bufferDuracion, posicionDuracion)
                encontrado = true
            }
            buffer.clear()
        }
        if (encontrado) {
            println("Duracion de la pelicula con ID $idPeliculaJSON modificada a$nuevaDuracion")
        } else {
            println("No se encontró la Pelicula con ID $idPeliculaJSON")
        }
    }
}

fun eliminarPlanta(path: Path, idPeliculaJSON: Int) {
// Creamos un fichero temporal en el mismo directorio
    val pathTemporal = Paths.get(path.toString() + ".tmp")
    var peliculaEncontrada = false
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
                if (id == idPeliculaJSON) {
                    peliculaEncontrada = true
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
    if (peliculaEncontrada) {
// Si se encontró y eliminó, reemplazar fichero original con el temporal
        Files.move(pathTemporal, path, StandardCopyOption.REPLACE_EXISTING)
        println("Pelicula con ID $idPeliculaJSON eliminada con éxito.")
    } else {
// Si no se encontró, no hace falta hacer nada, borramos el temporal
        Files.delete(pathTemporal)
        println("No se encontró ninguna pelicula con ID $idPeliculaJSON.")
    }
}


fun OrganizarBinario() {
    val archivoPath: Path = Paths.get("datos_fin/binario/datos.bin")
    val lista = listOf(
        PeliculaBinaria(1, "El Origen", "Christopher Nolan","Ciencia Ficción", 2.48 ),
        PeliculaBinaria(2, "El Padrino", "Francis Ford Coppola","Crimen", 2.92),
        PeliculaBinaria(3, "Tiempos Violentos", "Quentin Tarantino", "Crimen",2.54),
        PeliculaBinaria(4, "Sueños de Fuga", "Frank Darabont", "Drama", 2.37),
        PeliculaBinaria(5, "Forrest Gump", "Robert Zemeckis", "Drama", 2.33)
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
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula:${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
    }
    modificarAlturaPelicula(archivoPath, 2, 2.96)
// --- Volver a leer para verificar el cambio ---
    val leidasDespuesDeModificar = leerPeliculas(archivoPath)
    println("Peliculas leídas después de la modificación:")
    for (dato in leidasDespuesDeModificar) {
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula:${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
    }
    // --- Eliminar una planta
    eliminarPlanta(archivoPath, 3)
// --- Volver a leer para verificar el cambio ---
    val leidasDespuesDeEliminar = leerPeliculas(archivoPath)
    println("Plantas leídas después de la modificación:")
    for (dato in leidasDespuesDeEliminar) {
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula:${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
    }
}
