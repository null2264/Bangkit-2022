package io.github.null2264.githubuser.data

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.null2264.githubuser.lib.User
import io.github.null2264.githubuser.ui.detail.DetailActivity

interface UsersRecyclerInterface {
    fun showRecyclerList(
        context: Context?,
        owner: LifecycleOwner,
        recyclerView: RecyclerView,
        data: LiveData<List<User>>,
    ) {
        if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        else
            recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = UsersRecyclerAdapter()
        recyclerView.adapter = adapter

        data.observe(owner) { users ->
            adapter.submitList(users)
        }

        adapter.setOnItemClickCallback(object : UsersRecyclerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                context.startActivity(Intent(context, DetailActivity::class.java)
                    .putExtra("USER_DATA", data))
            }
        })
    }
}