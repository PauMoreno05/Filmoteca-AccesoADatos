import org.example.leerDatosInicialesCSV
import java.nio.file.Path
import org.example.Filmoteca // Data class de LectorCSV.kt
import org.example.leerDatosInicialesCSV
import org.example.escribirDatosCSV

import PeliculaJSON // Data class de LectorJSON.kt
import leerDatosInicialesJSON
import escribirDatosJSON

import org.example.PeliculaXML // Data class de LectorXML.kt
import org.example.leerDatosInicialesXML
import org.example.escribirDatosXML
import java.nio.file.Paths


// Definición de las rutas base para entradas y salidas
const val CSV_IN_PATH = "datos_ini/filmoteca.csv"
const val JSON_IN_PATH = "datos_ini/filmoteca.json"
const val XML_IN_PATH = "datos_ini/filmoteca.xml"
const val DATA_FIN_PATH = "datos_fin/"

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

fun CSVaXML() {
    val entradaCSV = Paths.get(CSV_IN_PATH)
    val salidaXML = Paths.get(DATA_FIN_PATH + "conversion_csv_a_xml.xml")

    try {
        val peliculasCSV = leerDatosInicialesCSV(entradaCSV)

        // Mapeo: Filmoteca (CSV) a PeliculaXML (XML). Los campos coinciden.
        val peliculasXML = peliculasCSV.map {
            PeliculaXML(
                it.idPelicula, it.tituloPeli, it.director, it.genero, it.duracionHoras
            )
        }

        escribirDatosXML(salidaXML, peliculasXML)
        println("Conversión completada: CSV -> XML en ${salidaXML.fileName}")

    } catch (e: Exception) {
        println("Error general en la conversión CSV -> XML: ${e.message}")
    }
}

// --- Conversiones desde JSON ---

fun JSONaXML() {
    val entradaJSON = Paths.get(JSON_IN_PATH)
    val salidaXML = Paths.get(DATA_FIN_PATH + "conversion_json_a_xml.xml")

    try {
        val peliculasJSON = leerDatosInicialesJSON(entradaJSON)

        // Mapeo: PeliculaJSON (JSON) a PeliculaXML (XML)
        val peliculasXML = peliculasJSON.map {
            PeliculaXML(
                it.idPeliculaJSON, it.tituloPeliJSON, it.directorJSON, it.generoJSON, it.duracionHorasJSON
            )
        }

        escribirDatosXML(salidaXML, peliculasXML)
        println("Conversión completada: JSON -> XML en ${salidaXML.fileName}")

    } catch (e: Exception) {
        println("Error general en la conversión JSON -> XML: ${e.message}")
    }
}

fun JSONaCSV() {
    val entradaJSON = Paths.get(JSON_IN_PATH)
    val salidaCSV = Paths.get(DATA_FIN_PATH + "conversion_json_a_csv.csv")

    try {
        val peliculasJSON = leerDatosInicialesJSON(entradaJSON)

        // Mapeo: PeliculaJSON (JSON) a Filmoteca (CSV)
        val peliculasCSV = peliculasJSON.map {
            Filmoteca(
                it.idPeliculaJSON, it.tituloPeliJSON, it.directorJSON, it.generoJSON, it.duracionHorasJSON
            )
        }

        escribirDatosCSV(salidaCSV, peliculasCSV)
        println("Conversión completada: JSON -> CSV en ${salidaCSV.fileName}")

    } catch (e: Exception) {
        println("Error general en la conversión JSON -> CSV: ${e.message}")
    }
}

// --- Conversiones desde XML ---

fun XMLaJSON() {
    val entradaXML = Paths.get(XML_IN_PATH)
    val salidaJSON = Paths.get(DATA_FIN_PATH + "conversion_xml_a_json.json")

    try {
        val peliculasXML = leerDatosInicialesXML(entradaXML)

        // Mapeo: PeliculaXML (XML) a PeliculaJSON (JSON)
        val peliculasJSON = peliculasXML.map {
            PeliculaJSON(
                it.idPelicula, it.tituloPeli, it.director, it.genero, it.duracionHoras
            )
        }

        escribirDatosJSON(salidaJSON, peliculasJSON)
        println("Conversión completada: XML -> JSON en ${salidaJSON.fileName}")

    } catch (e: Exception) {
        println("Error general en la conversión XML -> JSON: ${e.message}")
    }
}

fun XMLaCSV() {
    val entradaXML = Paths.get(XML_IN_PATH)
    val salidaCSV = Paths.get(DATA_FIN_PATH + "conversion_xml_a_csv.csv")

    try {
        val peliculasXML = leerDatosInicialesXML(entradaXML)

        // Mapeo: PeliculaXML (XML) a Filmoteca (CSV). Los campos coinciden.
        val peliculasCSV = peliculasXML.map {
            Filmoteca(
                it.idPelicula, it.tituloPeli, it.director, it.genero, it.duracionHoras
            )
        }

        escribirDatosCSV(salidaCSV, peliculasCSV)
        println("Conversión completada: XML -> CSV en ${salidaCSV.fileName}")

    } catch (e: Exception) {
        println("Error general en la conversión XML -> CSV: ${e.message}")
    }
}