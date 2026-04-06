package com.hobsinnovations.hobsinn.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<User> {
        val user = userService.createUser(request)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return if (user != null) ResponseEntity.ok(user) else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody request: UpdateUserRequest): ResponseEntity<User> {
        val user = userService.updateUser(id, request)
        return if (user != null) ResponseEntity.ok(user) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = userService.deleteUser(id)
        return if (deleted) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }
}