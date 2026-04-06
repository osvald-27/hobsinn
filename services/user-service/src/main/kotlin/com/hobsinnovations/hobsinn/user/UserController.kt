package com.hobsinnovations.hobsinn.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = userService.createUser(request)
        return ResponseEntity.ok(user.toResponse())
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return if (user != null) ResponseEntity.ok(user.toResponse()) else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllUsers(
        @RequestParam(required = false) role: UserRole?,
        @RequestParam(required = false) status: AccountStatus?,
        @RequestParam(required = false) query: String?
    ): ResponseEntity<List<UserResponse>> {
        val users = userService.searchUsers(role, status, query).map(User::toResponse)
        return ResponseEntity.ok(users)
    }

    @GetMapping("/roles")
    fun getRoles(): ResponseEntity<List<UserRole>> {
        return ResponseEntity.ok(UserRole.values().toList())
    }

    @GetMapping("/statuses")
    fun getStatuses(): ResponseEntity<List<AccountStatus>> {
        return ResponseEntity.ok(AccountStatus.values().toList())
    }

    @GetMapping("/summary")
    fun getSummary(): ResponseEntity<UserSummary> {
        return ResponseEntity.ok(userService.getSummary())
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody request: UpdateUserRequest): ResponseEntity<UserResponse> {
        val user = userService.updateUser(id, request)
        return if (user != null) ResponseEntity.ok(user.toResponse()) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = userService.deleteUser(id)
        return if (deleted) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }
}