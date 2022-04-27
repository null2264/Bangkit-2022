package io.github.null2264.dicodingstories.helper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import io.github.null2264.dicodingstories.HiltTestActivity
import io.github.null2264.dicodingstories.R
import java.io.IOException
import java.io.InputStreamReader

object Util {
    fun stringFromFile(fileName: String): String {
        try {
            val ctx = ApplicationProvider.getApplicationContext<Context>()
            val stream = ctx.assets.open(fileName)
            val builder = StringBuilder()
            val reader = InputStreamReader(stream, "UTF-8")
            reader.readLines().forEach { builder.append(it) }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        }
    }

    inline fun <reified T : Fragment> launchFragmentInHiltContainer(
        fragmentArgs: Bundle? = null,
        @StyleRes themeResId: Int = R.style.Theme_DicodingStories,
        fragmentFactory: FragmentFactory? = null,
        crossinline action: Fragment.() -> Unit = {},
    ) {
        val intent = Intent.makeMainActivity(
            ComponentName(
                ApplicationProvider.getApplicationContext(),
                HiltTestActivity::class.java
            )
        ).putExtra(
            "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
            themeResId
        )

        ActivityScenario.launch<HiltTestActivity>(intent).onActivity { activity ->
            fragmentFactory?.let {
                activity.supportFragmentManager.fragmentFactory = it
            }
            val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
                Preconditions.checkNotNull(T::class.java.classLoader),
                T::class.java.name
            )

            fragment.arguments = fragmentArgs
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()

            fragment.action()
        }
    }
}