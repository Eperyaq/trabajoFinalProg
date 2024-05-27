package gestorFicheros

import java.io.File

/**
 * Interfaz que define operaciones de manejo de ficheros y directorios utilizados en el juego de bingo.
 * Incluye métodos para verificar existencias, escribir, leer, crear archivos y directorios.
 */
interface IFicheros {


    /**
     * Verifica si un archivo existe en la ruta especificada.
     *
     * @param ruta La ruta del archivo a verificar.
     * @return Verdadero si el archivo existe, falso de lo contrario.
     */
    fun existeFic(ruta: String): Boolean

    /**
     * Escribe información en un fichero especificado. Si el fichero no existe, no se crea automáticamente.
     *
     * @param fichero El fichero en el que se desea escribir.
     * @param info La información a escribir en el fichero.
     * @return Verdadero si la escritura fue exitosa, falso de lo contrario.
     */
    fun escribir(fichero: File, info: String): Boolean

    /**
     * Lee el contenido de un fichero y retorna una lista de strings, donde cada elemento representa una línea del fichero.
     *
     * @param fichero El fichero a leer.
     * @return Lista de strings con el contenido del fichero, o null si hubo un error al leer.
     */
    fun leer(fichero: File): List<String>?


    /**
     * Crea un fichero en la ruta especificada, con la posibilidad de incluir información inicial y de sobreescribir un fichero existente.
     *
     * @param ruta La ruta donde se desea crear el fichero.
     * @param info Información inicial para escribir en el fichero al crearlo. Por defecto está vacío.
     * @param sobreescribir Si es verdadero, sobrescribe el fichero si ya existe; de lo contrario, no modifica el fichero existente.
     * @return El fichero creado, o null si la creación falló o si el fichero ya existe y no se eligió sobreescribir.
     */
    fun crearFic(ruta: String, info: String = "", sobreescribir: Boolean = true): File?

    /**
     * Lee el fichero y escoge una palabra justo despues de la especificada
     * @param ruta Ruta del fichero a leer
     * @return El String encontrado despues de lo especificado o null si da error
     */
    fun leerIni(ruta: String): String?

    /**
     * Procesa un fichero y devuelve un mapa de comandos separados por tipo.
     *
     * @param filePath La ruta del fichero a procesar.
     * @return Un mapa donde las claves son los tipos de comandos y los valores son listas de listas de strings con los detalles de los comandos.
     */
    fun sacarComandosSeparados(filePath: String): Map<String, List<List<String>>>
}