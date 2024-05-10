class StudentViewModelBBDD(private val studentRepository: StudentRepository){
    fun getAllStudents(): Result<List<String>> {
        return studentRepository.getAllStudents()
    }

    fun updateStudents(students: List<String>): Result<Unit> {
        return studentRepository.updateStudents(students)
    }
}