package com.rounak.merabills.di
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import android.content.Context
import androidx.room.Room
import com.rounak.merabills.constants.Constants
import com.rounak.merabills.data.db.AppDao
import com.rounak.merabills.data.db.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun providesAppDao(@Named("app_db") appDatabase: AppDatabase): AppDao = appDatabase.appDao()

    @Singleton
    @Provides
    @Named("app_db")
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase
            = Room.databaseBuilder(context, AppDatabase::class.java, Constants.DB_NAME).build()
}