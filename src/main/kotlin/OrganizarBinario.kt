import org.example.leerDatosInicialesCSV
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
fun anadirPelicula(
    path: Path,
    idPeliculaJSON: Int,
    tituloPeliJSON: String,
    directorJSON: String,
    generoJSON: String,
    duracionHorasJSON: Double
) {
    // Creamos un nuevo objeto PeliculaBinaria con los datos proporcionados
    val nuevaPeli = PeliculaBinaria(idPeliculaJSON, tituloPeliJSON, directorJSON, generoJSON, duracionHorasJSON)

    // Abrimos el canal en modo APPEND.
    // CREATE: crea el fichero si no existe.
    // WRITE: permite escribir en el fichero.
    // APPEND: asegura que cada nueva película se escriba al final del fichero.
    try {
        FileChannel.open(path, StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { canal ->

            // Creamos un buffer del tamaño exacto del registro
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            // Escribimos los datos de la película en el buffer, en el mismo orden que al leerlos.
            // Primero el ID (entero, 4 bytes)
            buffer.putInt(nuevaPeli.idPeliculaJSON)

            // Luego el título (cadena de longitud fija: 40 caracteres)
            buffer.put(
                nuevaPeli.tituloPeliJSON
                    .padEnd(TAMANO_TITULO, ' ') // Rellenamos con espacios hasta completar el tamaño fijo
                    .toByteArray(Charset.defaultCharset()), // Convertimos la cadena a bytes
                0,
                TAMANO_TITULO
            )

            // Director (cadena de longitud fija: 25 caracteres)
            buffer.put(
                nuevaPeli.directorJSON
                    .padEnd(TAMANO_DIRECTOR, ' ')
                    .toByteArray(Charset.defaultCharset()),
                0,
                TAMANO_DIRECTOR
            )

            // Género (cadena de longitud fija: 10 caracteres)
            buffer.put(
                nuevaPeli.generoJSON
                    .padEnd(TAMANO_GENERO, ' ')
                    .toByteArray(Charset.defaultCharset()),
                0,
                TAMANO_GENERO
            )

            // Duración (número decimal en formato Double, 8 bytes)
            buffer.putDouble(nuevaPeli.duracionHorasJSON)

            // Pasamos el buffer de modo escritura a modo lectura
            // para que los datos se puedan escribir en el fichero.
            buffer.flip()

            // Escribimos el contenido del buffer en el canal
            while (buffer.hasRemaining()) {
                canal.write(buffer)
            }

            println("Pelicula '${nuevaPeli.tituloPeliJSON}' añadida con éxito.")
        }
    } catch (e: Exception) {
        println("Error al añadir la Pelicula: ${e.message}")
        e.printStackTrace()
    }
}


fun importar() {
    // Ruta del fichero CSV de entrada
    val rutaCSV = Paths.get("datos_ini/filmoteca.csv")

    // Ruta del fichero binario de salida
    val rutaBinario = Paths.get("datos_fin/binario/datos.bin")

    // Aseguramos que la carpeta destino existe (la crea si no)
    Files.createDirectories(rutaBinario.parent)

    // Leemos los datos del fichero CSV (lista de objetos Filmoteca)
    val peliculas = leerDatosInicialesCSV(rutaCSV)

    // Si no se pudo leer ninguna película, salimos
    if (peliculas.isEmpty()) {
        println("No se encontraron películas para importar.")
        return
    }

    // Vaciamos/creamos el fichero binario antes de empezar
    vaciarCrearFichero(rutaBinario)

    // Recorremos todas las películas leídas y las añadimos una por una
    for (peli in peliculas) {
        anadirPelicula(
            rutaBinario,
            peli.idPelicula,
            peli.tituloPeli,
            peli.director,
            peli.genero,
            peli.duracionHoras
        )
    }

    println("Importación completada. Se han añadido ${peliculas.size}.")
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
            val tituloBytes = ByteArray(TAMANO_TITULO)
            buffer.get(tituloBytes)
            val titulo = String(tituloBytes,
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
                val posicionDuracion = posicionActual - TAMANO_REGISTRO + TAMANO_ID + TAMANO_TITULO + TAMANO_DIRECTOR + TAMANO_GENERO
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

fun eliminarPelicula(path: Path, idPeliculaJSON: Int) {
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
// Si es la pelicla a eliminar, no hacemos nada.
// El buffer se limpiará para la siguiente lectura.
                } else {
// Si NO es la pelicula a eliminar, escribimos el registro
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

fun mostrarTodo(path: Path) {
    try {
        val peliculas = leerPeliculas(path)
        if (peliculas.isEmpty()) {
            println("El fichero binario está vacío.")
            return
        }

        println("=== LISTADO DE PELÍCULAS ===")

        peliculas.forEach { peli ->
            // Linea para mostrar el formato
            println("ID: ${peli.idPeliculaJSON}, Título: \"${peli.tituloPeliJSON.trim()}\", Director: \"${peli.directorJSON.trim()}\", Género: \"${peli.generoJSON.trim()}\", Duración: ${String.format("%.2f", peli.duracionHorasJSON)} horas")
        }

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun nuevoReg(path: Path) {
    println("=== 2. AÑADIR NUEVO REGISTRO ===")
    try {
        print("ID de la película: ")
        val id = readLine()?.toIntOrNull() ?: throw IllegalArgumentException("ID inválido.")
        print("Titulo: ")
        val titulo = readLine() ?: ""
        print("Director: ")
        val director = readLine() ?: ""
        print("Género: ")
        val genero = readLine() ?: ""
        print("Duración: ")
        val duracion = readLine()?.toDoubleOrNull() ?: throw IllegalArgumentException("Duración inválida o no numérica.")

        anadirPelicula(path, id, titulo, director, genero, duracion)
    } catch (e: Exception) {
        println(" Error: ${e.message}")
    }
}

fun modificar(path: Path, id: Int) {
    try {
        print("Introduzca la NUEVA DURACIÓN para el ID $id: ")
        val nuevaDuracion = readLine()?.toDoubleOrNull()

        if (nuevaDuracion != null && nuevaDuracion > 0) {
            // Llamar a la funcion de modificación real
            modificarAlturaPelicula(path, id, nuevaDuracion)
        } else {
            println("Duración introducida no valida.")
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun eliminar(path: Path, id: Int) {
    // Llamar a la funcion de eliminación real
    eliminarPelicula(path, id)
}



fun OrganizarBinario() {
    val archivoPath: Path = Paths.get("datos_fin/binario/datos.bin")

    // Llama a importar() para vaciar/crear el fichero y llenarlo desde el CSV
    importar()


    // Comprueba: Leer el fichero binario y mostrar
    val leidas = leerPeliculas(archivoPath)
    println("\nPeliculas leídas del fichero después de la importación (Comprobación 6):")
    for (dato in leidas) {
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula:${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
    }

    // Modificar un registro
    modificarAlturaPelicula(archivoPath, 2, 2.96)

    // Volver a leer para verificar el cambio
    val leidasDespuesDeModificar = leerPeliculas(archivoPath)
    println("\nPeliculas leídas después de la modificación (Comprobación 9 - Modificar):")
    for (dato in leidasDespuesDeModificar) {
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula:${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
    }

    // Eliminar un registro
    eliminarPelicula(archivoPath, 3)

    // Volver a leer para verificar el cambio
    val leidasDespuesDeEliminar = leerPeliculas(archivoPath)
    println("\nPeliculas leídas después de la eliminación :")
    for (dato in leidasDespuesDeEliminar) {
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula:${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
    }
}