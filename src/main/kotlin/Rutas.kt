import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension // Extensión de Kotlin para obtener la extensión

fun PosiblesRutas() {
// Path relativo al directorio del proyecto
    val rutaRelativa: Path = Path.of("documentos", "ejemplo.txt")
// Path absoluto en Windows
    val rutaAbsolutaWin: Path = Path.of("C:", "Users", "Pau", "Documentos")
// Path absoluto en Linux/macOS
    val rutaAbsolutaNix: Path = Path.of("/home/pau/documentos")
    println("Ruta relativa: " + rutaRelativa) // Muestra la ruta relativa
    println("Ruta absoluta: " + rutaRelativa.toAbsolutePath()) // ruta completa
    println("Ruta absoluta: " + rutaAbsolutaWin) // ruta absoluta Windows
    println("Ruta absoluta: " + rutaAbsolutaNix) // ruta absoluta Linux/macOS
}


fun OrganizarRutas() {
// 1. Ruta de la carpeta a organizar
    val carpeta = Path.of("multimedia")
    5
    println("--- Iniciando la organización de la carpeta: " + carpeta + "---")
    try {
// 2. Recorrer la carpeta desordenada y utilizar .use para asegurar que los recursos del sistema se cierren correctamente
                Files.list(carpeta).use { streamDePaths ->
                    streamDePaths.forEach { pathFichero ->
// 3. Solo interesan los ficheros, ignorar subcarpetas
                        if (Files.isRegularFile(pathFichero)) {
// 4. Obteners la extensión del fichero (ej: "pdf", "jpg")
                            val extension = pathFichero.extension.lowercase()
                            if (extension.isBlank()) {
                                println("-> Ignorando: " + pathFichero.fileName)
                                return@forEach // Salta a la siguiente iteración del bucle
                            }
// 5. Crear la ruta del directorio de destino
                            val carpetaDestino = carpeta.resolve(extension)
// 6. Crear el directorio de destino si no existe
                            if (Files.notExists(carpetaDestino)) {
                                println("-> Creando nueva carpeta " + extension)
                                Files.createDirectories(carpetaDestino)
                            }
// 7. Mover el fichero a su nueva carpeta
                            val pathDestino = carpetaDestino.resolve(pathFichero.fileName)
                            Files.move(pathFichero, pathDestino, StandardCopyOption.REPLACE_EXISTING)
                            println("-> Moviendo " + pathFichero.fileName + " a " + extension)
                        }
                    }
                }
        println("\n--- ¡Organización completada con éxito! ---")
    } catch (e: Exception) {
        println("\n--- Ocurrió un error durante la organización ---")
        e.printStackTrace()
    }
}

fun Informe() {
    val carpetaPrincipal = Path.of("multimedia")
    println("--- Mostrando la estructura final con Files.walk() ---")
    try {
        Files.walk(carpetaPrincipal).use { stream ->
            8
// Ordenar el stream para una visualización más predecible
            stream.sorted().forEach { path ->
// Calcular profundidad para la indentación
// Restamos el número de componentes de la ruta base para que el directorio principal no tenga indentación
                val profundidad = path.nameCount - carpetaPrincipal.nameCount
                val indentacion = "\t".repeat(profundidad)
// Determinamos si es directorio o fichero para el prefijo
                val prefijo = if (Files.isDirectory(path)) "[DIR]" else "[FILE]"
// No imprimimos la propia carpeta raíz, solo su contenido
                if (profundidad > 0) {
                    println("$indentacion$prefijo ${path.fileName}")
                }
            }
        }
    } catch (e: Exception) {
        println("\n--- Ocurrió un error durante el recorrido ---")
        e.printStackTrace()
    }
}
