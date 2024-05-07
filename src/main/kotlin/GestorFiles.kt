import java.io.File

class GestorFiles(): IFiles{
    override fun exiteDirectorio(ruta: String): String {
        try {
            if(File(ruta).isDirectory){
                return ""
            }
        }catch(e:SecurityException){
            return "Error al comprobar el directorio $ruta: ${e.message}"
        }
        return "No existe el directorio"
    }

    override fun existeFichero(ruta: String): String {
        try {
            if(File(ruta).isFile){
                return ""
            }
        }catch(e:SecurityException){
            return "Error al comprobar el fichero $ruta: ${e.message}"
        }
        return "No existe el fichero"
    }

    override fun crearDirectorio(ruta: String): String {
        val directorio = File(ruta)
        try {
            if (!directorio.isDirectory){
                if (!directorio.mkdirs()){
                    return "No se pudo crear la ruta del directorio"
                }
            }
        }catch(e:Exception){
            return "Error al crear el directorio $ruta: ${e.message}"
        }
        return ""
    }

    override fun crearFichero(ruta: String, mensaje: String, sobreescribir: Boolean): File? {
        val fichero = File(ruta)
        crearDirectorio(fichero.parent)
        try {
            if (sobreescribir){
                fichero.writeText(mensaje)
            }else{
                fichero.createNewFile()
                if (mensaje.isNotEmpty()){
                    fichero.appendText(mensaje)
                }
            }
        }catch (e:Exception){
            return null
        }
        return fichero
    }

    override fun leerFichero(fichero: File): List<String>? {
        val lista : List<String>
        try {
            lista = fichero.readLines()
        }catch (e:Exception){
            return null
        }
        return lista
    }

    override fun escribirFichero(fichero: File, mensaje: String): String {
        try {
            fichero.appendText(mensaje)
        }catch (e: Exception){
            return "Error al escribir en el fichero ${e.message}:"
        }
        return ""
    }

    override fun buscarFicheroEmpiezaPor(directorio: File, nombreFicheroInicio: String): File? {
        val ficheros = directorio.listFiles { fichero ->
            fichero.isFile && fichero.name.startsWith(nombreFicheroInicio)
        }
        if (ficheros != null && ficheros.isNotEmpty()) {
            val ultimoModificado = ficheros.maxByOrNull { it.lastModified() }
            if (ultimoModificado != null) {
                return ultimoModificado
            }
        }
        return null
    }

    override fun buscarFicheroMensaje(fichero: File, mensaje: String): String? {
        val lineas = leerFichero(fichero)
        if (lineas != null) {
            for (linea in lineas) {
                if (linea == mensaje.dropLast(1)) {
                    return ""
                }
            }
        }
        return null
    }

    override fun buscarFicheroFinalizaPor(directorio: File, empiezaPor: String, terminaPor: String): File? {
        val ficheros = directorio.listFiles { fichero ->
            fichero.isFile && fichero.name.startsWith(empiezaPor) && fichero.name.endsWith(terminaPor)
        }

        if (ficheros != null && ficheros.isNotEmpty()) {
            val ultimoModificado = ficheros.maxByOrNull { it.lastModified() }
            if (ultimoModificado != null) {
                return ultimoModificado
            }
        }

        return null
    }
}