import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import java.io.*


fun TextoPlano() {
//Escritura en fichero de texto
//writeString
    val texto = "Hola, mundo desde Kotlin"
    Files.writeString(Paths.get("datos_fin/txt/saludo.txt"), texto)
//write
    val ruta = Paths.get("datos_fin/txt/texto.txt")
    val lineasParaGuardar = listOf(
        "Primera línea",
        "Segunda línea",
        "¡Hola desde Kotlin!"
    )
    Files.write(ruta, lineasParaGuardar, StandardCharsets.UTF_8)
    println("Fichero de texto escrito.")
//newBuffered
    Files.newBufferedWriter(Paths.get("datos_fin/txt/log.txt")).use { writer
        ->
        writer.write("Log iniciado...\n")
        writer.write("Proceso completado.\n")
//Lectura del fichero de texto
//readAllLines
        val lineasLeidas = Files.readAllLines(ruta)
        println("Contenido leído con readAllLines:")
        for (lineas in lineasLeidas) {
            println(lineas)
            11
        }
//readString
        val contenido = Files.readString(ruta)
        println("Contenido leído con readString:")
        println(contenido)
//newBufferedReader
        Files.newBufferedReader(ruta).use { reader ->
            println("Contenido leído con newBufferedReader:")
            reader.lineSequence().forEach { println(it) }
        }
    }
}


// Clase Persona (serializable completamente)
class Persona(val nombre: String, val edad: Int) : Serializable
// Clase Usuario con un atributo que NO se serializa
class Usuario(
    val nombre: String,
    @Transient val clave: String // Este campo no se guardará
) : Serializable

fun Serializar() {
    val rutaPersona = "documentos/persona.obj"
    val rutaUsuario = "documentos/usuario.obj"
// Asegurar que el directorio exista
    val directorio = File("documentos")
    if (!directorio.exists()) {
        directorio.mkdirs()
    }
// --- Serializar Persona ---
    val persona = Persona("Pau", 20)
    try {
        ObjectOutputStream(FileOutputStream(rutaPersona)).use { oos ->
            oos.writeObject(persona)
        }
        println("Persona serializada.")
    } catch (e: IOException) {
        println("Error al serializar Persona: ${e.message}")
    }
// --- Deserializar Persona ---
    try {
        val personaLeida = ObjectInputStream(FileInputStream(rutaPersona)).use { ois ->
            ois.readObject() as Persona
        }
        println("Persona deserializada:")
        println("Nombre: ${personaLeida.nombre}, Edad: ${personaLeida.edad}")
    } catch (e: Exception) {
        println("Error al deserializar Persona: ${e.message}")
    }
// --- Serializar Usuario ---
    val usuario = Usuario("Eli", "1234")
    try {
        ObjectOutputStream(FileOutputStream(rutaUsuario)).use { oos ->
            oos.writeObject(usuario)
        }
        println("Usuario serializado.")
    } catch (e: IOException) {
        println("Error al serializar Usuario: ${e.message}")
    }
// --- Deserializar Usuario ---
    try {
        val usuarioLeido = ObjectInputStream(FileInputStream(rutaUsuario)).use {
                ois -> ois.readObject() as Usuario

        }
        println("Usuario deserializado:")
        println("Nombre: ${usuarioLeido.nombre}, Clave: ${usuarioLeido.clave}")
    } catch (e: Exception) {
        println("Error al deserializar Usuario: ${e.message}")
    }
}
