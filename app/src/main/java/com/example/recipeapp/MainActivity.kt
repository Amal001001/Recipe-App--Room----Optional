package com.example.recipeapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainActivity : AppCompatActivity() {
    private val noteDao by lazy { NoteDatabase.getDatabase(this).noteDao() }
    private val repository by lazy { NoteRepository(noteDao) }

    private lateinit var rvNotes: RecyclerView

    private lateinit var notes: ArrayList<Note>
    lateinit var floatingActionButton:FloatingActionButton
    lateinit var linearLayout:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingActionButton = findViewById(R.id.floatingActionButton)
        linearLayout = findViewById(R.id.linearLayout)
        floatingActionButton.setOnClickListener{rvNotes.isVisible = false
            linearLayout.isVisible = true}

        notes = arrayListOf()

        val title = findViewById<View>(R.id.etTitle) as EditText
        val author = findViewById<View>(R.id.etAuthor) as EditText
        val inge = findViewById<View>(R.id.etIngr) as EditText
        val ins = findViewById<View>(R.id.etInst) as EditText
        val savebtn = findViewById<View>(R.id.badd) as Button
        val showbtn = findViewById<View>(R.id.bview) as Button

        savebtn.setOnClickListener {
            if (title.text.toString() != "" && author.text.toString() != "" && inge.text.toString() != "" && ins.text.toString() != "") {
                val t = title.text.toString()
                val a = author.text.toString()
                val ing = inge.text.toString()
                val inst = ins.text.toString()
                addNote(t, a, ing, inst)
                Toast.makeText(applicationContext, "Save Success!", Toast.LENGTH_SHORT).show()

                title.setText("")
                author.setText("")
                inge.setText("")
                ins.setText("")

                updateRV()
            }
        }

        rvNotes = findViewById(R.id.rvNotes)
        updateRV()
        readFromDB()

        showbtn.setOnClickListener { linearLayout.isVisible = false
            readFromDB()
            rvNotes.isVisible = true}
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRV() {
        rvNotes.adapter = NoteAdapter(this, notes)
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun readFromDB() {
        CoroutineScope(IO).launch {
            notes.clear()
            notes.addAll(
                noteDao.getNotes()
            )
            withContext(Dispatchers.Main) {
                rvNotes.adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun addNote(title: String, author: String, ingredients: String, instructions: String) {
            CoroutineScope(IO).launch {
                repository.addNote(Note(0, title, author, ingredients, instructions))

            }
    }

    private fun editNote(noteID: Int,newTitle: String,newAuthor:String, newIng:String, newInst:String) {
            CoroutineScope(IO).launch {
                repository.updateNote(Note(noteID,newTitle,newAuthor,newIng,newInst))
                readFromDB()
            }
    }

    fun deleteNote(noteID: Int) {
            CoroutineScope(IO).launch {
                repository.deleteNote(Note(noteID, "","","",""))
                readFromDB()
            }
    }

    fun raiseDialog(noteID: Int,newTitle: String,newAuthor:String, newIng:String, newInst:String) {

        val alert = AlertDialog.Builder(this)
        alert.setTitle("Update Recipe")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val updatedtitle = EditText(this)
        updatedtitle.setText(newTitle)
        layout.addView(updatedtitle)

        val updatedauthor = EditText(this)
        updatedauthor.setText(newAuthor)
        layout.addView(updatedauthor)

        val updateding = EditText(this)
        updateding.setText(newIng)
        layout.addView(updateding)

        val updatedinst = EditText(this)
        updatedinst.setText(newInst)
        layout.addView(updatedinst)

        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("Update") { _, _ ->
            val updatedtitle = updatedtitle.text.toString()
            val updatedauthor = updatedauthor.text.toString()
            val updateding = updateding.text.toString()
            val updatedinst = updatedinst.text.toString()

            editNote(noteID,updatedtitle,updatedauthor, updateding, updatedinst)
            Toast.makeText(this, "Updated Sucessfully", Toast.LENGTH_LONG).show()
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
        }

    @SuppressLint("SetTextI18n")
    fun deleteDialog(Id: Int){
        val dialogBuilder = AlertDialog.Builder(this)
        val confirmDelete = TextView(this)
        confirmDelete.text = "  Are you sure you want to delete this recipe?"
        dialogBuilder
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    _, _ -> deleteNote(Id) })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.cancel() })
        val alert = dialogBuilder.create()
        alert.setTitle("Delete Recipe")
        alert.setView(confirmDelete)
        alert.show()
    }

}
