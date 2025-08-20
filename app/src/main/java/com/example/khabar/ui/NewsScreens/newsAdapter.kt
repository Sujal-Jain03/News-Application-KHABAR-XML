package com.example.khabar.ui.NewsScreens


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.khabar.R
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.example.khabar.data.model.Article
import java.text.SimpleDateFormat
import java.util.Locale

class NewsPageAdapter: RecyclerView.Adapter<NewsPageAdapter.NewsPageHolder>() {
    private var article = listOf<Article>()


    inner class NewsPageHolder(
        parent: ViewGroup
    ): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_layout,
            parent,
            false)
    ){
        private val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        private val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        private val newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        private val authorText: TextView = itemView.findViewById(R.id.author)
        private val dateText: TextView = itemView.findViewById(R.id.date)

        fun bind(article:Article){
            newsTitle.text = article.title
            newsDescription.text = article.description
            authorText.text = article.author ?: "Unknown Author"
            dateText.text = article.publishedAt?.formatToDisplayDate() ?:"No date"

            if (!article.urlToImage.isNullOrEmpty()) {
                newsImage.load(article.urlToImage) {
                    placeholder(R.drawable.ic_placeholder) // Your placeholder
                    error(R.drawable.your_image_here) // Error fallback
                    crossfade(true)
                    transformations(RoundedCornersTransformation(16f))
                }
            } else {
                // Load placeholder when image URL is null
                newsImage.setImageResource(R.drawable.ic_placeholder)
            }

            newsImage.load(article.urlToImage){

                //show a placeholder while loading
                placeholder(R.drawable.ic_placeholder)
                //show this if image fails to load
                error(R.drawable.ic_placeholder)

                //crossfade animate for smooth loading
                crossfade(true)
                crossfade(300)

                scale(Scale.FILL)

                transformations(RoundedCornersTransformation(8f))

                listener(
                    onStart = {/* Show loading indicator if needed*/},
                    onSuccess = { _, _ ->  },
                    onError = { _, error ->
                        Log.e("NewsPageAdapter","Image loading failed",error.throwable)
                    }
                )
            }
        }
    }

    private fun String.formatToDisplayDate(): String =
        try{
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(this)
            outputFormat.format(date!!)
        }catch(e: Exception){
            this
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    )= NewsPageHolder(parent)

    override fun onBindViewHolder(holder: NewsPageHolder, position: Int) {
        holder.bind(article[position])
    }

    override fun getItemCount() = article.size


    fun setArticles(newArticles: List<Article>){
        article = newArticles
        notifyDataSetChanged()

    }
}