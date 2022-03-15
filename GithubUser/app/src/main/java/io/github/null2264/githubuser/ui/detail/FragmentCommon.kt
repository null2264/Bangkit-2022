package io.github.null2264.githubuser.ui.detail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.null2264.githubuser.lib.User

fun showRecyclerList(
    context: Context?,
    destClass: Class<*>,
    owner: LifecycleOwner,
    recyclerView: RecyclerView,
    data: LiveData<List<User>>,
) {
    if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    else
        recyclerView.layoutManager = LinearLayoutManager(context)

    val adapter = DetailFollowsAdapter()
    recyclerView.adapter = adapter

    data.observe(owner) { users ->
        adapter.setList(users)
    }

    adapter.setOnItemClickCallback(object : DetailFollowsAdapter.OnItemClickCallback {
        override fun onItemClicked(data: User) {
            context.startActivity(Intent(context, destClass)
                .putExtra("USER_DATA", data))
        }
    })
}