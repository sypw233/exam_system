package ccc.sypw.onlineExamSystem.exception

class UserNameAlreadyTakenException(message: String) : RuntimeException(message)
class UserEmailAlreadyExistsException(message: String) : RuntimeException(message)
class InvalidCredentialsException(message: String) : RuntimeException(message)