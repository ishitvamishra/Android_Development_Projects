package com.example.datastore

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.datastore.ui.theme.DataStoreTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreTheme {

            }
        }
    }
}

@Entity
data class StudentData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val cgpa: String,
)

@Dao
interface StudentDataOperations {
    @Insert
    suspend fun insertStudentData(s: StudentData)

    @Query("SELECT * FROM StudentData")
    suspend fun fetchStudentData(): List<StudentData>
}

@Database(
    entities = [StudentData::class],
    version = 1
)
abstract class StudentDataAbstraction : RoomDatabase() {
    abstract fun studentDataOperations(): StudentDataOperations
}

object StudentDataRepository {
    fun getStudentData(context: Context): StudentDataAbstraction {
        return Room.databaseBuilder(
            context,
            StudentDataAbstraction::class.java,
            "StudentData"
        ).build()
    }
}

class StudentRoomArchi(a: Application) : AndroidViewModel(a) {
    val db = StudentDataRepository.getStudentData(getApplication())
    var studentList by mutableStateOf<List<StudentData>>(emptyList())
        private set

    fun insertStudentData(student: StudentData) {
        viewModelScope.launch {
            db.studentDataOperations().insertStudentData(student)
            getStudentData()
        }
    }

    fun getStudentData() {
        viewModelScope.launch {
            studentList = db.studentDataOperations().fetchStudentData()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DataStoreTheme {
        
    }
}
