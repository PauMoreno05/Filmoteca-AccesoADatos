
import java.nio.file.Paths


fun main(){
    // Inicializar el fichero binario importando datos del CSV (buena práctica antes del menú)
    importar()

    val ARCHIVO_BINARIO = Paths.get("datos_fin/binario/datos.bin")

    var opcion: Int // Variable para almacenar la opción del menú

    // El menú se mantendrá en ejecución hasta que el usuario decida salir (opcion == 5)
    do {
        println("\n==================================")
        println("       MENÚ PRINCIPAL 🎬")
        println("==================================")
        println("1. Mostrar todos los registros")
        println("2. Añadir un nuevo registro")
        println("3. Modificar un registro (por ID)")
        println("4. Eliminar un registro (por ID)")
        println("5. Salir")
        println("----------------------------------")
        print("Seleccione una opción: ")

        // 🟢 Validar la entrada del usuario: toIntOrNull()
        val entrada = readLine()
        opcion = entrada?.toIntOrNull() ?: 0 // Asigna 0 si la entrada no es un número

        // Validar que la opción esté en el rango 1-5
        if (opcion !in 1..5) {
            println("Opción no válida. Por favor, introduzca un número del 1 al 5.")
            continue // Vuelve a mostrar el menú
        }

        // ➡️ Manejar las opciones del menú
        when (opcion) {
            1 -> {
                // Mostrar todos: Llama a mostrarTodo()
                mostrarTodo(ARCHIVO_BINARIO)
            }
            2 -> {
                // Añadir registro: Llama a nuevoReg(). La función ya pide los datos.
                nuevoReg(ARCHIVO_BINARIO)
            }
            3 -> {
                // Modificar registro: Pide ID y llama a modificar()
                println("\n--- 3. MODIFICAR REGISTRO ---")
                try {
                    print("Introduzca el ID del registro a modificar: ")
                    val idModificar = readLine()?.toIntOrNull() ?: throw IllegalArgumentException("ID inválido.")
                    modificar(ARCHIVO_BINARIO, idModificar) // Llama a la función que pide la nueva duración
                } catch (e: Exception) {
                    println("Error en la entrada del ID: ${e.message}. Operación cancelada.")
                }
            }
            4 -> {
                // Eliminar registro: Pide ID y llama a eliminar()
                println("\n--- 4. ELIMINAR REGISTRO ---")
                try {
                    print("Introduzca el ID del registro a eliminar: ")
                    val idEliminar = readLine()?.toIntOrNull() ?: throw IllegalArgumentException("ID inválido.")
                    eliminar(ARCHIVO_BINARIO, idEliminar)
                } catch (e: Exception) {
                    println("Error en la entrada del ID: ${e.message}. Operación cancelada.")
                }
            }
            5 -> {
                // Salir
                println("\n👋 ¡Hasta pronto! Saliendo del programa.")
            }
        }
    } while (opcion != 5) // El bucle continúa mientras la opción no sea 5
}