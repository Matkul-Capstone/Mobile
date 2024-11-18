//package com.example.speaktoo.data.register
//
//import com.example.speaktoo.data.register.model.RegisteredUser
//
///**
// * Class that requests authentication and user information from the remote data source and
// * maintains an in-memory cache of login status and user credentials information.
// */
//
//class LoginRepository(val dataSource: RegisterDataSource) {
//
//    // in-memory cache of the loggedInUser object
//    var user: RegisteredUser? = null
//        private set
//
//    val isLoggedIn: Boolean
//        get() = user != null
//
//    init {
//        // If user credentials will be cached in local storage, it is recommended it be encrypted
//        // @see https://developer.android.com/training/articles/keystore
//        user = null
//    }
//
//    fun logout() {
//        user = null
//        dataSource.logout()
//    }
//
//    fun login(username: String, password: String): Result<RegisteredUser> {
//        // handle login
//        val result = dataSource.login(username, password)
//
//        if (result is Result.Success) {
//            setLoggedInUser(result.data)
//        }
//
//        return result
//    }
//
//    private fun setLoggedInUser(registeredUser: RegisteredUser) {
//        this.user = registeredUser
//        // If user credentials will be cached in local storage, it is recommended it be encrypted
//        // @see https://developer.android.com/training/articles/keystore
//    }
//}