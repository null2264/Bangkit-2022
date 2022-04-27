package io.github.null2264.dicodingstories.data.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import io.github.null2264.dicodingstories.data.model.Story
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object Util {
    class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem == newItem
    }

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {},
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                data = t
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        try {
            afterObserve.invoke()
            if (!latch.await(time, timeUnit))
                throw TimeoutException("LiveData value was never set.")
        } finally {
            this.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    fun setupDiffer() = AsyncPagingDataDiffer(
        diffCallback = DiffCallback(),
        updateCallback = NoopListCallback(),
        workerDispatcher = Dispatchers.Main,
    )
}