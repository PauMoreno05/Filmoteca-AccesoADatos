
import java.nio.file.Paths


fun main(){
    importar()
    val ARCHIVO_BINARIO = Paths.get("datos_fin/binario/datos.bin")
    var opcion: Int

    do {
        println("==================================")
        println("       MENÚ PRINCIPAL  ")
        println("==================================")
        println("")
        println("1. Mostrar todos los registros")
        println("2. Añadir un nuevo registro")
        println("3. Modificar un registro (por ID)")
        println("4. Eliminar un registro (por ID)")
        println("5. Salir")
        println("==================================")
        print("Seleccione una opción: ")

        val entrada = readLine()
        opcion = entrada?.toIntOrNull() ?: 0 // Asigna 0 si la entrada no es un número

        if (opcion !in 1..5) {
            println("Opción no válida, introduzca un numero del 1 al 5.")
            continue // Vuelve a mostrar el menú
        }

        when (opcion) {
            1 -> {
                mostrarTodo(ARCHIVO_BINARIO)
            }
            2 -> {
                nuevoReg(ARCHIVO_BINARIO)
            }
            3 -> {
                println("=== MODIFICAR REGISTRO ===")
                try {
                    print("Introduzca el ID del registro a modificar: ")
                    val idModificar = readLine()?.toIntOrNull() ?: throw IllegalArgumentException("ID inválido.")
                    modificar(ARCHIVO_BINARIO, idModificar)
                } catch (e: Exception) {
                    println("Error: ${e.message}.")
                }
            }
            4 -> {
                println("=== ELIMINAR REGISTRO ===")
                try {
                    print("Introduzca el ID del registro a eliminar: ")
                    val idEliminar = readLine()?.toIntOrNull() ?: throw IllegalArgumentException("ID inválido.")
                    eliminar(ARCHIVO_BINARIO, idEliminar)
                } catch (e: Exception) {
                    println("Error : ${e.message}.")
                }
            }
            5 -> {
                println("Saliendo del programa.")
            }
        }
    } while (opcion != 5)
}