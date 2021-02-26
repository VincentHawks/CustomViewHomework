package com.swtecnn.contactlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), DisplayStateCallback {

    lateinit var contactList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactList = findViewById(R.id.list)

        contactList.adapter = ContactAdapter(listOf(
            "Alice",
            "Bob",
            "Charlie",
            "Dorothy",
            "Ethan"
        ))

        contactList.adapter!!.notifyDataSetChanged()

        for(child in contactList) {
            (child as ContactView).parentCallback = this@MainActivity
        }
    }

    override fun notifyDisplayStateChanged() {
        for(child in contactList) {
            (child as ContactView).displayText()
        }
    }
}