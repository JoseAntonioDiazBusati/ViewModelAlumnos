import java.sql.Connection
import java.sql.DriverManager

object Bbdd {
    private const val URL = "jdbc:mysql://localhost:3306/studentdb"
    private const val USER = "studentuser"
    private const val PASSWORD = "password"
    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (e: ClassNotFoundException) {
            e.printStackTrace();
        }
    }
    fun getConnection(): Connection {
        return DriverManager.getConnection(URL, USER, PASSWORD)
    }
}