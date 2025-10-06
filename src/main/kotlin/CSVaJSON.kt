import org.example.leerDatosInicialesCSV
import java.nio.file.Path

data class PeliculaCON(val idPelicula: Int, val tituloPeli: String, val director: String, val genero: String, val duracionHoras: Double)

fun CSVaJSON() {
    val entradaCSV = Path.of("datos_ini/filmoteca.csv")
    val salidaJSON = Path.of("datos_fin/filmoteca_convertido.json")

    try {
        val peliculasCSV = leerDatosInicialesCSV(entradaCSV)

        val peliculasJSON = peliculasCSV.map {
            PeliculaJSON(it.idPelicula, it.tituloPeli, it.director, it.genero, it.duracionHoras)
        }

        escribirDatosJSON(salidaJSON, peliculasJSON)

        for (dato in peliculasJSON) {
            println(" - ID: ${dato.idPeliculaJSON}, Titulo: ${dato.tituloPeliJSON}, Director: ${dato.directorJSON}, Genero: ${dato.generoJSON}, Duracion: ${dato.duracionHorasJSON} horas")
        }
    } catch (e: Exception) {
        println("Error general en la conversión: ${e.message}")
    }
}
