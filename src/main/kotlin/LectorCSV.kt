package org.example
import java.nio.file.Files
import java.nio.file.Path
import java.io.File
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter

data class Filmoteca(val idPelicula: Int, val tituloPeli: String, val director: String, val genero: String, val duracionHoras: Double )

//prueba git
fun LectorCSV() {
    val archivoCSV = Path.of("datos_ini/filmoteca.csv")
    val salidaCSV = Path.of("datos_fin/filmoteca2.csv")
    val datos: List<Filmoteca>
    datos = leerDatosInicialesCSV(archivoCSV)
    for (dato in datos) {
        println(" - ID: ${dato.idPelicula}, Titulo Pelicula: ${dato.tituloPeli}, Director: ${dato.director}, Genero: ${dato.genero}, Duracion: ${dato.duracionHoras} Horas")
    }
    escribirDatosCSV(salidaCSV,datos)
}

fun leerDatosInicialesCSV(ruta: Path): List<Filmoteca>{
    var peliculas: List<Filmoteca> = emptyList()

    if (!Files.isReadable(ruta)){
        println("No se puede leer el fiche de la ruta: $ruta")
    }else{
        val reader = csvReader { delimiter = ',' }
        val filas : List<List<String>> = reader.readAll(ruta.toFile())
        peliculas = filas.mapNotNull { columnas ->
            if (columnas.size >= 5){
                try {
                    val idPelicla = columnas[0].toInt()
                    val tituloPeli = columnas[1]
                    val director = columnas[2]
                    val genero = columnas[3]
                    val duracionHoras = columnas[4].toDouble()
                        Filmoteca(idPelicla,tituloPeli,director,genero,duracionHoras)
                }catch (e : Exception){
                    println("Fila Invalida ignorada $columnas -> Error ${e.message}")
                    null
                }
            }else{
                println("Fila con formato incorrecto ignorada: $columnas")
                null
            }
        }
    }
    return peliculas
}

fun escribirDatosCSV(ruta: Path,plantas: List<Filmoteca>){
    try {
        val fichero: File = ruta.toFile()
        csvWriter {
            delimiter = ','
        }.writeAll(
            plantas.map { planta ->
                listOf(planta.idPelicula.toString(),
                    planta.tituloPeli,
                    planta.director,
                    planta.genero,
                    planta.duracionHoras.toDouble())
            },
            fichero
        )
        println("\nInformación guardada en: $fichero")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
