package com.denson.noteapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private lateinit var listViewNotes: ListView
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonUpdate: Button
    private lateinit var buttonDelete: Button

    private lateinit var noteViewModel: NoteViewModel
    private var selectedNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewNotes = findViewById(R.id.listViewNotes)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonDelete = findViewById(R.id.buttonDelete)

        // Initialize ViewModel
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Observe LiveData for changes
        noteViewModel.allNotes.observe(this, Observer { notes ->
            // Create a simple adapter to display note titles
            val titles = notes.map { it.title }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)
            listViewNotes.adapter = adapter
        })

        // Handle Add Button
        buttonAdd.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            if (title.isNotEmpty() && content.isNotEmpty()) {
                val note = Note(title = title, content = content)
                noteViewModel.insert(note)
                clearInputs()
            }
        }

        // Handle Update Button
        buttonUpdate.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            selectedNote?.let {
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val updatedNote = it.copy(title = title, content = content)
                    noteViewModel.update(updatedNote)
                    clearInputs()
                }
            }
        }

        // Handle Delete Button
        buttonDelete.setOnClickListener {
            selectedNote?.let {
                noteViewModel.delete(it.id)
                clearInputs()
            }
        }

        // Handle ListView Item Click
        listViewNotes.setOnItemClickListener { _, _, position, _ ->
            val notes = noteViewModel.allNotes.value ?: return@setOnItemClickListener
            selectedNote = notes[position]
            editTextTitle.setText(selectedNote?.title)
            editTextContent.setText(selectedNote?.content)
        }
    }

    // Helper function to clear input fields
    private fun clearInputs() {
        editTextTitle.text.clear()
        editTextContent.text.clear()
        selectedNote = null
    }
}
