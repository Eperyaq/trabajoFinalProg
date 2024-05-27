package gestorFicheros

import output.IOutputInfo
import java.io.File
import java.io.FileNotFoundException

/**
 * Implementación concreta de la interfaz IFicheros para manejar ficheros de texto.
 * Provee funcionalidades para operar con ficheros y directorios, específicamente para el formato de texto.
 *
 * @property consola Instancia de IOutputInfo para mostrar mensajes de error o confirmación durante las operaciones de ficheros.
 */
class Ficheros(private val consola: IOutputInfo) : IFicheros {

    /**
     * Verifica si un archivo existe en la ruta especificada.
     *
     * @param ruta La ruta del archivo a verificar.
     * @return Verdadero si el archivo existe, falso de lo contrario.
     */
    override fun existeFic(ruta: String): Boolean {
        try {
            if (File(ruta).isFile) {
                return true
            }
        } catch (e: SecurityException) {
            consola.showMessage("Error al comprobar si existe el fichero $ruta: ${e.message}")
        }
        return false
    }

    /**
     * Escribe información en un fichero especificado. Si el fichero no existe, no se crea automáticamente.
     *
     * @param fichero El fichero en el que se desea escribir.
     * @param info La información a escribir en el fichero.
     * @return Verdadero si la escritura fue exitosa, falso de lo contrario.
     */
    override fun escribir(fichero: File, info: String): Boolean {
        try {
            fichero.writeText(info)
        } catch (e: Exception) {
            consola.showMessage("Error al escribir en el archivo: ${e.message}")
            return false
        }
        return true
    }

    /**
     * Lee el contenido de un fichero y retorna una lista de strings, donde cada elemento representa una línea del fichero.
     *
     * @param fichero El fichero a leer.
     * @return Lista de strings con el contenido del fichero, o null si hubo un error al leer.
     */
    override fun leer(fichero: File): List<String>? {
        val lista : List<String>
        try {
            lista = fichero.readLines()
        } catch (e: Exception) {
            consola.showMessage("Error al leer las líneas del archivo: ${e.message}")
            return null
        }
        return lista
    }


    /**
     * Lee el fichero y escoge una palabra justo despues de la especificada
     * @param ruta Ruta del fichero a leer
     *
     * @return El String encontrado despues de lo especificado o null si da error
     */
    override fun leerIni(ruta: String): String? {
        var resultado: String? = null

        try {
            val fichero = File(ruta)
            val existe = fichero.exists()
            val esFile = fichero.isFile


            if (existe && esFile) {
                fichero.forEachLine { linea ->
                    if (linea.startsWith("type=")) {
                        resultado = linea.substringAfter("type=")
                    }

                }
            } else {
                throw FileNotFoundException()
            }
        }catch (e: FileNotFoundException){
            consola.showMessage("Error, fichero no encontrado")
        }
        return resultado
    }

    /**
     * Crea un fichero en la ruta especificada, con la posibilidad de incluir información inicial y de sobreescribir un fichero existente.
     *
     * @param ruta La ruta donde se desea crear el fichero.
     * @param info Información inicial para escribir en el fichero al crearlo. Por defecto está vacío.
     * @param sobreescribir Si es verdadero, sobrescribe el fichero si ya existe; de lo contrario, no modifica el fichero existente.
     * @return El fichero creado, o null si la creación falló o si el fichero ya existe y no se eligió sobreescribir.
     */
    override fun crearFic(ruta: String, info: String, sobreescribir: Boolean): File? {
        val fichero = File(ruta)

        try {
            if (sobreescribir) {
                fichero.writeText(info)
            } else {
                fichero.createNewFile()
                if (info.isNotEmpty()) {
                    fichero.appendText(info)
                }
            }
        } catch (e: Exception) {
            consola.showMessage("Error al crear el fichero $ruta: ${e.message}")
            return null
        }
        return fichero
    }


    /**
     * Procesa un fichero y devuelve un mapa de comandos separados por tipo.
     *
     * @param filePath La ruta del fichero a procesar.
     * @return Un mapa donde las claves son los tipos de comandos y los valores son listas de listas de strings con los detalles de los comandos.
     */
    override fun sacarComandosSeparados(filePath: String): Map<String, List<List<String>>> {
        val comandos = mutableMapOf<String, MutableList<List<String>>>()

        val lineas = File(filePath).readLines()

        var charActual: String? = null
        var detalles = mutableListOf<List<String>>()

        for (line in lineas) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue
            }

            if (trimmedLine.startsWith("-")) {
                if (charActual != null) {
                    comandos.getOrPut(charActual) { mutableListOf() }.addAll(detalles)
                    detalles = mutableListOf() // reset detalles for the new command type
                }
                charActual = trimmedLine
            } else {
                val parametros = trimmedLine.split(";").map { it.trim() }
                detalles.add(parametros) // Add the split parameters as a list
            }
        }

        // Añadir el último comando procesado
        if (charActual != null) {
            comandos.getOrPut(charActual) { mutableListOf() }.addAll(detalles)
        }

        return comandos
    }


}