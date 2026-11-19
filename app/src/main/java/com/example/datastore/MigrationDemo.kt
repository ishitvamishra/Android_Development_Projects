package com.example.datastore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class MigrationDemo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = StudentDetailDatabase.getDatabase(this)
        val dao = database.studentDetailDao()
        setContent {

            StudentDetailScreen(dao )
        }
    }
}

//with 1 parameters
/*
@Entity(tableName = "student_detail")
data class StudentDetail(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String
)
@Dao
interface StudentDetailDao {

    @Insert
    suspend fun insertStudent(student: StudentDetail)

    @Query("SELECT * FROM student_detail")
    fun getAllStudents(): Flow<List<StudentDetail>>
}

@Database(
    entities = [StudentDetail::class],
    version = 1,
    exportSchema = false
)
abstract class StudentDetailDatabase : RoomDatabase() {

    abstract fun studentDetailDao(): StudentDetailDao

    companion object {

        @Volatile
        private var INSTANCE: StudentDetailDatabase? = null

        fun getDatabase(context: Context): StudentDetailDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDetailDatabase::class.java,
                    "student_detail_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }}

@Composable
fun StudentDetailScreen(dao: StudentDetailDao) {

    var name by remember { mutableStateOf("") }

    val students by dao.getAllStudents()
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Student Detail App",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Student Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                if (name.isNotBlank()) {

                    scope.launch {

                        dao.insertStudent(
                            StudentDetail(name = name)
                        )

                        name = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Student")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Student Records",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn {

            items(students) { student ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text("ID: ${student.id}")
                        Text("Name: ${student.name}")
                    }
                }
            }
        }
    }
}
*/

// with 2 parameters
@Entity(tableName = "student_detail")
data class StudentDetail(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    @ColumnInfo(defaultValue = "0.0")
    val cgpa: Double = 0.0
)


@Dao
interface StudentDetailDao {

    @Insert
    suspend fun insertStudent(student: StudentDetail)

    @Query("SELECT * FROM student_detail ORDER BY id")
    fun getAllStudents(): Flow<List<StudentDetail>>
}

// MIGRATION FROM VERSION 1 TO VERSION 2
val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            "ALTER TABLE student_detail ADD COLUMN cgpa REAL NOT NULL DEFAULT 0.0"
        )
    }
}

@Database(
    entities = [StudentDetail::class],
    version = 2,
    exportSchema = false
)
abstract class StudentDetailDatabase : RoomDatabase() {

    abstract fun studentDetailDao(): StudentDetailDao

    companion object {

        @Volatile
        private var INSTANCE: StudentDetailDatabase? = null

        fun getDatabase(context: Context): StudentDetailDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDetailDatabase::class.java,
                    "student_detail_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}


class MigrationDemo1 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = StudentDetailDatabase.getDatabase(this)
        val dao = database.studentDetailDao()

        setContent {
            MaterialTheme {
                StudentDetailScreen(dao)
            }
        }
    }
}


@Composable
fun StudentDetailScreen(dao: StudentDetailDao) {

    var name by remember { mutableStateOf("") }
    var cgpa by remember { mutableStateOf("") }

    val students by dao.getAllStudents()
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Student Detail App - Migration",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Student Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cgpa,
            onValueChange = { cgpa = it },
            label = { Text("Student CGPA") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                if (name.isNotBlank() && cgpa.toDoubleOrNull() != null) {

                    scope.launch {

                        dao.insertStudent(
                            StudentDetail(
                                name = name,
                                cgpa = cgpa.toDouble()
                            )
                        )

                        name = ""
                        cgpa = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Student")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Student Records",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn {

            items(students) { student ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text("ID: ${student.id}")

                        Text("Name: ${student.name}")

                        Text("CGPA: ${student.cgpa}")
                    }
                }
            }
        }
    }
}
