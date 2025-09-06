package de.resume.inventory.management.system.inventoryservice.services.article


fun interface ArticleService {
    fun consume(key: String, payload: String)
}
