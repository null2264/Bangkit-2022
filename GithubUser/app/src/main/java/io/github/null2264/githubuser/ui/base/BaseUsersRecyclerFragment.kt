package io.github.null2264.githubuser.ui.base

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.null2264.githubuser.data.UsersRecyclerAdapter
import io.github.null2264.githubuser.data.database.entity.UserEntity

abstract class BaseUsersRecyclerFragment(id: Int) : Fragment(id) {
    lateinit var recyclerView: RecyclerView
    open val adapter = UsersRecyclerAdapter()

    fun setupRecyclerList(hasFixedSize: Boolean = true) {
        recyclerView.apply {
            layoutManager = if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                GridLayoutManager(context, 2)
            else
                LinearLayoutManager(context)

            setHasFixedSize(hasFixedSize)

            this.adapter = this@BaseUsersRecyclerFragment.adapter
        }
    }

    open fun bind(user: UserEntity) {}
}