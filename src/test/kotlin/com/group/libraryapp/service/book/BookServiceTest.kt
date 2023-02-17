package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) : FunSpec({
    test("책 등록이 정상 동작한다") {
        // given
        val request = BookRequest("이상한 나라의 엘리스", BookType.COMPUTER)

        // when
        bookService.saveBook(request)

        // then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("이상한 나라의 엘리스")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    test("책 대출이 정상 동작한다") {
        // given
        bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("최태현", null))
        val request = BookLoanRequest("최태현", "이상한 나라의 엘리스")

        // when
        bookService.loanBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("이상한 나라의 엘리스")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].isReturn).isFalse()
    }

    test("책이 진작 대출되어 있다면, 신규 대출이 실패한다") {
        // given
        bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("최태현", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "이상한 나라의 엘리스"))
        val request = BookLoanRequest("최태현", "이상한 나라의 엘리스")

        // when & then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message
        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    test("책 반납이 정상 동작한다") {
        // given
        val savedUser = userRepository.save(User("최태현", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "이상한 나라의 엘리스"))
        val request = BookReturnRequest("최태현", "이상한 나라의 엘리스")

        // when
        bookService.returnBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].isReturn).isTrue()
    }

//  @Test
//  @DisplayName("책 대여 권수를 정상 확인한다")
//  fun countLoanedBookTest() {
//    // given
//    val savedUser = userRepository.save(User("최태현", null))
//    userLoanHistoryRepository.saveAll(listOf(
//      UserLoanHistory.fixture(savedUser, "A"),
//      UserLoanHistory.fixture(savedUser, "B", UserLoanStatus.RETURNED),
//      UserLoanHistory.fixture(savedUser, "C", UserLoanStatus.RETURNED),
//    ))
//
//    // when
//    val result = bookService.countLoanedBook()
//
//    // then
//    assertThat(result).isEqualTo(1)
//  }
//
//  @Test
//  @DisplayName("분야별 책 권수를 정상 확인한다")
//  fun getBookStatisticsTest() {
//    // given
//    bookRepository.saveAll(listOf(
//      Book.fixture("A", BookType.COMPUTER),
//      Book.fixture("B", BookType.COMPUTER),
//      Book.fixture("C", BookType.SCIENCE),
//    ))
//
//    // when
//    val results = bookService.getBookStatistics()
//
//    // then
//    assertThat(results).hasSize(2)
//    assertCount(results, BookType.COMPUTER, 2L)
//    assertCount(results, BookType.SCIENCE, 1L)
//  }
//
//  fun assertCount(results: List<BookStatResponse>, type: BookType, count: Long) {
//    assertThat(results.first { result -> result.type == type }.count).isEqualTo(count)
//  }
}) {
    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }
}

private fun Book.Companion.fixture(
    name: String = "책 이름",
    type: BookType = BookType.COMPUTER,
    id: Long? = null,
): Book {
    return Book(
        name = name,
        type = type,
        id = id,
    )
}
