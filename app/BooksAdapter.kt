class BookAdapter(private var books: List<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // A filter to show only the books that match the selected category
    var filter: String = "All"
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // A view holder that holds the book item layout
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val bookName: TextView = itemView.findViewById(R.id.bookName)
        val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
    }

    // Inflate the book item layout from XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    // Bind the book data to the book item layout
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        // Use Glide library to load the image from the URL
        Glide.with(holder.itemView.context).load(book.image).into(holder.bookImage)
        holder.bookName.text = book.title
        holder.bookAuthor.text = book.author
    }

    // Return the number of books that match the filter
    override fun getItemCount(): Int {
        return books.count { it.genre == filter || filter == "All" }
    }
}