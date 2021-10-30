package com.example.recipeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recipe_row.view.*
import com.example.recipeapp.databinding.RecipeRowBinding

class NoteAdapter(private val activity: MainActivity, private val items: ArrayList<Note>): RecyclerView.Adapter<NoteAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: RecipeRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ItemViewHolder {
        return ItemViewHolder(
            RecipeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteAdapter.ItemViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            tvrecipeName.text = item.title
            tvrecipeAuthor.text = item.author
            tvrecipeIngr.text = item.ingredients
            tvrecipeInst.text = item.instructions
            ibEditNote.setOnClickListener {
                activity.raiseDialog(item.id,item.title,item.author,item.ingredients,item.instructions)
            }
            ibDeleteNote.setOnClickListener {
                activity.deleteDialog(item.id)
            }
        }
    }

    override fun getItemCount() = items.size
}