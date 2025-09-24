import java.nio.file.Files
import java.nio.file.Path
import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class PeliculaJSON(val idPeliculaJSON: Int, val tituloPeliJSON: String, val directorJSON: String, val generoJSON: String, val duracionHorasJSON: Double)

fun main() {
    val entradaJSON = Path.of("datos_ini/filmoteca.json")
    val salidaJSON = Path.of("datos_fin/filmoteca2.json")
    val datos: List<PeliculaJSON>
    datos = leerDatosInicialesJSON(entradaJSON)
    for (dato in datos) {
        println(" - ID: ${dato.idPeliculaJSON}, Titulo Pelicula: ${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero:${dato.generoJSON} Duracion: ${dato.duracionHorasJSON} Horas")
    }
    escribirDatosJSON(salidaJSON, datos)
}
fun leerDatosInicialesJSON(ruta: Path): List<PeliculaJSON> {
    var peliculasJSON: List<PeliculaJSON> = emptyList()
    val jsonString = Files.readString(ruta)
    peliculasJSON = Json.decodeFromString<List<PeliculaJSON>>(jsonString)
    return peliculasJSON
}
fun escribirDatosJSON(ruta: Path,peliculasJSON: List<PeliculaJSON>) {
    try {

        val json = Json { prettyPrint = true }.encodeToString(peliculasJSON)

        Files.writeString(ruta, json)
        println("\nInformación guardada en: $ruta")

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}