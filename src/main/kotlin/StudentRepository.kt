import java.sql.Connection
import java.sql.SQLException

class StudentRepository {
    fun getAllStudents(): Result<List<String>> {
        val connectionDb: Connection = Bbdd.getConnection()
        val students = mutableListOf<String>()
        return try {
            val stmt = connectionDb.createStatement()
            val rs = stmt.executeQuery("SELECT name FROM students")
            while (rs.next()) {
                students.add(rs.getString("name"))
            }
            Result.success(students)
        } catch (e: SQLException) {
            Result.failure(e)
        } finally {
            connectionDb.close()
        }
    }

    fun updateStudents(students: List<String>): Result<Unit> {
        var connectionDb: Connection? = null
        return try {
            connectionDb = Bbdd.getConnection()
            connectionDb.autoCommit = false
            val stmt = connectionDb.createStatement()
            stmt.execute("DELETE FROM students")
            val ps = connectionDb.prepareStatement("INSERT INTO students (name) VALUES (?)")
            for (student in students) {
                ps.setString(1, student)
                ps.executeUpdate()
            }
            connectionDb.commit()
            Result.success(Unit)
        } catch (e: SQLException) {
            connectionDb?.rollback()
            Result.failure(e)
        } finally {
            connectionDb?.autoCommit = true
            connectionDb?.close()
        }
    }
}