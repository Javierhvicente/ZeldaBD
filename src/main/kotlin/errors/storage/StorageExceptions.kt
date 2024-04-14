package org.example.exceptions.storage

sealed class StorageError(val message: String){
    class StoreException(message: String) : StorageError(message)
    class LoadException(message: String) : StorageError(message)
}