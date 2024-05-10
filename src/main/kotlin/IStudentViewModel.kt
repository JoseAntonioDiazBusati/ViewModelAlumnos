import androidx.compose.runtime.*

interface IStudentViewModel {
    val nuevoAlumno: State<String>

    val alumnos: List<String>

    val infoMensaje: State<String>

    val mostrarMensaje: State<Boolean>

    val seleccionarId: State<Int>

    fun anadirAlumno()

    fun eliminarAlumno(index: Int)

    fun cargarAlumnos()

    fun guardarAlumnos()

    fun limpiarAlumnos()

    fun showScrollStudentListImage(): Boolean

    fun newStudentChange(name: String)

    fun seleccionAlumno(index: Int)

    fun mostrarMensajes(show: Boolean)
}