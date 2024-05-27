package output

import management.Management
import service.ICtfsService
import service.IGroupService

/**
 * Clase Console que implementa la interfaz IOutputInfo para mostrar información formateada en la consola.
 *
 */
class Console: IOutputInfo {

    /**
     * Muestra un mensaje en la consola.
     *
     * @param message El mensaje a mostrar.
     * @param lineBreak Indica si se debe añadir un salto de línea al final del mensaje.
     */
    override fun showMessage(message: String, lineBreak:Boolean){
        if (lineBreak){
            println(message)
        } else {
            print(message)
        }
    }

    /**
     * Muestra la información de participación de un grupo en diferentes CTFs de manera formateada.
     *
     * @param grupoId El ID del grupo del cual se quiere mostrar la información.
     */
    override fun mostrarGruposBonitos(grupoId: Int,mapaValores: Map<String, List<Triple<Int, Int, Int>>>?){

        // MAP => String, List<Triple>
        //TRIPLE => IDCTFS, PUNTUACION, POSICION

        if (mapaValores != null) {
            if (mapaValores.isNotEmpty()) {

                val unicoValor = mapaValores.entries.first() //Esto te da los valores de la "primera vuelta" asi cuando saques los demas en el for no se repita el encabezado
                val valores = unicoValor.value.first()

                for ((key, values) in mapaValores) {
                    // Encabezado
                    showMessage("Procesado: Listado participacion del grupo '$key' ")
                    showMessage("GRUPO: $grupoId    $key   MEJORCTF: ${valores.first}, Posicion:${values.first().third}, Puntuacion: ${valores.second}")

                    // Encabezado de tabla
                    showMessage("%-5s | %10s | %5s ".format("CTF", "Puntuacion", "Posicion"))
                    showMessage("-----------------------------")

                    // Listar todas las participaciones
                    for (value in values) {
                        showMessage("%5s | %10s | %5s ".format(value.first, value.second, value.third))
                    }

                    showMessage("\n") // Hecho con un \n aposta + el salto de línea
                }
            } else {
                showMessage("ERROR, El grupo indicado no tiene ninguna participacion o no existe.")
            }
        }
    }

    /**
     * Muestra la información de todos los grupos y sus participaciones en diferentes CTFs de manera formateada.
     */
    override fun mostrarTodosLosGrupos(mapaValores: Map<Pair<String, Int>, List<Triple<Int, Int, Int>>>?) {

        //Pair < grupoDesc grupoId> Triple  <ctfId, puntuacion y posicion>


        if (mapaValores != null) {
            if (mapaValores.isNotEmpty()) {
                for ((key, values) in mapaValores) {

                    // Encabezado
                    showMessage("Procesado: Listado participacion del grupo '${key.first}' ")
                    showMessage("GRUPO: ${key.second}    ${key.first} MEJORCTF ${values.first().third } Posicion: ${values.first().third} Puntuacion ${values.first().second}")

                    // Encabezado de tabla
                    showMessage("%-5s | %10s | %5s ".format("CTF", "Puntuacion", "Posicion"))
                    showMessage("-----------------------------")

                    // Listar todas las participaciones
                    for (value in values) {
                        showMessage("%5s | %8s   | %8s ".format(value.first, value.second, value.third))
                    }

                    showMessage("\n") // Hecho con un \n aposta + el salto de línea
                }
            } else {
                showMessage("ERROR, No existe ningun Ctf.")
            }
        }
    }

    /**
     * Muestra la información de un CTF de forma formateada y bonita.
     *
     * @param ctfId El ID del CTF del cual se quiere mostrar la información.
     */
    override fun mostrarCtfBonito(ctfId:Int, mapas: Map<String, Int>?){

        if (!mapas.isNullOrEmpty()) {
            showMessage("Procesado: Listado participacion en el CTF '$ctfId' ")
            showMessage("GRUPO GANADOR: ${mapas.keys.first()}, Mejor puntuacion: ${mapas.values.first()}, Total participantes: ${mapas.size}") //primero saca el nombre del ganador, despues el numero de puntos y con el .size el numero de participantes que hay en el mapa

            //encabezado
            showMessage("%-15s | %8s".format("GRUPOS","Puntuacion"))
            showMessage("-------------------------------")

            mapas.forEach{ grupo ->

                val key = grupo.key
                val value = grupo.value
                showMessage("%-15s | %13s".format(key,value)) //saca a los grupos formateados bonitos
            }
        } else {
            showMessage("ERROR, valores incorrectos o inexistentes.")
        }
    }

}