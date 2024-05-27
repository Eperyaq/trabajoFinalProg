package output

import entity.CtfsEntity
import entity.GroupEntity

interface IOutputInfo {
    fun showMessage(message: String, lineBreak:Boolean = true)
    fun mostrarGruposBonitos(grupoId: Int,mapaValores: Map<String, List<Triple<Int, Int, Int>>>?)
    fun mostrarTodosLosGrupos(mapaValores: Map<Pair<String, Int>, List<Triple<Int, Int, Int>>>?)
    fun mostrarCtfBonito(ctfId:Int, mapas: Map<String, Int>?)

}