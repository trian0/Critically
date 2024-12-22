package com.example.critically.data.repos

import com.example.critically.data.Result
import com.example.critically.data.api.ApiBooks
import com.example.critically.models.Item
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class BooksRepositoryImpl(
    private val apiBook: ApiBooks
) : BooksRepository {
    override suspend fun getSearchedBooksList(bookName: StateFlow<String>): Flow<Result<List<Item>>> {
        return flow {
            val booksFromApi = try {
                apiBook.getBooksList(bookName.value)
            } catch (e: IOException) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
                FirebaseCrashlytics.getInstance().sendUnsentReports()
                emit(Result.Error(message = "Error loading books"))
                return@flow
            } catch (e: HttpException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                FirebaseCrashlytics.getInstance().sendUnsentReports()
                e.printStackTrace()
                emit(Result.Error(message = "Error loading books"))
                return@flow
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                FirebaseCrashlytics.getInstance().sendUnsentReports()
                e.printStackTrace()
                emit(Result.Error(message = "Error loading books"))
                return@flow
            }

            emit(Result.Success(booksFromApi.items))
        }
    }
}