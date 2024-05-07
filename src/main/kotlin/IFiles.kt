import java.io.File

interface IFiles {

    fun exiteDirectorio(ruta: String): String

    fun existeFichero(ruta: String): String

    fun escribirFichero(fichero: File, mensaje: String): String

    fun leerFichero(fichero: File): List<String>?

    fun crearDirectorio(ruta: String): String

    fun crearFichero(ruta: String, mensaje: String = "", sobreescribir: Boolean = true): File?

    fun buscarFicheroMensaje(fichero: File, mensaje: String): String?

    fun buscarFicheroEmpiezaPor(directorio: File, nombreFicheroInicio: String): File?

    fun buscarFicheroFinalizaPor(directorio: File, empiezaPor: String, terminaPor: String): File?
}