package com.example.khabar.di

import com.example.khabar.data.api.ApiServices
import com.example.khabar.data.repository.AuthRepository
import com.example.khabar.utilis.AppConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import com.example.khabar.data.datasource.NewsDatasource
import com.example.khabar.data.datasource.NewsDatasourceImpl
import com.example.khabar.data.repository.NewsRepo

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository =
        AuthRepository(
            auth = auth,
            firestore = firestore,
        )

    @Provides
    @Singleton
    fun providesRetrofit():Retrofit{
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply{
            level = HttpLoggingInterceptor.Level.BASIC }
        val httpClient = OkHttpClient().newBuilder().addInterceptor(httpLoggingInterceptor)
        httpClient.apply{
            readTimeout(60, TimeUnit.SECONDS)
        }
        val moshi = Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
        return Retrofit
            .Builder()
            .baseUrl(AppConstants.APP_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClient.build())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiServices = retrofit.create(ApiServices::class.java)
    @Provides
    @Singleton
    fun providesNewsDatasource(apiServices: ApiServices) : NewsDatasource = NewsDatasourceImpl(apiServices)

    @Provides
    @Singleton
    fun providesNewsRepository(newsDatasource: NewsDatasource): NewsRepo  = NewsRepo(newsDatasource)
}
