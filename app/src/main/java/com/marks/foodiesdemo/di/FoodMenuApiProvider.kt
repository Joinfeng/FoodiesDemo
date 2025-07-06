package com.marks.foodiesdemo.di

import com.marks.foodiesdemo.model.data.FoodMenuApi
import com.marks.foodiesdemo.model.data.FoodMenuApi.Companion.API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Retrofit 负责将接口定义转为网络请求和数据解析，但它本身不处理底层的 HTTP 请求。 OkHttp 是一个高效的 HTTP 客户端，负责真正发起网络请求、处理连接、拦截器等。  Retrofit 默认底层用 OkHttp 作为网络请求库，两者结合可以：
 * 利用 OkHttp 的高效、灵活和强大功能（如拦截器、缓存、超时设置等）
 * Retrofit 专注于接口封装和数据解析，OkHttp 专注于网络通信
 * 方便扩展和自定义网络行为
 * 总结：Retrofit 负责“怎么用”，OkHttp 负责“怎么连”，两者配合让网络请求更强大和灵活
 */

@InstallIn(SingletonComponent::class)
@Module
class FoodMenuApiProvider {

    @Provides
    @Singleton
    fun provideAuthInterceptorOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit
            .Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodMenuApiService(
        retrofit: Retrofit
    ): FoodMenuApi.Service {
        return retrofit.create(FoodMenuApi.Service::class.java)
    }

}