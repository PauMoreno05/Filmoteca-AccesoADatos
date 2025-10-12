# Gestor de Filmoteca Binaria

Este es un programa de consola desarrollado en Kotlin para gestionar una colección de películas (una filmoteca). Los datos se almacenan en un fichero binario de acceso aleatorio llamado `datos.bin`, lo que permite una lectura y modificación eficiente de registros individuales.

La aplicación permite la importación inicial de datos desde un fichero CSV, así como operaciones CRUD (Crear, Leer, Modificar, Eliminar) a través de un menú interactivo creado en el documento ***Main*** del proyecto.

## 1. Estructura de Datos

El diseño de la estructura binaria se basa en un registro de tamaño fijo para permitir el acceso directo y la modificación de campos específicos sin reescribir todo el fichero.

### **1.1. Data Class (Estructura Lógica):**

```kotlin
data class PeliculaBinaria(
    val idPeliculaJSON: Int, 
    val tituloPeliJSON: String, 
    val directorJSON: String, 
    val generoJSON: String, 
    val duracionHorasJSON: Double
)
```
### **1.2. Estructura del registro del Binario (Fijo)**
Para permitir el acceso directo y secuencial, cada registro binario tiene un tamaño fijo. Los campos de tipo String se rellenan con espacios (padEnd) para alcanzar la longitud definida.

- idPeliculaJSON: 	Int	- 4	**Int.SIZE_BYTES**
- tituloPeliJSON:	String - 40	**const val TAMANO_TITULO**
- directorJSON:	String	- 25	**const val TAMANO_DIRECTOR**
- generoJSON:	String	- 10	**const val TAMANO_GENERO**
- duracionHorasJSON:  Double - 8	**Double.SIZE_BYTES**

### **1.3. Tamaño total del registro**

El tamaño de un registro individual es la suma de los tamaños fijos de todos sus campos:

- Tamaño Total del Registro = 4 + 40 + 25 + 10 + 8 = 87 bytes

## 2. Instrucciones de Ejecucion
1.  Ficheros necesarios: El programa requiere que exista un fichero de datos iniciales llamado ***filmoteca.csv*** dentro de una carpeta llamada ***datos_ini/***.

2. Compilación y Ejecución:
- Abre el proyecto en tu IDE ***ej. IntelliJ IDEA*** y espera a que Gradle sincronice las dependencias.

- Ejecuta la función ***main*** ubicada en el fichero ***Main.kt***.

- Al iniciar, la función ***importar()*** se ejecuta para crear el fichero binario ***datos_fin/binario/datos.bin*** y poblarlo con los datos del CSV.

- Se mostrará un menú interactivo que permitirá ejecutar las operaciones (Mostrar, Añadir, Modificar, Eliminar).

## 3. Decisiones de Diseño

- **Fichero Binario de Acceso Aleatorio:** Se eligió esta estructura para el almacenamiento final porque permite la función modificar (cambiar solo la duración) usando ***FileChannel.position()*** y ***write()*** sin afectar al resto del fichero, y la función eliminar usando un fichero temporal de manera eficiente.
- **Validación de Entrada:** Para el menú, se utiliza la función ***toIntOrNull()*** y bloques ***try-catch*** para validar las entradas del usuario, asegurando que el programa no se detenga si se introduce texto en lugar de un número.
- **Separacion de archivos .kt:** Toda la parte de gestión de binarios se ha programándo en el documento ***OrganizarBinario.kt*** para una mejor organizacion del proyecto, separando de la parte del menu interactivo, que está al completo en el archivo ***Main.kt***.