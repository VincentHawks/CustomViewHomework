package com.swtecnn.contactlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(val contents: List<String>): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(val contactView: ContactView): RecyclerView.ViewHolder(contactView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val contactView = ContactView(parent.context)

        contactView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        )

        return ContactViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactView.contact = contents[position]
    }

    override fun getItemCount() = contents.size

}