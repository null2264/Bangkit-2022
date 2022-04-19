package io.github.null2264.dicodingstories

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.null2264.dicodingstories.data.Repository
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.data.api.AuthService
import io.github.null2264.dicodingstories.data.preference.PreferencesHelper
import io.github.null2264.dicodingstories.lib.Common
import io.github.null2264.dicodingstories.lib.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // --- [ Start of qualifier defines ]

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LoggedInRetrofitClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LoggedInOkHttpClient

    // AuthRetrofitClient only used for login and register
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofitClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthOkHttpClient

    // --- [ End of qualifier defines ]

    @Singleton
    @Provides
    fun providesPreferences(@ApplicationContext context: Context) = PreferencesHelper(context)

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    @LoggedInOkHttpClient
    fun providesOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @LoggedInRetrofitClient
    fun providesRetroFit(@LoggedInOkHttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(Common.API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    @AuthOkHttpClient
    fun providesAuthOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    @AuthRetrofitClient
    fun providesAuthRetroFit(@AuthOkHttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(Common.API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun providesApiService(@LoggedInRetrofitClient retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun providesAuthApiService(@AuthRetrofitClient retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun providesRepository(apiService: ApiService) = Repository(apiService)
}