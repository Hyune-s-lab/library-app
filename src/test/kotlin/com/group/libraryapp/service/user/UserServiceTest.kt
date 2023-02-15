package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) : FunSpec({
    test("유저 저장이 정상 동작한다") {
        // given
        val request = UserCreateRequest("최태현", null)

        // when
        userService.saveUser(request)

        // then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("최태현")
        assertThat(results[0].age).isNull()
    }

//    test("유저 조회가 정상 동작한다") {
//        // given
//        userRepository.saveAll(
//            listOf(
//                User("A", 20),
//                User("B", null),
//            )
//        )
//
//        // when
//        val results = userService.getUsers()
//
//        // then
//        assertThat(results).hasSize(2) // [UserResponse(), UserResponse()]
//        assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B") // ["A", "B"]
//        assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
//    }
//
//    test("유저 업데이트가 정상 동작한다") {
//        // given
//        val savedUser = userRepository.save(User("A", null))
//        val request = UserUpdateRequest(savedUser.id!!, "B")
//
//        // when
//        userService.updateUserName(request)
//
//        // then
//        val result = userRepository.findAll()[0]
//        assertThat(result.name).isEqualTo("B")
//    }
//
//    test("유저 삭제가 정상 동작한다") {
//        // given
//        userRepository.save(User("A", null))
//
//        // when
//        userService.deleteUser("A")
//
//        // then
//        assertThat(userRepository.findAll()).isEmpty()
//    }
//
//    test("대출 기록이 없는 유저도 응답에 포함된다") {
//        // given
//        userRepository.save(User("A", null))
//
//        // when
//        val results = userService.getUserLoanHistories()
//
//        // then
//        assertThat(results).hasSize(1)
//        assertThat(results[0].name).isEqualTo("A")
//        assertThat(results[0].books).isEmpty()
//    }
//
//    test("대출 기록이 많은 유저의 응답이 정상 동작한다") {
//        // given
//        val savedUser = userRepository.save(User("A", null))
//        userLoanHistoryRepository.saveAll(
//            listOf(
//                UserLoanHistory.fixture(savedUser, "책1", UserLoanStatus.LOANED),
//                UserLoanHistory.fixture(savedUser, "책2", UserLoanStatus.LOANED),
//                UserLoanHistory.fixture(savedUser, "책3", UserLoanStatus.RETURNED),
//            )
//        )
//
//        // when
//        val results = userService.getUserLoanHistories()
//
//        // then
//        assertThat(results).hasSize(1)
//        assertThat(results[0].name).isEqualTo("A")
//        assertThat(results[0].books).hasSize(3)
//        assertThat(results[0].books).extracting("name")
//            .containsExactlyInAnyOrder("책1", "책2", "책3")
//        assertThat(results[0].books).extracting("isReturn")
//            .containsExactlyInAnyOrder(false, false, true)
//    }
}) {
    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        userRepository.deleteAll()
    }
}
