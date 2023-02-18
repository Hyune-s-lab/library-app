package com.group.libraryapp.dto.user.response

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory

data class UserLoanHistoryResponse(
    val name: String, // 유저 이름
    val books: List<BookHistoryResponse>,
) {
    constructor(user: User) : this(
        user.name,
        user.userLoanHistories.map { BookHistoryResponse(it) }
    )
}

data class BookHistoryResponse(
    val name: String, // 책의 이름
    val isReturn: Boolean,
) {
    constructor(history: UserLoanHistory) : this(
        history.bookName,
        history.isReturn
    )
}
