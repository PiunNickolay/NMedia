package ru.netology.learningandtrying.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import ru.netology.learningandtrying.BuildConfig
import ru.netology.learningandtrying.Counts
import ru.netology.learningandtrying.R
import ru.netology.learningandtrying.databinding.CardAdBinding
import ru.netology.learningandtrying.databinding.CardPostBinding
import ru.netology.learningandtrying.dto.Ad
import ru.netology.learningandtrying.dto.AttachmentType
import ru.netology.learningandtrying.dto.FeedItem
import ru.netology.learningandtrying.dto.Post


interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onPost(post: Post)
    fun onImage(post: Post)
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback) {

    override fun getItemViewType(position: Int): Int {
         return when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknow item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val view =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(view, onInteractionListener)
            }

            R.layout.card_ad -> {
                val view = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(view)
            }

            else -> error("unknow view type")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val position = getItem(position)){
            is Ad -> (holder as? AdViewHolder)?.bind(position)
            is Post -> (holder as? PostViewHolder)?.bind(position)
            null -> error("unknow item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        val url = "${BuildConfig.BASE_URL}/media/${ad.image}"
        Glide.with(binding.image)
            .load(url)
            .placeholder(R.drawable.ic_is_not_image_24)
            .error(R.drawable.ic_error_24)
            .into(binding.image)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = with(binding) {
        root.setOnClickListener {
            onInteractionListener.onPost(post)
        }
        val url = "${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}"
        Glide.with(binding.avatar)
            .load(url)
            .placeholder(R.drawable.ic_is_not_image_24)
            .error(R.drawable.ic_error_24)
            .timeout(10_000)
            .circleCrop()
            .into(binding.avatar)
        author.text = post.author
        content.text = post.content
        like.apply {
            isChecked = post.likedByMe
            text = post.likes.toString()
        }
        share.text = Counts.countFormat(post.shareCount)
        view.text = Counts.countFormat(post.viewCount)
        like.setOnClickListener {
            onInteractionListener.onLike(post)
        }
        like.isClickable = true

        share.setOnClickListener {
            onInteractionListener.onShare(post)
        }
        share.isClickable = true

        menu.isVisible = post.ownedByMe

        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_actions)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onInteractionListener.onRemove(post)
                            true
                        }

                        R.id.edit -> {
                            onInteractionListener.onEdit(post)
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }
        menu.isClickable = true

        if (!post.video.isNullOrBlank()) {
            binding.videoContainer.visibility = View.VISIBLE
            binding.playButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, post.video!!.toUri())
                it.context.startActivity(intent)
            }
            playButton.isClickable = true
        } else {
            binding.videoContainer.visibility = View.GONE
        }

        if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) {
            val imageUrl = "http://10.0.2.2:9999/media/${post.attachment.url}"
            binding.postImage.visibility = View.VISIBLE

            Glide.with(binding.postImage)
                .load(imageUrl)
                .fitCenter()
                .timeout(10000)
                .into(binding.postImage)

            binding.postImage.setOnClickListener {
                onInteractionListener.onImage(post)
            }
        } else {
            binding.postImage.visibility = View.GONE
        }
    }

}

object PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }


}