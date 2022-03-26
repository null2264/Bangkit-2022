package io.github.null2264.githubuser.lib

import android.content.Context
import android.content.res.Configuration

class Common {
    companion object {
        const val GITHUB_BASE_URL = "https://github.com/"
        const val GITHUB_API_BASE_URL = "https://api.github.com/"

        fun isNightModeOn(ctx: Context): Boolean {
            return when (ctx.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> true
            }
        }
    }
}