import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class StudentScreen(
    private val gestorFichero: IFiles,
    private val studentsFile: File
): IStudentViewModel {

    companion object {
        private const val MAXCHARACTERS = 10
        private const val MAXNUMSTUDENTSVISIBLE = 7
    }

    private val _alumnos = mutableStateListOf<String>()
    override val alumnos: List<String> = _alumnos

    private val _infoMensaje = mutableStateOf("")
    override val infoMensaje: State<String> = _infoMensaje

    private val _mostrarMensaje = mutableStateOf(false)
    override val mostrarMensaje: State<Boolean> = _mostrarMensaje

    private val _nuevoAlumno = mutableStateOf("")
    override val nuevoAlumno: State<String> =_nuevoAlumno

    private val _seleccionarId = mutableStateOf(-1)
    override val seleccionarId: State<Int> =_seleccionarId

    override fun eliminarAlumno(index: Int) {
        if (index in _alumnos.indices) {
            _alumnos.removeAt(index)
        }
    }

    override fun anadirAlumno() {
        if (_nuevoAlumno.value.isNotBlank()) {
            _alumnos.add(_nuevoAlumno.value.trim())
            _nuevoAlumno.value = ""
        }
    }

    override fun mostrarMensajes(show: Boolean) {
        _mostrarMensaje.value = show
    }

    override fun newStudentChange(name: String) {
        if (name.length <= MAXCHARACTERS) {
            _nuevoAlumno    .value = name
        }
    }
    override fun seleccionAlumno(index: Int) {
        _seleccionarId.value = index
    }

    override fun cargarAlumnos() {
        val loadedStudents = gestorFichero.leerFichero(studentsFile)
        if (loadedStudents != null) {
            _alumnos.addAll(loadedStudents)
        } else {
            actualizarInfoMensaje("No se pudieron cargar los datos de los estudiantes.")
        }
    }

    override fun guardarAlumnos() {
        var error = ""
        val newStudentsFile = gestorFichero.crearFichero(studentsFile.absolutePath)
        if (newStudentsFile != null) {
            for (student in alumnos) {
                error = gestorFichero.escribirFichero(studentsFile, "$student\n")
                if (error.isNotEmpty()) {
                    break
                }
            }
            if (error.isNotEmpty()) {
                actualizarInfoMensaje(error)
            } else {
                actualizarInfoMensaje("Fichero guardado correctamente")
            }
        } else {
            actualizarInfoMensaje("No se pudo generar el fichero studentList.txt")
        }
    }

    override fun limpiarAlumnos() {
        _alumnos.clear()
    }

    override fun showScrollStudentListImage() = _alumnos.size > MAXNUMSTUDENTSVISIBLE

    private fun actualizarInfoMensaje(message: String) {
        _infoMensaje.value = message
        _mostrarMensaje.value = true
        CoroutineScope(Dispatchers.Default).launch {
            delay(2000)
            _mostrarMensaje.value = false
            _infoMensaje.value = ""
        }
    }
}