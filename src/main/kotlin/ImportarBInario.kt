// NOTA: Se asume que las data classes Filmoteca, PeliculaXML y PeliculaJSON
// y sus funciones de lectura correspondientes están importadas y disponibles.

import org.example.Filmoteca // De LectorCSV.kt
import org.example.leerDatosInicialesCSV
import org.example.PeliculaXML // De LectorXML.kt
import org.example.leerDatosInicialesXML
// import PeliculaJSON // De LectorJSON.kt (Si no tiene package)
// import leerDatosInicialesJSON // De LectorJSON.kt (Si no tiene package)
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Función original renombrada para mayor claridad
fun importarCSVaBinario() {
    // Rutas
    val rutaCSV = Paths.get("datos_ini/filmoteca.csv")
    val rutaBinario = Paths.get("datos_fin/binario/datos.bin")

    // Aseguramos que la carpeta destino existe
    Files.createDirectories(rutaBinario.parent)

    // Leemos los datos desde CSV
    val peliculas = leerDatosInicialesCSV(rutaCSV)

    if (peliculas.isEmpty()) {
        println("No se encontraron películas en el CSV para importar.")
        return
    }

    // Vaciamos/creamos el fichero binario
    vaciarCrearFichero(rutaBinario)

    // Recorremos y añadimos cada registro al binario
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

    println("Importación completada: CSV -> Binario. Se han añadido ${peliculas.size} películas.")
}

// -----------------------------------------------------------------------

fun importarXMLaBinario() {
    // Rutas
    val rutaXML = Paths.get("datos_ini/filmoteca.xml")
    val rutaBinario = Paths.get("datos_fin/binario/datos.bin")

    // Aseguramos que la carpeta destino existe
    Files.createDirectories(rutaBinario.parent)

    // Leemos los datos desde XML
    val peliculas = leerDatosInicialesXML(rutaXML) // Usa la función de LectorXML.kt

    if (peliculas.isEmpty()) {
        println("No se encontraron películas en el XML para importar.")
        return
    }

    // Vaciamos/creamos el fichero binario
    vaciarCrearFichero(rutaBinario)

    // Recorremos y añadimos cada registro al binario
    for (peli in peliculas) {
        anadirPelicula(
            rutaBinario,
            peli.idPelicula, // El nombre del campo en PeliculaXML es 'idPelicula'
            peli.tituloPeli,
            peli.director,
            peli.genero,
            peli.duracionHoras
        )
    }

    println("Importación completada: XML -> Binario. Se han añadido ${peliculas.size} películas.")
}

// -----------------------------------------------------------------------

fun importarJSONaBinario() {
    // Rutas
    val rutaJSON = Paths.get("datos_ini/filmoteca.json")
    val rutaBinario = Paths.get("datos_fin/binario/datos.bin")

    // Aseguramos que la carpeta destino existe
    Files.createDirectories(rutaBinario.parent)

    // Leemos los datos desde JSON
    // Se asume que 'leerDatosInicialesJSON' y 'PeliculaJSON' son accesibles.
    val peliculas = leerDatosInicialesJSON(rutaJSON)

    if (peliculas.isEmpty()) {
        println("No se encontraron películas en el JSON para importar.")
        return
    }

    // Vaciamos/creamos el fichero binario
    vaciarCrearFichero(rutaBinario)

    // Recorremos y añadimos cada registro al binario
    for (peli in peliculas) {
        anadirPelicula(
            rutaBinario,
            peli.idPeliculaJSON, // El nombre del campo en PeliculaJSON es 'idPeliculaJSON'
            peli.tituloPeliJSON,
            peli.directorJSON,
            peli.generoJSON,
            peli.duracionHorasJSON
        )
    }

    println("Importación completada: JSON -> Binario. Se han añadido ${peliculas.size} películas.")
}