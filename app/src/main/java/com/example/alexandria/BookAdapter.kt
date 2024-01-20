package com.example.alexandria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide





class BookAdapter(private val context: Context, private val bookList: ArrayList<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(book: Book)
    }

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(book: Book) {
            Glide.with(context)
                .load(book.image)
                .into(itemView.findViewById<ImageView>(R.id.bookImage))
            itemView.findViewById<TextView>(R.id.bookTitle).text = book.title
            itemView.findViewById<TextView>(R.id.bookAuthor).text = book.author


            itemView.setOnClickListener {
                listener?.onItemClick(book)
            }
        }
    }
}

