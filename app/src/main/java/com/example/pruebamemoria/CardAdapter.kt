package com.example.pruebamemoria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private var cards: List<Card>,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCard: ImageView = view.findViewById(R.id.ivCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }


    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]

        if (card.isFaceUp || card.isMatched) {
            holder.ivCard.setImageResource(card.imageResId)
            holder.ivCard.setPadding(0, 0, 0, 0)
        } else {
            holder.ivCard.setImageResource(R.drawable.card_back)
            val padding = 16
            holder.ivCard.setPadding(padding, padding, padding, padding)
        }

        holder.ivCard.alpha = if (card.isMatched) 0.5f else 1.0f
        // ===========================

        holder.itemView.setOnClickListener {
            if (!card.isMatched) {
                onCardClick(position)
            }
        }
    }

    override fun getItemCount() = cards.size

    fun updateCards(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }
}
