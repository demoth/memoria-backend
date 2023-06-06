package org.dnj.memoria.service

import org.dnj.memoria.ItemRepository
import org.dnj.memoria.SpaceRepository
import org.dnj.memoria.UserRepository
import org.dnj.memoria.model.Item
import org.dnj.memoria.model.Priority
import org.dnj.memoria.model.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ItemServiceTest {
    
    lateinit var service: ItemService
    
    @Mock
    lateinit var itemRepository: ItemRepository
    
    @Mock
    lateinit var userRepository: UserRepository
    
    @Mock
    lateinit var spaceRepository: SpaceRepository 

    @BeforeEach
    fun setup() {
        service = ItemService(itemRepository, userRepository, spaceRepository)
    }
    
    
    @Test
    fun testHetGetBySpace() {

        val testItem = Item("test type", "test item", Status.InProgress, Priority.High)
        `when`(itemRepository.findById(eq("id123"))).thenReturn(Optional.of(testItem))

        val result = service.getItem("id123")
        
        assertEquals(testItem.toDto(), result)
    }
}