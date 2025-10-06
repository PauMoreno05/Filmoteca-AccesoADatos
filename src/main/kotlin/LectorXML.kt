package org.example

import java.nio.file.Path
import java.io.File
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class PeliculaXML(
    @JacksonXmlProperty(localName = "idPelicula")
    val idPelicula: Int,
    @JacksonXmlProperty(localName = "tituloPeli")
    val tituloPeli: String,
    @JacksonXmlProperty(localName = "director")
    val director: String,
    @JacksonXmlProperty(localName = "genero")
    val genero: String,
    @JacksonXmlProperty(localName = "duracionHoras")
    val duracionHoras: Double
)

@JacksonXmlRootElement(localName = "peliculas")

data class PeliculasXML(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "pelicula")
    val listaPeliculasXML: List<PeliculaXML> = emptyList()
)

fun LectorXML() {
    val entradaXML = Path.of("/home/paumorcat2/IdeaProjects/Filmoteca-AccesoADatos/datos_ini/filmoteca.xml")
    val salidaXML = Path.of("/home/paumorcat2/IdeaProjects/Filmoteca-AccesoADatos/datos_fin/filmoteca2.xml")

    val datos: List<PeliculaXML>
    datos = leerDatosInicialesXML(entradaXML)
    for (dato in datos) {
        println(" - ID: ${dato.idPelicula}, Titulo Pelicula: ${dato.tituloPeli}, Director: ${dato.director}, Genero: ${dato.genero}, Duracion: ${dato.duracionHoras} Horas")
    }
    escribirDatosXML(salidaXML, datos)
}

fun leerDatosInicialesXML(ruta: Path): List<PeliculaXML> {
    val fichero: File = ruta.toFile()
    val xmlMapper = XmlMapper().registerKotlinModule()
    val plantasWrapper: PeliculasXML= xmlMapper.readValue(fichero)
    return plantasWrapper.listaPeliculasXML
}

fun escribirDatosXML(ruta: Path,peliculas: List<PeliculaXML>) {
    try {
        val fichero: File = ruta.toFile()
        val contenedorXml = PeliculasXML(peliculas)
        val xmlMapper = XmlMapper().registerKotlinModule()
        val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedorXml)
        fichero.writeText(xmlString)
        println("\nInformación guardada en: $fichero")
    }catch (e: Exception) {
        println("Error: ${e.message}")
    }
}