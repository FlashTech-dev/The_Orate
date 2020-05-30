package com.example.theorate.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.theorate.R
import com.example.theorate.registerlogin.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_user_message.*
import kotlinx.android.synthetic.main.user_row_newuser.view.*

class NewUserMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_message)

     //   supportActionBar?.title ="Select New User to Chat"
       // val adapter = GroupAdapter<ViewHolder>()
       // adapter.add(UserItem())
        //adapter.add(UserItem())
        //adapter.add(UserItem())
        //recycleview_newusermessage.adapter =adapter
        fetchUsers()
    }
    private fun fetchUsers()
    {
        val ref =FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val  adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user =it.getValue(User::class.java)
                    if (user!==null){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val intent =Intent(view.context, ChatLogActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                recycleview_newusermessage.adapter =adapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
class UserItem(val user: User): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text =user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)

    }
    override fun getLayout(): Int {
    return R.layout.user_row_newuser
    }
}

