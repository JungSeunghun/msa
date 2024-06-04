package com.pageday.userserver.common.exception


open class UserServiceException(message: String) : RuntimeException(message)

class PasswordMismatchException : UserServiceException("Passwords do not match")

class UsernameAlreadyExistsException : UserServiceException("Username already exists")

class NicknameAlreadyExistsException : UserServiceException("Nickname already exists")
