package io.example.wedzy.data.model

data class TaskTemplate(
    val title: String,
    val description: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val monthsBeforeWedding: Int,
    val estimatedDurationDays: Int = 0
)

object WeddingTaskTemplates {
    val templates = listOf(
        // 12 months before
        TaskTemplate(
            title = "Set wedding budget",
            description = "Determine total budget and allocate to categories",
            category = TaskCategory.BUDGET,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 12
        ),
        TaskTemplate(
            title = "Create guest list draft",
            description = "Start drafting your guest list with family and friends",
            category = TaskCategory.GUEST_MANAGEMENT,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 12
        ),
        TaskTemplate(
            title = "Research and book venue",
            description = "Visit venues and book your ceremony and reception location",
            category = TaskCategory.VENUE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 12
        ),
        TaskTemplate(
            title = "Hire wedding planner (optional)",
            description = "If using a planner, hire them early in the process",
            category = TaskCategory.OTHER,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 12
        ),
        
        // 10-11 months before
        TaskTemplate(
            title = "Book photographer",
            description = "Research and book your wedding photographer",
            category = TaskCategory.PHOTOGRAPHY,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 11
        ),
        TaskTemplate(
            title = "Book videographer",
            description = "Research and book your wedding videographer",
            category = TaskCategory.PHOTOGRAPHY,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 11
        ),
        TaskTemplate(
            title = "Select and book caterer",
            description = "Taste test menus and book your caterer",
            category = TaskCategory.CATERING,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 10
        ),
        TaskTemplate(
            title = "Book entertainment/DJ/Band",
            description = "Research and book ceremony and reception music",
            category = TaskCategory.MUSIC,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 10
        ),
        
        // 9 months before
        TaskTemplate(
            title = "Shop for wedding dress",
            description = "Start shopping for your wedding dress or suit",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 9
        ),
        TaskTemplate(
            title = "Book florist",
            description = "Research and book your wedding florist",
            category = TaskCategory.DECORATION,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 9
        ),
        TaskTemplate(
            title = "Send save-the-dates",
            description = "Design and send save-the-date cards to guests",
            category = TaskCategory.INVITATIONS,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 9
        ),
        
        // 8 months before
        TaskTemplate(
            title = "Book hair and makeup artist",
            description = "Schedule trials and book your beauty team",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 8
        ),
        TaskTemplate(
            title = "Book transportation",
            description = "Arrange transportation for wedding day",
            category = TaskCategory.OTHER,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 8
        ),
        TaskTemplate(
            title = "Register for gifts",
            description = "Create your wedding registry",
            category = TaskCategory.OTHER,
            priority = TaskPriority.LOW,
            monthsBeforeWedding = 8
        ),
        
        // 6-7 months before
        TaskTemplate(
            title = "Order wedding invitations",
            description = "Design and order your wedding invitations",
            category = TaskCategory.INVITATIONS,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 7
        ),
        TaskTemplate(
            title = "Book hotel room blocks",
            description = "Reserve hotel blocks for out-of-town guests",
            category = TaskCategory.GUEST_MANAGEMENT,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 7
        ),
        TaskTemplate(
            title = "Order wedding cake",
            description = "Taste test and order your wedding cake",
            category = TaskCategory.CATERING,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 6
        ),
        TaskTemplate(
            title = "Shop for wedding rings",
            description = "Select and order wedding bands",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 6
        ),
        
        // 4-5 months before
        TaskTemplate(
            title = "Plan rehearsal dinner",
            description = "Book venue and plan rehearsal dinner details",
            category = TaskCategory.CATERING,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 5
        ),
        TaskTemplate(
            title = "Finalize ceremony details",
            description = "Confirm ceremony readings, music, and officiant",
            category = TaskCategory.VENUE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 5
        ),
        TaskTemplate(
            title = "Order wedding favors",
            description = "Select and order guest favors",
            category = TaskCategory.DECORATION,
            priority = TaskPriority.LOW,
            monthsBeforeWedding = 4
        ),
        
        // 3 months before
        TaskTemplate(
            title = "Mail wedding invitations",
            description = "Address and mail invitations to all guests",
            category = TaskCategory.INVITATIONS,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 3
        ),
        TaskTemplate(
            title = "Schedule dress fittings",
            description = "Schedule and attend dress/suit fittings",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 3
        ),
        TaskTemplate(
            title = "Apply for marriage license",
            description = "Research requirements and apply for marriage license",
            category = TaskCategory.OTHER,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 3
        ),
        TaskTemplate(
            title = "Finalize reception details",
            description = "Confirm menu, seating chart, and timeline",
            category = TaskCategory.CATERING,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 3
        ),
        
        // 2 months before
        TaskTemplate(
            title = "Order wedding programs",
            description = "Design and order ceremony programs",
            category = TaskCategory.INVITATIONS,
            priority = TaskPriority.LOW,
            monthsBeforeWedding = 2
        ),
        TaskTemplate(
            title = "Schedule hair and makeup trial",
            description = "Do trial run for wedding day beauty",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 2
        ),
        TaskTemplate(
            title = "Break in wedding shoes",
            description = "Start wearing wedding shoes to break them in",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.LOW,
            monthsBeforeWedding = 2
        ),
        
        // 1 month before
        TaskTemplate(
            title = "Confirm final guest count",
            description = "Follow up on RSVPs and confirm headcount with vendors",
            category = TaskCategory.GUEST_MANAGEMENT,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 1
        ),
        TaskTemplate(
            title = "Finalize seating chart",
            description = "Create final seating arrangements",
            category = TaskCategory.GUEST_MANAGEMENT,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 1
        ),
        TaskTemplate(
            title = "Confirm all vendor details",
            description = "Reconfirm times, locations, and details with all vendors",
            category = TaskCategory.OTHER,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 1
        ),
        TaskTemplate(
            title = "Write wedding vows (if applicable)",
            description = "Write and practice your personal vows",
            category = TaskCategory.OTHER,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 1
        ),
        TaskTemplate(
            title = "Final dress fitting",
            description = "Attend final dress/suit fitting",
            category = TaskCategory.ATTIRE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 1
        ),
        
        // 2-3 weeks before
        TaskTemplate(
            title = "Prepare wedding day emergency kit",
            description = "Pack emergency kit with essentials (safety pins, stain remover, etc.)",
            category = TaskCategory.OTHER,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 0,
            estimatedDurationDays = 21
        ),
        TaskTemplate(
            title = "Confirm honeymoon plans",
            description = "Finalize honeymoon reservations and itinerary",
            category = TaskCategory.HONEYMOON,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 0,
            estimatedDurationDays = 21
        ),
        
        // 1 week before
        TaskTemplate(
            title = "Pack for honeymoon",
            description = "Pack bags for honeymoon trip",
            category = TaskCategory.HONEYMOON,
            priority = TaskPriority.MEDIUM,
            monthsBeforeWedding = 0,
            estimatedDurationDays = 7
        ),
        TaskTemplate(
            title = "Rehearsal and rehearsal dinner",
            description = "Attend wedding rehearsal and dinner",
            category = TaskCategory.VENUE,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 0,
            estimatedDurationDays = 2
        ),
        TaskTemplate(
            title = "Prepare vendor payments and tips",
            description = "Prepare envelopes with final payments and gratuities",
            category = TaskCategory.BUDGET,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 0,
            estimatedDurationDays = 7
        ),
        
        // Day before
        TaskTemplate(
            title = "Get good rest",
            description = "Relax, hydrate, and get a good night's sleep",
            category = TaskCategory.OTHER,
            priority = TaskPriority.HIGH,
            monthsBeforeWedding = 0,
            estimatedDurationDays = 1
        )
    )
}
