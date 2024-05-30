package com.pageday.userserver.signup.exception


open class UserServiceException(message: String) : RuntimeException(message)

class PasswordMismatchException : UserServiceException("Passwords do not match")

class UsernameAlreadyExistsException : UserServiceException("Username already exists")

class NicknameAlreadyExistsException : UserServiceException("Nickname already exists")

class EmailAlreadyExistsException : UserServiceException("Email already exists")
