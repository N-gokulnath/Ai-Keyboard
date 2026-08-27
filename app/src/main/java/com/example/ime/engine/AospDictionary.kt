package com.example.ime.engine

class AospDictionary {

    private val commonWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "meeting", "project", "schedule", "tomorrow", "today", "yesterday", "review", "specs",
        "prototype", "design", "team", "update", "sounds", "great", "awesome", "perfect",
        "thanks", "thank", "hello", "please", "confirm", "available", "morning", "afternoon",
        "presentation", "document", "summary", "feedback", "feature", "release", "sprint"
    )

    private val nextWordPredictions = mapOf(
        "sounds" to listOf("great!", "good!", "like a plan."),
        "thank" to listOf("you!", "you very much.", "you for the update."),
        "thanks" to listOf("for your help!", "again!", "for letting me know."),
        "looking" to listOf("forward to it!", "into this now.", "at the specs."),
        "let" to listOf("me know.", "us know.", "me check."),
        "i" to listOf("will", "have", "am", "think", "would"),
        "i'll" to listOf("be there.", "send it over.", "check it out."),
        "we" to listOf("can", "should", "are", "will", "have"),
        "are" to listOf("we", "you", "there", "they"),
        "can" to listOf("you", "we", "I", "it"),
        "see" to listOf("you soon!", "you tomorrow!", "you then!"),
        "have" to listOf("a great day!", "a good weekend!", "been")
    )

    fun getSuggestions(composingWord: String, previousWord: String = ""): List<String> {
        val cleanComposing = composingWord.trim().lowercase()

        if (cleanComposing.isEmpty()) {
            return emptyList()
        }

        val prefixMatches = commonWords.filter { it.startsWith(cleanComposing) }
            .sortedBy { it.length }

        val suggestions = mutableListOf<String>()
        if (!commonWords.contains(cleanComposing)) {
            // Keep user typed word as first choice
            suggestions.add(composingWord)
        }

        suggestions.addAll(prefixMatches)

        if (suggestions.isEmpty()) {
            suggestions.add(composingWord)
        }

        return suggestions.distinct().take(3)
    }
}
